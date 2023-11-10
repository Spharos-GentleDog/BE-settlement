package egenius.settlement.domain.settlement.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Table(name = "daily_product_settlement")
// 상품별 1일 정산금액
public class DailyProductSettlement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "product_daily_total_amount")
    private Integer productDailyTotalAmount; // 상품별 일일 총 정산금액

    @Column(name = "daily_card_amount")
    private Integer dailyCardAmount; // 상품별 카드 결제금액

    @Column(name = "daily_pay_amount")
    private Integer dailyPayAmount; // 상품별 페이 결제금액 - 카카오, 네이버, 토스 모두 더해서 준다
}
