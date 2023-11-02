package egenius.settlement.domain.settlement.infrastructure;

import egenius.settlement.domain.settlement.entity.DailySettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DailySettlementRepository extends JpaRepository<DailySettlement, Long> {
}
