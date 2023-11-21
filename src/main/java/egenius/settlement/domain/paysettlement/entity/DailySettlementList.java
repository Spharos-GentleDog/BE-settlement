package egenius.settlement.domain.paysettlement.entity;

import egenius.settlement.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @Column(name = "product_code")
    private String productCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_settlement_id", referencedColumnName = "id")
    private DailySettlement dailySettlement;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_product_settlement_id", referencedColumnName = "id")
    private DailyProductSettlement dailyProductSettlement;
}
