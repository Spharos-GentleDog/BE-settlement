package egenius.settlement.domain.settlement.entity;

import egenius.settlement.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "daily_settlement_list")
public class DailySettlementList extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_email")
    private String vendorEmail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_settlement_id", referencedColumnName = "id")
    private DailySettlement dailySettlement;
}
