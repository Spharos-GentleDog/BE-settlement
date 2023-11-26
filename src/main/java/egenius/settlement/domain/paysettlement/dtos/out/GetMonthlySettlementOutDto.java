package egenius.settlement.domain.paysettlement.dtos.out;

import egenius.settlement.domain.paysettlement.dtos.MonthlyProductSettlementDto;
import egenius.settlement.domain.paysettlement.entity.enums.SettlementStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMonthlySettlementOutDto {
    private Integer monthlySettlementAmount;

    private Integer monthlyCommissionAmount;

    private Integer expectedMonthlySettlementAmount;

    private SettlementStatus settlementStatus;

    @Builder.Default
    private List<MonthlyProductSettlementDto> monthlyProductSettlementDtoList = new ArrayList<>();

    public void createProductList(List productList) {
        this.monthlyProductSettlementDtoList = productList;
    }
}
