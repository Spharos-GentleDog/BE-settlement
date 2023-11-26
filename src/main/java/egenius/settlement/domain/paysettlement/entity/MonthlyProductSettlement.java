package egenius.settlement.domain.paysettlement.entity;

import egenius.settlement.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@ToString
@Table(name = "monthly_product_settlement")
public class MonthlyProductSettlement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_monthly_total_amount")
    private Integer productMonthlyTotalAmount; // 상품별 월별 총 정산금액

    @Column(name = "monthly_card_amount", columnDefinition = "int default 0")
    private Integer monthlyCardAmount; // 상품별 카드 결제금액

    @Column(name = "monthly_pay_amount", columnDefinition = "int default 0")
    private Integer monthlyPayAmount; // 상품별 페이 결제금액 - 카카오, 네이버, 토스 모두 더해서 준다

    @Column(name = "count")
    private Integer count; // 해당 상품 결제 개수

    /**
     * 1. monthlyCardAmount 추가
     * 2. monthlyPayAmount 추가
     * 3. count 추가
     * 4. totalAmount 추가
     */

    // 1. monthlyCardAmount 추가
    public void addMonthlyCardAmount(int monthlyCardAmount) {
        this.monthlyCardAmount += monthlyCardAmount;
    }

    // 2. monthlyPayAmount 추가
    public void addMonthlyPayAmount(int monthlyPayAmount) {
        this.monthlyPayAmount += monthlyPayAmount;
    }

    // 3. count 추가
    public void addCount(int count) {
        this.count += count;
    }

    // 4. totalAmount 추가
    public void addTotalAmount(int productMonthlyTotalAmount) {
        this.productMonthlyTotalAmount += productMonthlyTotalAmount;
    }
}
