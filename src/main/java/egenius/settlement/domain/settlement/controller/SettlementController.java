package egenius.settlement.domain.settlement.controller;

import egenius.settlement.domain.settlement.application.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/settlement")
@RequiredArgsConstructor
public class SettlementController {
    private final SettlementService settlementService;

    /**
     *
     */

    // kafka 테스트를 위한 컨트롤러, 추후 수정
    @GetMapping("")
    public void testConsume() {
        settlementService.consume(null);
    }


}
