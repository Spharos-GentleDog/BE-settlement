package egenius.settlement.domain.paysettlement.presentation;

import egenius.settlement.domain.paysettlement.application.DailySettlementService;
import egenius.settlement.domain.paysettlement.application.MonthlySettlementService;
import egenius.settlement.domain.paysettlement.dtos.in.GetDailySettlementInDto;
import egenius.settlement.domain.paysettlement.dtos.out.GetDailySettlementOutDto;
import egenius.settlement.domain.paysettlement.dtos.out.GetMonthlySettlementOutDto;
import egenius.settlement.domain.paysettlement.webdto.in.GetDailySettlementWebInDto;
import egenius.settlement.domain.paysettlement.webdto.out.GetDailySettlementWebOutDto;
import egenius.settlement.domain.paysettlement.webdto.out.GetMonthlySettlementWebOutDto;
import egenius.settlement.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/settlement")
@RequiredArgsConstructor
public class SettlementController {

    private final DailySettlementService dailySettlementService;
    private final MonthlySettlementService monthlySettlementService;
    private final ModelMapper modelMapper;

    /**
     * 1. 판매자별 일일정산 조회
     * 2. 판매자별 월간정산 조회
     */

    // 1. 판매자별 일일정산 조회
    @Operation(summary = "일일 정산 조회", description = "판매자별 일일 정산 조회", tags = {"Daily Settlement"})
    @GetMapping("/daily")
    public BaseResponse<?> getDailySettlement(@RequestHeader String vendorEmail,
                                              GetDailySettlementWebInDto webInDto) {
        GetDailySettlementInDto inDto = modelMapper.map(webInDto, GetDailySettlementInDto.class);
        GetDailySettlementOutDto outDto = dailySettlementService.getDailySettlement(vendorEmail, inDto);
        GetDailySettlementWebOutDto webOutDto = modelMapper.map(outDto, GetDailySettlementWebOutDto.class);
        return new BaseResponse<>(webOutDto);
    }

    // 2. 판매자별 월말정산 조회
    @Operation(summary = "월말 정산 조회", description = "판매자별 월말 정산 조회", tags = {"Monthly Settlement"})
    @GetMapping("/monthly")
    public BaseResponse<?> getMonthlySettlement(@RequestHeader String vendorEmail,
                                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        GetMonthlySettlementOutDto outDto = monthlySettlementService.getMonthlySettlement(vendorEmail, yearMonth);
        GetMonthlySettlementWebOutDto webOutDto = modelMapper.map(outDto, GetMonthlySettlementWebOutDto.class);
        return new BaseResponse<>(webOutDto);
    }
}
