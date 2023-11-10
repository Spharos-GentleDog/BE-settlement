package egenius.settlement.domain.settlement.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import egenius.settlement.global.common.exception.BaseException;
import egenius.settlement.global.common.response.BaseResponseStatus;
import egenius.settlement.global.config.kafka.KafkaConsumerConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService{

    // ConsumerConfig를 주입받음
    private final KafkaConsumerConfig consumerConfig;
    private final ObjectMapper objectMapper;

    @Override
    @KafkaListener(topics = "payment_data", groupId = "test1")
    public void consume(String message) {
        try {
            // String Type으로 통째로 넘어온 Chunk에서, items에 payment 정보가 담겨있으므로
            HashMap<String, Object> kafkaData = objectMapper.readValue(message, HashMap.class);
            ArrayList paymentData = (ArrayList) kafkaData.get("items");
            paymentData.forEach(data->{
                System.out.println("paymentData = " + data);
            });

        } catch (JsonProcessingException e) {
            log.info("error: "+e.getMessage());
            throw new BaseException(BaseResponseStatus.JSON_PARSING_ERROR);
        }

    }

}
