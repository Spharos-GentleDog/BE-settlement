package egenius.settlement.domain.paysettlement.application;


import egenius.settlement.domain.paysettlement.dtos.in.GetDailySettlementInDto;
import egenius.settlement.domain.paysettlement.dtos.out.GetDailySettlementOutDto;
import egenius.settlement.domain.paysettlement.entity.DailyProductSettlement;
import egenius.settlement.domain.paysettlement.entity.DailySettlement;
import egenius.settlement.domain.paysettlement.entity.enums.PaymentMethod;

import java.util.List;

public interface DailySettlementService {

    /**
     * 1. DailySettlement 생성
     * 2. DailyProductSettlement 생성
     * 3. DailySettlement 조회
     * 4. 월간 판매자 정산을 진행할 판매자 조회
     * 5. 월간 상품 정산을 진행할 상품 조회
     */


    // 1. DailySettlement 생성
    DailySettlement createDailySettlement(String vendorEmail, DailyProductSettlement dailyProductSettlement);

    // 2. DailyProductSettlement 생성
    DailyProductSettlement createDailyProductSettlement(
            String productName,
            String productCode,
            Integer productAmount,
            Integer count,
            String productMainImageUrl,
            PaymentMethod paymentMethod
    );

    // 3. DailySettlement 조회
    GetDailySettlementOutDto getDailySettlement(GetDailySettlementInDto getDailySettlementInDto);

    // 4. 월간 판매자 정산을 진행할 판매자 조회
    List<String> getMonthlyVendor();

    // 5. 월간 상품 정산을 진행할 상품 조회
    List<String> getMonthlyProduct();
}
