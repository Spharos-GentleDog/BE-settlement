package egenius.settlement.domain.paysettlement.dtos.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetDailySettlementInDto {
    private LocalDate start;

    private LocalDate end;
}
