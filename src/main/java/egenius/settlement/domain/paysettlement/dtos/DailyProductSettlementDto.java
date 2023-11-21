package egenius.settlement.domain.paysettlement.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyProductSettlementDto {
    private String productName;

    private String productCode;

    private Integer productDailyTotalAmount; // 상품별 일일 총 정산금액

    private Integer dailyCardAmount; // 상품별 카드 결제금액

    private Integer dailyPayAmount; // 상품별 페이 결제금액 - 카카오, 네이버, 토스 모두 더해서 준다

    private Integer count; // 해당 상품 결제 개수

    private String mainImageUrl;
}
