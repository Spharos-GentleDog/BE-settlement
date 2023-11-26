package egenius.settlement.domain.batch.chunk;

import egenius.settlement.domain.paysettlement.entity.DailySettlement;
import egenius.settlement.domain.paysettlement.entity.MonthlySettlement;
import egenius.settlement.domain.paysettlement.entity.QDailySettlement;
import egenius.settlement.domain.paysettlement.entity.QMonthlySettlement;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class MonthlySettlementQuerydslReader {
    private final static int CHUNK_SIZE = 1;
    private final EntityManagerFactory enf;

    @Bean
    @StepScope // start, end -> 이부분을 잡런처에서 받아와야할듯?
    public QuerydslPagingItemReader<MonthlySettlement> monthlySettlementReader(
            @Value("#{jobParameters['start']}") LocalDateTime start,
            @Value("#{jobParameters['end']}") LocalDateTime end
    ) {
        // start와 end 사이에 해당하는 모든 dailySettlement를 가져옴
        QMonthlySettlement qMonthlySettlement = QMonthlySettlement.monthlySettlement;
        QuerydslPagingItemReader<MonthlySettlement> reader = new QuerydslPagingItemReader<>(
                enf,
                CHUNK_SIZE,
                queryFactory -> queryFactory
                        .selectFrom(qMonthlySettlement)
                        .where(qMonthlySettlement.createdAt.goe(start)
                                .and(qMonthlySettlement.createdAt.lt(end)))
        );
        reader.setPageSize(CHUNK_SIZE);
        return reader;
    }
}
