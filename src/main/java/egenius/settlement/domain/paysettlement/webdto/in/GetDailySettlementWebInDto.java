package egenius.settlement.domain.paysettlement.webdto.in;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
public class GetDailySettlementWebInDto {

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate start;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;

    public GetDailySettlementWebInDto(LocalDate start, LocalDate end) {
        this.start = start;
        this.end = end;
    }
}
