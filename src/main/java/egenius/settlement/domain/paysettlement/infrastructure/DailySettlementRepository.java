package egenius.settlement.domain.paysettlement.infrastructure;

import egenius.settlement.domain.paysettlement.entity.DailySettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DailySettlementRepository extends JpaRepository<DailySettlement, Long> {
    /**
     * 1. 판매자 email로 중복검사
     * 2. 판매자 email로 조히
     */

    // 1. 판매자 email로 중복검사
    Boolean existsByVendorEmail(String vendorEmail);

    // 2. 판매자 email로 조회
    Optional<DailySettlement> findByVendorEmail(String vendorEmail);
}
