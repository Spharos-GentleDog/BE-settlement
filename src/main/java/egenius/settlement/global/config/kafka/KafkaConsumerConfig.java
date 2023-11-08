package egenius.settlement.global.config.kafka;

import com.fasterxml.jackson.databind.deser.std.MapDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;

@Configuration
public class KafkaConsumerConfig {

    // yml파일에 설정한 bootstrap 주소
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactoryr() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(createConsumer());
        factory.setConcurrency(5);
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> createConsumer() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configs.put(GROUP_ID_CONFIG, "test1");
        // key, value에 대한 직렬화 설정 -> Byte array, String, Integer Serializer를 사용할 수 있다
        // 메시지를 가져올 파티션의 key를 역직렬화
        configs.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        // value는 메시지라 보면 됨
        configs.put(VALUE_DESERIALIZER_CLASS_CONFIG, MapDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configs);
    }
}
