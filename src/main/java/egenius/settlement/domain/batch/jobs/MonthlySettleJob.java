//package egenius.settlement.domain.batch.jobs;
//
//import egenius.settlement.domain.batch.chunk.QuerydslPagingItemReader;
//import egenius.settlement.domain.paysettlement.entity.DailySettlement;
//import egenius.settlement.domain.paysettlement.entity.MonthlySettlement;
//import egenius.settlement.domain.paysettlement.entity.QDailySettlement;
//import egenius.settlement.domain.paysettlement.infrastructure.MonthlySettlementRepository;
//import jakarta.persistence.EntityManagerFactory;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.batch.core.Job;
//import org.springframework.batch.core.Step;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.item.ItemProcessor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//
//import java.time.LocalDateTime;
//import java.time.YearMonth;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//@Slf4j
//@Configuration
//@RequiredArgsConstructor
//public class MonthlySettleJob {
//
//    // spring batch
//    private final static int CHUNK_SIZE = 1;
//    private final JobRepository jobRepository;
//    private final EntityManagerFactory enf;
//    private final PlatformTransactionManager transactionManager;
//    // repository
//    private final MonthlySettlementRepository monthlySettlementRepository;
//
//    /**
//     * 1. job
//     * 2. step
//     * 3. reader
//     * 4. processor
//     * 5. writer
//     */
//
//    // 1. job
//    @Bean
//    public Job monthlySettlementJob() {
//        return new JobBuilder("monthlySettlementJob", jobRepository)
//                .start()
//    }
//
//    // 2. step
//    @Bean
//    public Step monthlySettlementStep(JobRepository jobRepository) {
//
//    }
//
//    // 3. reader
//    @Bean
//    public QuerydslPagingItemReader<DailySettlement> monthlySettlementReader() {
//        // '시작 날짜' = '저번달 2일', '끝 날짜' = '이번달 2일' -> 이번달1일에 저번달 31일 정산이 진행되기 때문
//        LocalDateTime start = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();
//        LocalDateTime end = YearMonth.now().atDay(2).atStartOfDay();
//
//        // 정산해야할 일일 정산내역을 모두 가져옴
//        QDailySettlement qDailySettlement = QDailySettlement.dailySettlement;
//        QuerydslPagingItemReader<DailySettlement> reader = new QuerydslPagingItemReader<>(
//                enf,
//                CHUNK_SIZE,
//                queryFactory -> queryFactory
//                        .selectFrom(qDailySettlement)
//                        .where(qDailySettlement.createdAt.goe(start)
//                                .and(qDailySettlement.createdAt.lt(end)))
//        );
//        reader.setPageSize(CHUNK_SIZE);
//        return reader;
//    }
//
//    // 4. processor
//    @Bean
//    public ItemProcessor<DailySettlement, MonthlySettlement> monthlySettlementProcessor() {
//        // 0번 -> 판매자 관련 정산정보, 1번 -> 상품 관련 정산정보
//        List<HashMap<String, List>> returnData = new ArrayList<>();
//
//        // 일일정산에서 필요한 값들을 꺼내서 계산을 진행
//        return settlementData -> {
//            HashMap<String, List> vendorDataMap = returnData.get(0);
//            HashMap<String, List> productDataMap = returnData.get(1);
//
//            // 판매자에 관련된 정보리스트 생성
//            String vendorEmail = settlementData.getVendorEmail();
//            List vendorDataList = new ArrayList<>();
//            if (vendorDataMap.containsKey(vendorEmail) == true) {
//                vendorDataList = vendorDataMap.get(vendorEmail);
//            }
//
//            // 판매자 정산 관련 데이터
//            Integer dailySettlementAmount = settlementData.getDailySettlementAmount();
//            Integer dailyCommissionAmount = settlementData.getDailyCommissionAmount();
//            Integer expectedDailySettlementAmount = settlementData.getExpectedDailySettlementAmount();
//
//            // 상품에 관련된 정보리스트 생성
//            String productCode = settlementData.getVendorEmail();
//            List vendorDataList = new ArrayList<>();
//            if (vendorDataMap.containsKey(vendorEmail) == true) {
//                vendorDataList = vendorDataMap.get(vendorEmail);
//            }
//
//            return
//        };
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//}
