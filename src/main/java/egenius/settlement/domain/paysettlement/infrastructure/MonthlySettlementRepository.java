package egenius.settlement.domain.paysettlement.infrastructure;

import egenius.settlement.domain.paysettlement.entity.MonthlySettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MonthlySettlementRepository extends JpaRepository<MonthlySettlement, Long> {
}
