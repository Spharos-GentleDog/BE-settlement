package egenius.settlement.domain.paysettlement.webdto.in;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Getter
public class GetDailySettlementWebInDto {
    private String vendorEmail;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate start;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate end;

    public GetDailySettlementWebInDto(String vendorEmail, LocalDate start, LocalDate end) {
        this.vendorEmail = vendorEmail;
        this.start = start;
        this.end = end;
    }
}
