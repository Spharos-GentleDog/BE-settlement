package egenius.settlement.domain.settlement.entity.enums;

import egenius.settlement.global.common.enums.BaseEnum;
import egenius.settlement.global.common.enums.BaseEnumConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod implements BaseEnum<String, String> {
    /**
     * 1. 코드 작성
     * 2. field 선언
     * 3. converter 구현
     */

    // 1. 코드 작성
    CARD("C","카드"),
    KAKAO_PAY("K","카카오페이"),
    NAVER_PAY("N","네이버페이"),
    TOSS_PAY("T","토스페이")
    ;

    // 2. field 선언
    private final String code;
    private final String description;

    // 3. converter 구현
    @Converter(autoApply = true)
    static class thisConverter extends BaseEnumConverter<PaymentMethod, String, String> {
        public thisConverter() {
            super(PaymentMethod.class);
        }
    }

}
