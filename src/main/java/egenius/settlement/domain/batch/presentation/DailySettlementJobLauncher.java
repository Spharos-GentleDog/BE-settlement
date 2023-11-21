package egenius.settlement.domain.batch.presentation;

import egenius.settlement.global.common.exception.BaseException;
import egenius.settlement.global.common.response.BaseResponse;
import egenius.settlement.global.common.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/settlement/daily/batch")
@RequiredArgsConstructor
@Slf4j
public class DailySettlementJobLauncher {
    private final JobLauncher jobLauncher;
    private final Job dailyPaymentSaveJob;



    //todo: 스케쥴러로 작동하게 만들기
    @GetMapping("")
    public BaseResponse paymentDataTransferBatch() {
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        // jobParams : job을 실행할때 넘겨주고싶은 파라미터 & job을 고유하게 식별하는 역할
        // 중복방지 : jobParams에 addDate만 남겨두면, 키는 일정하고 params도 하루동안은 일정하기에 중복 실행을 방지할 수 있음
        JobParameters jobParameters = new JobParametersBuilder()
                .addDate("Date", today)
                .addLong("time",System.currentTimeMillis())
                .toJobParameters();
        try {
            JobExecution jobExecution = jobLauncher.run(dailyPaymentSaveJob,jobParameters);
            if (jobExecution.getExitStatus().equals(ExitStatus.COMPLETED)) {
                log.info("job success");
            } else {
                log.info("job failed");
                log.info("fail code: " + jobExecution.getExitStatus());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BaseException(BaseResponseStatus.DAILY_SETTLEMENT_FAILED);
        }
        return new BaseResponse();
    }

}
