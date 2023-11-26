package egenius.settlement.domain.paysettlement.application;

import com.querydsl.jpa.impl.JPAQueryFactory;
import egenius.settlement.domain.paysettlement.entity.MonthlyProductSettlement;
import egenius.settlement.domain.paysettlement.entity.MonthlySettlement;
import egenius.settlement.domain.paysettlement.entity.enums.SettlementStatus;
import egenius.settlement.domain.paysettlement.infrastructure.MonthlyProductSettlementRepository;
import egenius.settlement.domain.paysettlement.infrastructure.MonthlySettlementRepository;
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

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MonthlySettlementServiceImpl implements MonthlySettlementService{
    // repository
    private final MonthlySettlementRepository monthlySettlementRepository;
    private final MonthlyProductSettlementRepository monthlyProductSettlementRepository;
    private final JPAQueryFactory queryFactory;
    // util
    private final ModelMapper modelMapper;

    /**
     * 1. MonthlySettlement 생성
     * 2. MonthlyProductSettlement 생성
     * 3. MonthlySettlement 조회
     */

    // 1. MonthlySettlement 생성
    @Override
    public MonthlySettlement createMonthlySettlement(String vendorEmail) {
        // 날짜와 vendorEmail로 조회
        LocalDateTime stt = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
        // 조회
        Optional<MonthlySettlement> getResult =
                monthlySettlementRepository.findByVendorEmailAndCreatedAtBetween(vendorEmail, stt, end);
        // 존재한다면, 존재하는 값을 반환
        if (getResult.isPresent() == true) {
            return getResult.get();
        }
        // 존재하지 않는다면 새로 만듦
        else {
            return MonthlySettlement.builder()
                    .vendorEmail(vendorEmail)
                    .monthlySettlementAmount(0)
                    .monthlyCommissionAmount(0)
                    .expectedMonthlySettlementAmount(0)
                    .settlementStatus(SettlementStatus.PAYMENT_BEFORE)
                    .settlementStartDay(YearMonth.now().minusMonths(1).atDay(1))
                    .settlementEndDay(YearMonth.now().minusMonths(1).atEndOfMonth())
                    .build();
        }
    }

    // 2. MonthlyProductSettlement 생성
    @Override
    @Transactional(readOnly = true)
    public MonthlyProductSettlement createMonthlyProductSettlement(String productName, String productCode) {
        // 날짜와 ProductCode로 조회
        LocalDateTime stt = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
        // 조회
        Optional<MonthlyProductSettlement> getResult =
                monthlyProductSettlementRepository.findByProductCodeAndCreatedAtBetween(productCode,stt,end);
        // 존재한다면, 존재하는 값을 thisMonthProductSettle로 설정
        if (getResult.isPresent() == true) {
            return getResult.get();
        }
        // 존재하지 않는다면 새로 만듦
        else {
            return MonthlyProductSettlement.builder()
                    .productName(productName)
                    .productCode(productCode)
                    .productMonthlyTotalAmount(0)
                    .monthlyCardAmount(0)
                    .monthlyPayAmount(0)
                    .count(0)
                    .build();
        }
    }

    // 3. MonthlySettlement 조회



}
