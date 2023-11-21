package egenius.settlement.domain.paysettlement.entity;

import egenius.settlement.domain.paysettlement.entity.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "daily_settlement")
public class DailySettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "daily_settlement_amount")
    private Integer dailySettlementAmount; //1일 정산 총 금액

    @Column(name = "daily_commission_amount")
    private Integer dailyCommissionAmount; //1일 정산 총 수수료

    @Column(name = "expected_daily_settlement_amount")
    private Integer expectedDailySettlementAmount; //1일 정산 입금예정액

    // 정산전, 정산후, 추가될걸 고려해서 크기를 5로 설정
    @Column(name = "settlement_status", columnDefinition = "tinyint", length = 5)
    private SettlementStatus settlementStatus;

    @OneToMany(fetch = FetchType.LAZY)
    private List<DailyProductSettlement> dailyProductSettlementList;

}
