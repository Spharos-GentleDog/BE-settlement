package egenius.settlement.domain.settlement.entity;

import egenius.settlement.domain.settlement.entity.enums.SettlementStatus;
import egenius.settlement.global.common.BaseTimeEntity;
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
public class DailySettlement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "daily_settlement_amount")
    private Integer dailySettlementAmount;

    @Column(name = "daily_commission_amount")
    private Integer dailyCommissionAmount;

    @Column(name = "expected_daily_settlement_amount")
    private Integer expectedDailySettlementAmount;

    // 정산전, 정산후, 추가될걸 고려해서 크기를 5로 설정
    @Column(name = "settlement_status", columnDefinition = "tinyint", length = 5)
    private SettlementStatus settlementStatus;

}
