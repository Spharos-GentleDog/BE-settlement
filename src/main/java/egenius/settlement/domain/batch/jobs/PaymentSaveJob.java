package egenius.settlement.domain.batch.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import egenius.settlement.domain.paysettlement.application.SettlementServiceImpl;
import egenius.settlement.domain.paysettlement.entity.*;
import egenius.settlement.domain.paysettlement.entity.enums.PaymentMethod;
import egenius.settlement.domain.paysettlement.infrastructure.DailyProductSettlementRepository;
import egenius.settlement.domain.paysettlement.infrastructure.DailySettlementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.kafka.KafkaItemReader;
import org.springframework.batch.item.kafka.builder.KafkaItemReaderBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class PaymentSaveJob {

    // spring batch
    private final static int CHUNK_SIZE = 1;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    // kafka
    private final Properties dailyPaymentSaveProps;
    // repository
    private final DailySettlementRepository dailySettlementRepository;
    private final DailyProductSettlementRepository dailyProductSettlementRepository;
    // service
    private final SettlementServiceImpl settlementService;
    // util
    private final ObjectMapper objectMapper;
    // yml파일에 설정한 bootstrap 주소
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;


    /**
     * 1. job
     * 2. step
     * 3. reader
     * 4. processor
     * 5. writer
     */


    // 1. job
    @Bean
    public Job dailyPaymentSaveJob() {
        return new JobBuilder("dailyPaymentSaveJob", jobRepository)
                .start(dailyPaymentSaveStep(jobRepository))
                .build();
    }

    // 2. step
    @Bean
    public Step dailyPaymentSaveStep(JobRepository jobRepository) {
        return new StepBuilder("dailyPaymentSaveStep", jobRepository)
                .<String, List>chunk(CHUNK_SIZE, transactionManager)
                .reader(kafkaItemReader())
                .processor(processor())
                .writer(writer())
                .build();
    }


    // 3. reader
    @Bean
    public KafkaItemReader<String, String> kafkaItemReader() {
        // kafka consumer
        List<Integer> partitions = Arrays.asList(0, 1, 2, 3, 4);
        KafkaItemReader<String, String> dailyPaymentSaveItemReader = new KafkaItemReaderBuilder<String, String>()
                .partitions(partitions)
                .partitionOffsets(new HashMap<>()) //모름
                .consumerProperties(dailyPaymentSaveProps)
                .name("dailyPaymentSaveItemReader")
                .saveState(true) // 모름
                .pollTimeout(Duration.ofSeconds(1L))
                .topic("payment_data")
                .build();
        return dailyPaymentSaveItemReader;
    }


    // 4. processor
    // -> 판매자별 총 정산금액과, 상품별 정산내용을 구해서 넘겨야함
    @Bean
    public ItemProcessor<String, List> processor() {
        // 정산 내용 조회
        LocalDateTime stt = LocalDate.now().minusDays(1).atStartOfDay();
        LocalDateTime end = LocalDate.now().atStartOfDay();

        // processor 실행
        return message -> {
            log.info("processor 실행: "+ message);

            /**
             * 전처리 과정 : 데이터 타입 변환, 예외처리
             */
            // json(string) 형태로 넘어온 카프카 메시지를 HashMap형태로 바꿈
            HashMap<String, String> kafkaData = objectMapper.readValue(message, HashMap.class);

            // 상품별 정보를 생성
            String productName = kafkaData.get("productName");
            String productCode = kafkaData.get("productCode");
            Integer productAmount = Integer.parseInt(kafkaData.get("productAmount"));
            Integer count = Integer.parseInt(kafkaData.get("count"));
            String productMainImageUrl = kafkaData.get("productMainImageUrl");
            String vendorEmail = kafkaData.get("vendorEmail");
            PaymentMethod paymentMethod = PaymentMethod.valueOf(kafkaData.get("paymentMethod"));
            LocalDateTime paidAt = LocalDateTime.parse(kafkaData.get("paidAt"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));

            // 정산 날짜가 잘못되었다면 null을 반환
            if (paidAt.isBefore(stt) || paidAt.isAfter(end)) {
                return null;
            }

            // 상품별 정보를 List로 return
            List productData = Arrays.asList(
                    productName,
                    productCode,
                    productAmount,
                    count,
                    productMainImageUrl,
                    paymentMethod,
                    vendorEmail);
            return productData;
        };
    }


    // 5. writer
    // -> DailyProductSettlement를 업데이트 & 저장
    // -> DailyProductSettlement의 amount로 판매자별 일일 총 정산금액을 구해서 DailySettlement를 생성
    @Bean
    public ItemWriter<List> writer() {
        return chunk ->{
            chunk.forEach(productData->{
                // productData는 processor에서 넘긴 list이다
                /**
                 * DailyProductSettlement 생성/업데이트
                 */
                DailyProductSettlement dailyProductSettlement = settlementService.createDailyProductSettlement(
                        (String) productData.get(0),
                        (String) productData.get(1),
                        (Integer) productData.get(2),
                        (Integer) productData.get(3),
                        (String) productData.get(4),
                        (PaymentMethod) productData.get(5));
                dailyProductSettlementRepository.save(dailyProductSettlement);

                /**
                 * DailySettlement 생성
                 */
                String vendorEmail = (String) productData.get(6);
                // 중복검사
                DailySettlement dailySettlement = settlementService.createDailySettlement(
                            (String) productData.get(6),
                            dailyProductSettlement);
                dailySettlementRepository.save(dailySettlement);
            });
        };
    }



}
