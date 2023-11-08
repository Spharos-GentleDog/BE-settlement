package egenius.settlement.domain.settlement.entity;

import egenius.settlement.domain.settlement.entity.enums.SettlementStatus;
import egenius.settlement.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class MonthlySettlement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monthly_settlement_amount")
    private Integer monthlySettlementAmount;

    @Column(name = "monthly_commission_amount")
    private Integer monthlyCommissionAmount;

    @Column(name = "expected_monthly_settlement_amount")
    private Integer expectedMonthlySettlementAmount;

    // 정산전, 정산후, 추가될걸 고려해서 크기를 5로 설정
    @Column(name = "settlement_status", columnDefinition = "tinyint", length = 5)
    private SettlementStatus settlementStatus;

    // 언제 날짜부터 정산을 시작했는지
    @Column(name = "settlement_start_day")
    private LocalDate settlementStartDay;

    // 언제 날짜까지 정산을 했는지
    @Column(name = "settlement_end_day")
    private LocalDate settlementEndDay;
}
