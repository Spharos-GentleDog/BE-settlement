package egenius.settlement.domain.batch.joblauncher;

import egenius.settlement.global.common.exception.BaseException;
import egenius.settlement.global.common.response.BaseResponse;
import egenius.settlement.global.common.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

//@RestController
//@RequestMapping("/api/v1/settlement/daily/batch")
@Service
@RequiredArgsConstructor
@Slf4j
public class DailySettlementJobLauncher {
    private final JobLauncher jobLauncher;
    private final Job dailyPaymentSaveJob;
    private final Job dailySettlementJob;



    //todo: 스케쥴러로 작동하게 만들기
//    @GetMapping("")
    @Scheduled(cron = "${spring.scheduler.daily_settlement_job_launcher.cron}",
            zone = "${spring.scheduler.daily_settlement_job_launcher.zone}")
//    public BaseResponse paymentDataTransferBatch() {
    public void DailySettlementJobLauncher() {
        // '시작 날짜' = '오늘', '끝 날짜' = '내일'
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
        // jobParams : job을 실행할때 넘겨주고싶은 파라미터 & job을 고유하게 식별하는 역할
        // 중복방지 : jobParams에 addDate만 남겨두면, 키는 일정하고 params도 하루동안은 일정하기에 중복 실행을 방지할 수 있음
        JobParameters jobParameters = new JobParametersBuilder()
                .addLocalDateTime("start", start)
                .addLocalDateTime("end", end)
                .addLong("time",System.currentTimeMillis())
                .toJobParameters();

        // Kafka 데이터를 받아와서, 엔티티를 생성하는 Job
        try {
            JobExecution jobExecution = jobLauncher.run(dailyPaymentSaveJob,jobParameters);
            if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
                log.info("job success");
            } else {
                log.info("job failed");
                log.info("fail code: " + jobExecution.getExitStatus());
            }
        } catch (Exception e) {
            log.error("paymentSaveJob Failed: "+e.getMessage());
            throw new BaseException(BaseResponseStatus.PAYMENT_DATA_SAVE_FAILED);
        }

        // 저장된 결제 데이터로 일일 정산을 하는 Job
        try {
            JobExecution jobExecution = jobLauncher.run(dailySettlementJob, jobParameters);
            if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
                log.info("job success");
            } else {
                log.info("job failed");
                log.info("fail code: " + jobExecution.getExitStatus());
            }
        } catch (Exception e) {
            log.error("dailySettlementJob Failed: "+e.getMessage());
            throw new BaseException(BaseResponseStatus.DAILY_SETTLEMENT_FAILED);
        }
    }

}
