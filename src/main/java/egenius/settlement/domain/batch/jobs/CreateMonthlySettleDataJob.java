package egenius.settlement.domain.batch.jobs;

import egenius.settlement.domain.batch.chunk.DailySettlementQuerydslReader;
import egenius.settlement.domain.paysettlement.application.DailySettlementServiceImpl;
import egenius.settlement.domain.paysettlement.application.MonthlySettlementServiceImpl;
import egenius.settlement.domain.paysettlement.entity.*;
import egenius.settlement.domain.paysettlement.infrastructure.MonthlyProductSettlementRepository;
import egenius.settlement.domain.paysettlement.infrastructure.MonthlySettlementRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CreateMonthlySettleDataJob {
    // spring batch
    private final static int CHUNK_SIZE = 1;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory enf;
    private final DailySettlementQuerydslReader dailySettlementQuerydslReader;
    // repository
    private final MonthlySettlementRepository monthlySettlementRepository;
    private final MonthlyProductSettlementRepository monthlyProductSettlementRepository;
    // service
    private final DailySettlementServiceImpl dailySettlementService;
    private final MonthlySettlementServiceImpl monthlySettlementService;
    // util

    /**
     * 1. job
     * 2. step
     * 3. writer
     */

    // 1. job
    @Bean
    public Job monthlyPaymentSaveJob() {
        return new JobBuilder("monthlyPaymentSaveJob", jobRepository)
                .start(monthlyPaymentSaveStep(jobRepository, null, null))
                .build();
    }


    // 2. step
    @Bean
    @JobScope
    public Step monthlyPaymentSaveStep(
            JobRepository jobRepository,
            @Value("#{jobParameters['start']}") LocalDateTime start,
            @Value("#{jobParameters['end']}") LocalDateTime end) {
        return new StepBuilder("monthlyPaymentSaveStep", jobRepository)
                .<DailySettlement, DailySettlement>chunk(CHUNK_SIZE, transactionManager)
                .reader(dailySettlementQuerydslReader.dailySettlementReader(start, end))
                .writer(monthlyPaymentSaveWriter())
                .build();
    }


    // 3. writer
    @Bean
    public ItemWriter<DailySettlement> monthlyPaymentSaveWriter() {
        return DailyData-> {
            DailyData.forEach(dailySettlement -> {
                /**
                 * 월간 상품 정산 생성
                 */
                List<MonthlyProductSettlement> monthlyProductSettlementList = new ArrayList<>();
                dailySettlement.getDailyProductSettlementList().forEach(
                        product -> {
                            log.info("writer success : {}", product.getProductCode());
                            // 중복확인후 생성
                            MonthlyProductSettlement productSettlement
                                    = monthlySettlementService.createMonthlyProductSettlement(
                                            product.getProductName(), product.getProductCode());
                            // 리스트에 저장
                            monthlyProductSettlementList.add(productSettlement);
                        }
                );

                /**
                 * 월간 판매자 정산 생성
                 */
                // 중복확인 후 생성 혹은 업데이트
                String vendorEmail = dailySettlement.getVendorEmail();
                MonthlySettlement monthlySettlement = monthlySettlementService.createMonthlySettlement(vendorEmail);
                monthlySettlement.updateMonthlyProductSettlementList(monthlyProductSettlementList);
                // 저장
                monthlySettlementRepository.save(monthlySettlement);
            });
        };
    }




}
