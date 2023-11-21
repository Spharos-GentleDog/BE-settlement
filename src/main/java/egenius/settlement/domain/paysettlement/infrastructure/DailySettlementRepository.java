package egenius.settlement.domain.paysettlement.infrastructure;

import egenius.settlement.domain.paysettlement.entity.DailySettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DailySettlementRepository extends JpaRepository<DailySettlement, Long> {
    /**
     * 1. 판매자 email로 중복검사
     */

    // 1. 판매자 email로 중복검사
    Boolean existsByVendorEmail(String vendorEmail);
}
