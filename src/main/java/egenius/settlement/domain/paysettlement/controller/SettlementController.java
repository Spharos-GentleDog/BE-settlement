package egenius.settlement.domain.paysettlement.controller;

import egenius.settlement.domain.paysettlement.application.SettlementService;
import egenius.settlement.domain.paysettlement.dtos.out.GetDailySettlementOutDto;
import egenius.settlement.domain.paysettlement.webdto.out.GetDailySettlementWebOutDto;
import egenius.settlement.global.common.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settlement")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;
    private final ModelMapper modelMapper;

    /**
     * 1. 판매자별 일일정산 조회
     */

    // 1. 판매자별 일일정산 조회
    @Operation(summary = "일일 정산 조회", description = "판매자별 일일 정산 조회", tags = {"Daily Settlement"})
    @GetMapping("/daily")
    public BaseResponse<?> getDailySettlement(@RequestParam String vendorEmail) {
        GetDailySettlementOutDto outDto = settlementService.getDailySettlement(vendorEmail);
        GetDailySettlementWebOutDto webOutDto = modelMapper.map(outDto, GetDailySettlementWebOutDto.class);
        return new BaseResponse<>(webOutDto);
    }



}
