package egenius.settlement.domain.paysettlement.entity;

import egenius.settlement.domain.paysettlement.entity.enums.SettlementStatus;
import egenius.settlement.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "monthly_settlement")
public class MonthlySettlement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vendor_email")
    private String vendorEmail;

    @Column(name = "monthly_settlement_amount")
    private Integer monthlySettlementAmount;

    @Column(name = "monthly_commission_amount")
    private Integer monthlyCommissionAmount;

    @Column(name = "expected_monthly_settlement_amount")
    private Integer expectedMonthlySettlementAmount;

    // 정산전, 정산후, 추가될걸 고려해서 크기를 5로 설정
    @Column(name = "settlement_status", columnDefinition = "tinyint", length = 5)
    private SettlementStatus settlementStatus;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MonthlyProductSettlement> monthlyProductSettlementList = new ArrayList<>();



    /**
     * 1. MonthlyProductSettlement 추가
     * 2. 정산 금액 추가
     * 3. 정산 수수료 및 입금 예정액 계산
     * 4. MonthlyProductSettlementList 업데이트
     */

    // 1. MonthlyProductSettlement 추가
    public void addMonthlyProductSettlement(MonthlyProductSettlement monthlyProductSettlement) {
        this.monthlyProductSettlementList.add(monthlyProductSettlement);
    }

    // 2. 정산 금액 추가
    public void addSettlementAmount(Integer amount) {
        this.monthlySettlementAmount += amount;
    }

    // 3. 정산 수수료 및 입금 예정액 계산 -> 수수료 10%로 계산
    public void updateCommissionAndExpectedAmount(Integer commission) {
        this.monthlyCommissionAmount += commission;
        this.expectedMonthlySettlementAmount = this.monthlySettlementAmount - this.monthlyCommissionAmount;
    }

    // 4. List 업데이트
    public void updateMonthlyProductSettlementList(List<MonthlyProductSettlement> list) {
        this.monthlyProductSettlementList = list;
    }
}
