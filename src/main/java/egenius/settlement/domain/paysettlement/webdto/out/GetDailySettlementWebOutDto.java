package egenius.settlement.domain.paysettlement.webdto.out;

import egenius.settlement.domain.paysettlement.dtos.DailyProductSettlementDto;
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
public class GetDailySettlementWebOutDto {
    private Integer dailySettlementAmount;

    private Integer dailyCommissionAmount;

    private Integer expectedDailySettlementAmount;

    private SettlementStatus settlementStatus;

    @Builder.Default
    private List<DailyProductSettlementDto> dailyProductSettlementDtoList = new ArrayList<>();

    public void createProductList(List productList) {
        this.dailyProductSettlementDtoList = productList;
    }
}
