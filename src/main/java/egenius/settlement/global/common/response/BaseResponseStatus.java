package egenius.settlement.global.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum BaseResponseStatus {

    /**
     * 200: 요청 성공
     **/
    SUCCESS(HttpStatus.OK,true, 200, "요청에 성공하였습니다."),

    /**
     * 900: 기타 에러
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, 900, "Internal server error"),

    /**
     * 6000 : Settlement Service Error
     */
    JSON_PARSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, false, 6000, "메시지를 Parsing 할 수 없습니다."),
    DAILY_SETTLEMENT_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, false, 6001, "일일 정산에 실패했습니다"),
    DUPLICATE_DAILY_SETTLEMENT_LIST(HttpStatus.INTERNAL_SERVER_ERROR, false, 6002, "정산 내용이 중복됩니다"),
    PAYMENT_DATA_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, false, 6003, "결제 데이터 저장에 실패했습니다"),
    ;


    private final HttpStatusCode httpStatusCode;
    private final boolean isSuccess;
    private final int code;
    private String message;
}
