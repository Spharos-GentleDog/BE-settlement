package egenius.settlement.domain.paysettlement.presentation;

import egenius.settlement.domain.paysettlement.application.DailySettlementService;
import egenius.settlement.domain.paysettlement.dtos.in.GetDailySettlementInDto;
import egenius.settlement.domain.paysettlement.dtos.out.GetDailySettlementOutDto;
import egenius.settlement.domain.paysettlement.webdto.in.GetDailySettlementWebInDto;
import egenius.settlement.domain.paysettlement.webdto.out.GetDailySettlementWebOutDto;
import egenius.settlement.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settlement")
@RequiredArgsConstructor
public class SettlementController {

    private final DailySettlementService dailySettlementService;
    private final ModelMapper modelMapper;

    /**
     * 1. 판매자별 일일정산 조회
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



}
