package egenius.settlement.domain.paysettlement.dtos.in;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Getter
public class GetDailySettlementInDto {
    private String vendorEmail;

    private LocalDate start;

    private LocalDate end;
}
