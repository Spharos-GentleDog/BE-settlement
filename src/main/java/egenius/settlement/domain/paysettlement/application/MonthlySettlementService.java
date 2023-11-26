package egenius.settlement.domain.paysettlement.application;

import egenius.settlement.domain.paysettlement.dtos.out.GetMonthlySettlementOutDto;
import egenius.settlement.domain.paysettlement.entity.MonthlyProductSettlement;
import egenius.settlement.domain.paysettlement.entity.MonthlySettlement;
import egenius.settlement.domain.paysettlement.entity.enums.PaymentMethod;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

public interface MonthlySettlementService {
    /**
     * 1. MonthlySettlement 생성
     * 2. MonthlyProductSettlement 생성
     * 3. MonthlySettlement 조회
     */

    // 1. MonthlySettlement 생성
    MonthlySettlement createMonthlySettlement(String vendorEmail);

    // 2. MonthlyProductSettlement 생성
    MonthlyProductSettlement createMonthlyProductSettlement(
            String productName,
            String productCode
    );

    // 3. MonthlySettlement 조회
    @Transactional
    GetMonthlySettlementOutDto getMonthlySettlement(String vendorEmail, YearMonth yearMonth);

    // 3. MonthlySettlement 조회

}
