package egenius.settlement.domain.paysettlement.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import egenius.settlement.domain.paysettlement.dtos.DailyProductSettlementDto;
import egenius.settlement.domain.paysettlement.dtos.in.GetDailySettlementInDto;
import egenius.settlement.domain.paysettlement.dtos.out.GetDailySettlementOutDto;
import egenius.settlement.domain.paysettlement.entity.*;
import egenius.settlement.domain.paysettlement.entity.enums.PaymentMethod;
import egenius.settlement.domain.paysettlement.entity.enums.SettlementStatus;
import egenius.settlement.domain.paysettlement.infrastructure.DailyProductSettlementRepository;
import egenius.settlement.domain.paysettlement.infrastructure.DailySettlementRepository;
import egenius.settlement.global.common.exception.BaseException;
import egenius.settlement.global.common.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DailySettlementServiceImpl implements DailySettlementService {

    // repository
    private final DailyProductSettlementRepository dailyProductSettlementRepository;
    private final DailySettlementRepository dailySettlementRepository;
    private final JPAQueryFactory jpaQueryFactory;
    // util
    private final ModelMapper modelMapper;


    /**
     * 1. DailySettlement 생성
     * 2. DailyProductSettlement 생성
     * 3. DailySettlement 조회
     * 4. 월간 판매자 정산을 진행할 판매자 조회
     * 5. 월간 상품 정산을 진행할 상품 조회
     */

    // 1. DailySettlement 생성
    @Override
    public DailySettlement createDailySettlement(String vendorEmail, DailyProductSettlement dailyProductSettlement) {
        LocalDateTime today = LocalDate.now().atStartOfDay();
        LocalDateTime tomorrow = LocalDate.now().plusDays(1).atStartOfDay();
        Optional<DailySettlement> findResult = dailySettlementRepository.findByVendorEmailAndCreatedAtBetween(vendorEmail, today, tomorrow);
        DailySettlement dailySettlement = null;
        // 중복확인
        if (findResult.isEmpty()) {
            dailySettlement = DailySettlement.builder()
                    .vendorEmail(vendorEmail)
                    // 상태는 지급 전으로 한다
                    .dailySettlementAmount(0)
                    .dailyCommissionAmount(0)
                    .expectedDailySettlementAmount(0)
                    .settlementStatus(SettlementStatus.PAYMENT_BEFORE)
                    .build();
        }else{
            dailySettlement = findResult.get();
        }

        // 중복검사 -> 중복되지 않았다면 product Settlement를 daily Settlement에 추가
        if (dailySettlement.getDailyProductSettlementList().contains(dailyProductSettlement) == false) {
            dailySettlement.addDailyProductSettlement(dailyProductSettlement);
        }
        return dailySettlement;
    }


    // 2. DailyProductSettlement 생성
    @Override
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


    // 3. DailySettlement 조회
    @Override
    public GetDailySettlementOutDto getDailySettlement(String vendorEmail,
                                                       LocalDate date) {
        LocalDateTime stt = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        // 판매자 아이디 + 해당하는 날짜로 조회한다
        DailySettlement dailySettlement = null;
        Optional<DailySettlement> searchResult = dailySettlementRepository.findByVendorEmailAndCreatedAtBetween(vendorEmail, stt, end);
        if (searchResult.isPresent() == true) {
            dailySettlement = searchResult.get();
        }
        // 조건에 해당하는 일일정산이 없다면 빈값을 return
        else if (searchResult.isEmpty() == true){
            return GetDailySettlementOutDto.builder().build();
        }

        // DailyProductSettlementDto 생성
        List<DailyProductSettlementDto> productList = new ArrayList<>();
        dailySettlement.getDailyProductSettlementList().forEach(data -> {
            DailyProductSettlementDto settlementDto = modelMapper.map(data, DailyProductSettlementDto.class);
            productList.add(settlementDto);
        });

        // return Dto 생성
        GetDailySettlementOutDto outDto = modelMapper.map(dailySettlement, GetDailySettlementOutDto.class);
        outDto.createProductList(productList);
        return outDto;
    }


    // 4. 월간 판매자 정산을 진행할 DailySettlement 조회
    @Override
    public List<DailySettlement> getDailySettlementForMonthlySettlement(String vendorEmail) {
        QDailySettlement qDailySettlement = QDailySettlement.dailySettlement;
        LocalDateTime stt = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime end = YearMonth.now().atDay(30).atStartOfDay();
        // 날짜와 vendorEmail이 일치하는 일일 정산을 조회
        List<DailySettlement> dailySettlementList = jpaQueryFactory
                .selectFrom(qDailySettlement)
                .where(qDailySettlement.createdAt.goe(stt)
                        .and(qDailySettlement.createdAt.lt(end))
                        .and(qDailySettlement.vendorEmail.eq(vendorEmail))
                )
                .fetch();
        return dailySettlementList;
    }

    // 5. 월간 상품 정산을 진행할 DailyProductSettlement 조회
    @Override
    public List<DailyProductSettlement> getDailyProductForMonthlySettlement(String productCode) {
        QDailyProductSettlement qProductSettlement = QDailyProductSettlement.dailyProductSettlement;
        LocalDateTime stt = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();
        LocalDateTime end = YearMonth.now().atDay(30).atStartOfDay();
        // 날짜와 productCode가 일치하는 일일 상품정산을 조회
        List<DailyProductSettlement> productSettlementList = jpaQueryFactory
                .selectFrom(qProductSettlement)
                .where(qProductSettlement.createdAt.goe(stt)
                        .and(qProductSettlement.createdAt.lt(end))
                        .and(qProductSettlement.productCode.eq(productCode))
                )
                .fetch();
        return productSettlementList;
    }
}
