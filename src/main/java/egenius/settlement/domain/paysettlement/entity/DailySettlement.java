package egenius.settlement.domain.paysettlement.entity;

import egenius.settlement.domain.paysettlement.entity.enums.SettlementStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
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

    @Column(name = "vendor_email")
    private String vendorEmail;

    @Column(name = "daily_settlement_amount", columnDefinition = "integer default 0")
    private Integer dailySettlementAmount; //1일 정산 총 금액

    @Column(name = "daily_commission_amount", columnDefinition = "integer default 0")
    private Integer dailyCommissionAmount; //1일 정산 총 수수료

    @Column(name = "expected_daily_settlement_amount", columnDefinition = "integer default 0")
    private Integer expectedDailySettlementAmount; //1일 정산 입금예정액

    // 정산전, 정산후, 추가될걸 고려해서 크기를 5로 설정
    @Column(name = "settlement_status", columnDefinition = "tinyint", length = 5)
    private SettlementStatus settlementStatus;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY)
    private List<DailyProductSettlement> dailyProductSettlementList = new ArrayList<>();


    /**
     * 1. DailyProductSettlement 추가
     * 2. 정산 금액 추가
     * 3. 정산 수수료 및 입금 예정액 계산
     */

    // 1. DailyProductSettlement 추가
    public void addDailyProductSettlement(DailyProductSettlement dailyProductSettlement) {
        this.dailyProductSettlementList.add(dailyProductSettlement);
    }

    // 2. 정산 금액 추가
    public void addSettlementAmount(Integer amount) {
        this.dailySettlementAmount += amount;
    }

    // 3. 정산 수수료 및 입금 예정액 계산
    public void updateCommissionAndExpectedAmount(Integer commission) {
        this.dailyCommissionAmount = commission;
        this.expectedDailySettlementAmount = this.dailySettlementAmount - this.dailyCommissionAmount;
    }



}
