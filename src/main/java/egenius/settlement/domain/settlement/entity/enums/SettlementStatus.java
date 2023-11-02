package egenius.settlement.domain.settlement.entity.enums;

import egenius.settlement.global.common.enums.BaseEnum;
import egenius.settlement.global.common.enums.BaseEnumConverter;
import jakarta.persistence.Converter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SettlementStatus implements BaseEnum<Integer, String> {
    /**
     * 1. 코드 작성
     * 2. field 선언
     * 3. converter 구현
     */

    // 1. 코드 작성
    PAYMENT_BEFORE(0,"지급 전"),
    PAYMENT_COMPLETED(1, "지급 완료"),
    PAYMENT_PENDING(2, "지급 보류")
    ;

    // 2. field 선언
    private final Integer code;
    private final String description;

    // 3. converter 구현
    @Converter(autoApply = true)
    static class thisConverter extends BaseEnumConverter<SettlementStatus, Integer, String> {
        public thisConverter() {
            super(SettlementStatus.class);
        }
    }
}
