package egenius.settlement.domain.settlement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DailySettlementList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_email")
    private String vendorEmail;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_settlement_id", referencedColumnName = "id")
    private DailySettlement dailySettlement;
}
