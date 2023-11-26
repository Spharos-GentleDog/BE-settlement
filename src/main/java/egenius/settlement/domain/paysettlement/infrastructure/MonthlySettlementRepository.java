package egenius.settlement.domain.paysettlement.infrastructure;

import egenius.settlement.domain.paysettlement.entity.MonthlySettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MonthlySettlementRepository extends JpaRepository<MonthlySettlement, Long> {
    /**
     * 1. 판매자이메일과 날짜로 조회
     */

    // 1. 판매자이메일과 날짜로 조회
    Optional<MonthlySettlement> findByVendorEmailAndCreatedAtBetween(String vendorEmail, LocalDateTime stt, LocalDateTime end);
}
