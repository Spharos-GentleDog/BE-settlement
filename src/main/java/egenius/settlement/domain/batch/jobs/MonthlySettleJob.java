package egenius.settlement.domain.batch.jobs;

import egenius.settlement.domain.batch.chunk.MonthlySettlementQuerydslReader;
import egenius.settlement.domain.paysettlement.application.DailySettlementService;
import egenius.settlement.domain.paysettlement.entity.DailyProductSettlement;
import egenius.settlement.domain.paysettlement.entity.DailySettlement;
import egenius.settlement.domain.paysettlement.entity.MonthlySettlement;
import egenius.settlement.domain.paysettlement.infrastructure.MonthlyProductSettlementRepository;
import egenius.settlement.domain.paysettlement.infrastructure.MonthlySettlementRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MonthlySettleJob {

    // spring batch
    private final static int CHUNK_SIZE = 1;
    private final JobRepository jobRepository;
    private final EntityManagerFactory enf;
    private final PlatformTransactionManager transactionManager;
    // reader
    private final MonthlySettlementQuerydslReader monthlySettlementQuerydslReader;
    // service
    private final DailySettlementService dailySettlementService;
    // repository
    private final MonthlySettlementRepository monthlySettlementRepository;

    /**
     * 1. job
     * 2. step
     * 3. processor
     * 4. writer
     */

    // 1. job
    @Bean
    public Job monthlySettlementJob() {
        return new JobBuilder("monthlySettlementJob", jobRepository)
                .start(monthlySettlementStep(jobRepository,null,null))
                .build();
    }

    // 2. step
    @Bean
    @JobScope
    public Step monthlySettlementStep(
            JobRepository jobRepository,
            @Value("#{jobParameters['start']}") LocalDateTime start,
            @Value("#{jobParameters['end']}") LocalDateTime end) {
        return new StepBuilder("monthlySettlementStep", jobRepository)
                .<MonthlySettlement, MonthlySettlement>chunk(CHUNK_SIZE, transactionManager)
                .reader(monthlySettlementQuerydslReader.monthlySettlementReader(null, null))
                .processor(monthlySettlementProcessor())
                .writer(monthlySettlementWriter())
                .build();
    }


    // 3. processor
    @Bean
    public ItemProcessor<MonthlySettlement, MonthlySettlement> monthlySettlementProcessor() {
        return monthlySettlement -> {
            log.info("월말정산 판매자: {}", monthlySettlement.getVendorEmail());
            /**
             * 월간 판매자정산 업데이트
             */
            // 일일정산에서 필요한 값들을 꺼내서 계산을 진행
            List<DailySettlement> dailySettlementList = dailySettlementService
                    .getDailySettlementForMonthlySettlement(monthlySettlement.getVendorEmail());
            dailySettlementList.forEach(dailySettlement -> {
                // 총 수입 계산
                Integer amount = dailySettlement.getDailySettlementAmount();
                monthlySettlement.addSettlementAmount(amount);
                // 총 수수료 계산
                Integer commission = (int) (amount * 0.1);
                monthlySettlement.updateCommissionAndExpectedAmount(commission);
            });

            /**
             * 월간 상품정산 업데이트
             */
            monthlySettlement.getMonthlyProductSettlementList().forEach(
                    product -> {
                        log.info("월말정산 상품: {}",product.getProductCode());
                        // productCode와 정산일에 해당하는 일일 상품정산에서 [정산금액, 카드결제금액, 페이결제금액, 상품결제 개수]를 조회
                        List<DailyProductSettlement> dailyProductList = dailySettlementService
                                .getDailyProductForMonthlySettlement(product.getProductCode());
                        // 업데이트
                        dailyProductList.forEach(dailyProduct->{
                            log.info("일일정산 상품: {}",dailyProduct.getProductCode());
                            product.addTotalAmount(dailyProduct.getProductDailyTotalAmount());
                            product.addMonthlyCardAmount(dailyProduct.getDailyCardAmount());
                            product.addMonthlyPayAmount(dailyProduct.getDailyPayAmount());
                            product.addCount(dailyProduct.getCount());
                        });
                    }
            );


            return monthlySettlement;
        };
    }


    // 4. writer
    @Bean
    public ItemWriter<MonthlySettlement> monthlySettlementWriter() {
        return chunk -> {
            chunk.forEach(monthlySettlement -> {
                monthlySettlementRepository.save(monthlySettlement);
            });
        };
    }




















}
