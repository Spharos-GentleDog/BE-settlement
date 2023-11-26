package egenius.settlement.domain.batch.joblauncher;

import egenius.settlement.global.common.exception.BaseException;
import egenius.settlement.global.common.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Service
@Slf4j
@RequiredArgsConstructor
public class MonthlySettlementJobLauncher {
    private final JobLauncher jobLauncher;
    private final Job monthlyPaymentSaveJob;
    private final Job monthlySettlementJob;

    @Scheduled(cron = "${spring.scheduler.create_monthly_settle_data_launcher.cron}",
    zone = "${spring.scheduler.create_monthly_settle_data_launcher.zone}")
    public void MonthlySettlementJobLauncher() {
        // (현재 달 -1)에 해당하는 판매자들을 조회
//        LocalDateTime start = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();
//        LocalDateTime end = YearMonth.now().atDay(2).atStartOfDay();
        LocalDateTime start = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime end = YearMonth.now().plusMonths(1).atDay(2).atStartOfDay();
        log.info("Boundary Condition : start={}, end={}", start, end);
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("start", start)
                .addLocalDateTime("end", end)
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // entity 생성 Job 실행
        try {
            JobExecution jobExecution = jobLauncher.run(monthlyPaymentSaveJob, jobParameters);
            if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
                log.info("job success");
            } else {
                log.info("job failed");
                log.info("fail code: " + jobExecution.getExitStatus());
            }
        } catch (Exception e) {
            log.error("monthlyPaymentSaveJob Failed: "+e.getMessage());
            throw new BaseException(BaseResponseStatus.CREATE_MONTHLY_SETTLEMENT_DATA_FAILED);
        }

        // 정산 Job 실행
        try {
            JobExecution jobExecution = jobLauncher.run(monthlySettlementJob, jobParameters);
            if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
                log.info("job success");
            } else {
                log.info("job failed");
                log.info("fail code: " + jobExecution.getExitStatus());
            }
        }catch (Exception e) {
            log.error("monthlyPaymentSaveJob Failed: "+e.getMessage());
            throw new BaseException(BaseResponseStatus.MONTHLY_SETTLEMENT_FAILED);
        }
    }
}
