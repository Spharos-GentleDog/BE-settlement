package egenius.settlement.domain.batch.jobs;

import egenius.settlement.domain.batch.chunk.DailySettlementQuerydslReader;
import egenius.settlement.domain.batch.chunk.QuerydslPagingItemReader;
import egenius.settlement.domain.paysettlement.application.DailySettlementServiceImpl;
import egenius.settlement.domain.paysettlement.entity.DailySettlement;
import egenius.settlement.domain.paysettlement.entity.QDailySettlement;
import egenius.settlement.domain.paysettlement.infrastructure.DailyProductSettlementRepository;
import egenius.settlement.domain.paysettlement.infrastructure.DailySettlementRepository;
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
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DailySettleJob {

    // spring batch
    private final static int CHUNK_SIZE = 1;
    private final JobRepository jobRepository;
    private final EntityManagerFactory enf;
    private final PlatformTransactionManager transactionManager;
    // reader
    private final DailySettlementQuerydslReader dailySettlementQuerydslReader;
    // repository
    private final DailySettlementRepository dailySettlementRepository;
    private final DailyProductSettlementRepository dailyProductSettlementRepository;
    // service
    private final DailySettlementServiceImpl settlementService;
    // util
    // yml

    /**
     * 1. job
     * 2. step
     * 3. processor
     * 4. writer
     */

    // 1. job
    @Bean
//    @JobScope
    public Job dailySettlementJob() {
        return new JobBuilder("dailySettlementJob", jobRepository)
                .start(dailySettlementStep(jobRepository, null, null))
                .build();
    }

    // 2. step
    @Bean
//    @StepScope
    @JobScope
    public Step dailySettlementStep(
            JobRepository jobRepository,
            @Value("#{jobParameters['start']}") LocalDateTime start,
            @Value("#{jobParameters['end']}") LocalDateTime end) {
        return new StepBuilder("dailySettlementStep", jobRepository)
                .<DailySettlement,DailySettlement>chunk(CHUNK_SIZE, transactionManager)
                .reader(dailySettlementQuerydslReader.dailySettlementReader(start, end))
                .processor(dailySettlementProcessor())
                .writer(dailySettlementWriter())
                .build();
    }


    // 4. processor
    @Bean
    public ItemProcessor<DailySettlement, DailySettlement> dailySettlementProcessor() {
        return dailySettlement -> {
            // 판매자의 판매상품 리스트들의 값을 모두 더해주면 됨
            dailySettlement.getDailyProductSettlementList().forEach(
                    product -> {
                        // 일일 정산액을 업데이트한다
                        Integer amount = product.getProductDailyTotalAmount();
                        dailySettlement.addSettlementAmount(amount);
                        // 정산 수수료와 입금 예정액을 계산한다
                        Integer commission = (int) (amount * 0.1);
                        dailySettlement.updateCommissionAndExpectedAmount(commission);
                        log.info("processor result: "+dailySettlement);
                    }
            );
            return dailySettlement;
        };
    }


    //5. writer
    @Bean
    public ItemWriter<DailySettlement> dailySettlementWriter() {
        return chunk -> {
            chunk.forEach(
                dailySettlement -> {
                    dailySettlementRepository.save(dailySettlement);
                    log.info("result: "+dailySettlement);
                }
            );
        };
    }




}
