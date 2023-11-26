package egenius.settlement.domain.paysettlement.infrastructure;

import egenius.settlement.domain.paysettlement.entity.MonthlyProductSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MonthlyProductSettlementRepository extends JpaRepository<MonthlyProductSettlement, Long> {
    Optional<MonthlyProductSettlement> findByProductCodeAndCreatedAtBetween(
            String productCode,
            LocalDateTime stt,
            LocalDateTime end);
}
