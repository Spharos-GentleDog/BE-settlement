package egenius.settlement.domain.settlement.application;

import egenius.settlement.global.config.kafka.KafkaConsumerConfig;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class SettlementServiceImpl implements SettlementService{

    // ConsumerConfig를 주입받음
    private final KafkaConsumerConfig consumerConfig;

    @Override
    public void consume() {
        // config에서 정의한 createConsumer로 consumer를 생성
        KafkaConsumer<String, String> consumer = consumerConfig.createConsumer();
        // 정보를 가져올 topic을 subscribe
        consumer.subscribe(Arrays.asList("payment_data"));

        int result=0;
        while (true) {
            // poll에서 설정한 ms동안 데이터를 기다린다 = 설정한 값동안 기다리다가 다음 코드를 실행한다
            // 만약 그 시간동안 데이터가 오지 않는다면 -> 빈 records를 반환, 데이터가 있다면 데이터 records를 반환
            ConsumerRecords<String, String> records = consumer.poll(10000000);
            // records는 데이터 list이므로, record로 뽑아낸 후 record.value()로 프로듀서가 보낸 진짜 값을 가져올 수 있다
            for (ConsumerRecord<String, String> record : records) {
                // record.value()를 통해 데이터를 가져올 수 있다
                result += Integer.parseInt(record.value());
                System.out.println("record.value() = " + record.value()+", result = "+result);
            }
            System.out.println("final result = " + result);
            break;
        }
    }
}
