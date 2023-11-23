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
@Table(name = "daily_product_settlement")
// 상품별 1일 정산금액
public class DailyProductSettlement extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_daily_total_amount")
    private Integer productDailyTotalAmount; // 상품별 일일 총 정산금액

    @Column(name = "daily_card_amount", columnDefinition = "int default 0")
    private Integer dailyCardAmount; // 상품별 카드 결제금액

    @Column(name = "daily_pay_amount", columnDefinition = "int default 0")
    private Integer dailyPayAmount; // 상품별 페이 결제금액 - 카카오, 네이버, 토스 모두 더해서 준다

    @Column(name = "count")
    private Integer count; // 해당 상품 결제 개수

    @Column(name = "main_image_url")
    private String mainImageUrl;


    /**
     * 1. dailyCardAmount 추가
     * 2. dailyPayAmount 추가
     * 3. count 추가
     * 4. totalAmount 추가
     */

    // 1. dailyCardAmount 추가
    public void addDailyCardAmount(int dailyCardAmount) {
        this.dailyCardAmount += dailyCardAmount;
    }

    // 2. dailyPayAmount 추가
    public void addDailyPayAmount(int dailyPayAmount) {
        this.dailyPayAmount += dailyPayAmount;
    }

    // 3. count 추가
    public void addCount(int count) {
        this.count += count;
    }

    // 4. totalAmount 추가
    public void addTotalAmount(int productDailyTotalAmount) {
        this.productDailyTotalAmount += productDailyTotalAmount;
    }
}
