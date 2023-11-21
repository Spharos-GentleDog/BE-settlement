package egenius.settlement.global.config.kafka;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.*;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    // yml파일에 설정한 bootstrap 주소
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(createConsumer());
//        factory.setConcurrency(5);
//        return factory;
//    }
//
//    @Bean
//    public ConsumerFactory<String, String> createConsumer() {
//        Map<String, Object> configs = new HashMap<>();
//        configs.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
//        configs.put(GROUP_ID_CONFIG, "payment_group");
//        // key, value에 대한 직렬화 설정 -> Byte array, String, Integer Serializer를 사용할 수 있다
//        // 메시지를 가져올 파티션의 key를 역직렬화
//        configs.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        // value는 메시지라 보면 됨
//        configs.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(configs);
//    }

    // paymentJob에서 KafkaItemReader를 사용하기 위한 설정
    @Bean
    public Properties dailyPaymentSaveProps() {
        Properties props = new Properties();
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(MAX_POLL_RECORDS_CONFIG, 3);
        return props;
    }
}
