package egenius.settlement.domain.settlement.application;

import org.springframework.kafka.annotation.KafkaListener;

import java.util.Map;
import java.util.Objects;

public interface SettlementService {

    @KafkaListener(topics = "payment_data", groupId = "test1")
    void consume(String message);

}
