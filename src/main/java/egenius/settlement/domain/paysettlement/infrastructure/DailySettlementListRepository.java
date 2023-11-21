package egenius.settlement.domain.paysettlement.infrastructure;

import egenius.settlement.domain.paysettlement.entity.DailySettlementList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DailySettlementListRepository extends JpaRepository<DailySettlementList, Long> {
    List<DailySettlementList> findByCreatedAtBetween(LocalDateTime stt, LocalDateTime end);
}
