package egenius.settlement.domain.settlement.entity;

import egenius.settlement.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "monthly_settlement_list")
public class MonthlySettlementList extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_email")
    private String vendorEmail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "monthly_settlement_id", referencedColumnName = "id")
    private MonthlySettlement monthlySettlement;
}
