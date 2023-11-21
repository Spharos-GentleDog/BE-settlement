package egenius.settlement.domain.paysettlement.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import egenius.settlement.domain.paysettlement.entity.*;
import egenius.settlement.domain.paysettlement.entity.enums.PaymentMethod;
import egenius.settlement.domain.paysettlement.entity.enums.SettlementStatus;
import egenius.settlement.domain.paysettlement.infrastructure.BeforeSettlementRepository;
import egenius.settlement.domain.paysettlement.infrastructure.DailyProductSettlementRepository;
import egenius.settlement.domain.paysettlement.infrastructure.DailySettlementListRepository;
import egenius.settlement.domain.paysettlement.infrastructure.DailySettlementRepository;
import egenius.settlement.global.common.exception.BaseException;
import egenius.settlement.global.common.response.BaseResponseStatus;
import egenius.settlement.global.config.kafka.KafkaConsumerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.batch.item.Chunk;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SettlementServiceImpl implements SettlementService {

    // ConsumerConfig를 주입받음
    private final KafkaConsumerConfig consumerConfig;
    private final ObjectMapper objectMapper;
    // repository
    private final DailyProductSettlementRepository dailyProductSettlementRepository;
    private final DailySettlementRepository dailySettlementRepository;
    private final DailySettlementListRepository dailySettlementListRepository;
    private final JPAQueryFactory jpaQueryFactory;
    // util
    private final ModelMapper modelMapper;


    /**
     * 1. DailySettlementList 생성
     * 2. DailyProductSettlement 생성
     * 3. DailySettlement 생성
     */

    // 1. DailySettlementList 생성
    public void createDailySettlementList(
            HashMap<String, String> paymentData,
            DailySettlement dailySettlement,
            DailyProductSettlement dailyProductSettlement) {
        // 들어있는 data :
        //      productAmount, productCode, productMainImageUrl, vendorEmail,
        //      count, paymentMethod, paidAt, productName, key
        DailySettlementList list = DailySettlementList.builder()
                .vendorEmail(paymentData.get("vendorEmail"))
                .dailySettlement(dailySettlement)
                .dailyProductSettlement(dailyProductSettlement)
                .build();
        dailySettlementListRepository.save(list);
    }


    // 2. DailyProductSettlement 생성
    public DailyProductSettlement createDailyProductSettlement(
            String productName,
            String productCode,
            Integer productAmount,
            Integer count,
            String productMainImageUrl,
            PaymentMethod paymentMethod
    ) {
        // 정산 내용 조회 -> 결제날짜가 아닌 생성날짜이므로, 오늘~내일 사이에 생성되어야한다
        LocalDateTime stt = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
        log.info("stt: "+stt+", end: "+end);
        QDailyProductSettlement qDailyProductSettlement = QDailyProductSettlement.dailyProductSettlement;

        // 날짜와 ProductCode로 productSettlement를 조회한다
        DailyProductSettlement todayProductSettlement = null;
        try {
            DailyProductSettlement productSettlement = jpaQueryFactory
                    .selectFrom(qDailyProductSettlement)
                    .where(qDailyProductSettlement.createdAt.goe(stt)
                            .and(qDailyProductSettlement.createdAt.lt(end))
                            .and(qDailyProductSettlement.productCode.eq(productCode)))
                    .fetchOne();
            log.info("product result: "+productSettlement);
            // null이 아니라면 update
            if (productSettlement != null) {
                productSettlement.addCount(count);
                productSettlement.addTotalAmount(count * productAmount);
                todayProductSettlement = productSettlement;
            }
            // null이라면 새로 생성
            else {
                todayProductSettlement = DailyProductSettlement.builder()
                        .productName(productName)
                        .productCode(productCode)
                        .productDailyTotalAmount(productAmount * count)
                        .count(count)
                        .dailyCardAmount(0)
                        .dailyPayAmount(0)
                        .mainImageUrl(productMainImageUrl)
                        .build();
            }
        }
        // 중복이 발생할 경우 exception return
        catch (Exception e) {
            throw new BaseException(BaseResponseStatus.DUPLICATE_DAILY_SETTLEMENT_LIST);
        }

        // 결제 방식에 따라 update
        if (paymentMethod == PaymentMethod.CARD) {
            todayProductSettlement.addDailyCardAmount(productAmount * count);
        } else {
            todayProductSettlement.addDailyPayAmount(productAmount * count);
        }
        return todayProductSettlement;
    }


    // 3. DailySettlement 생성
    public void createDailySettlement(Integer totalAmount, Integer totalCommission, Integer expectedAmount) {
        DailySettlement dailySettlement = DailySettlement.builder()
                // 1일 정산 총 금액
                .dailySettlementAmount(totalAmount)
                // 1일 정산 총 수수료
                .dailyCommissionAmount(totalCommission)
                // 1일 정산 입금 예정액
                .expectedDailySettlementAmount(expectedAmount)
                // 상태는 입금 전으로 한다
                .settlementStatus(SettlementStatus.PAYMENT_BEFORE)
                .build();
        dailySettlementRepository.save(dailySettlement);
    }



}
