package egenius.settlement.domain.paysettlement.infrastructure;

import egenius.settlement.domain.paysettlement.entity.BeforeSettlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeforeSettlementRepository extends JpaRepository<BeforeSettlement, Long> {


}
