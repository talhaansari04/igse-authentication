package com.igse.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${services.kafka.groupId}")
    private String groupId;



/*    @Bean
    KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }*/

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        Map<String, Object> prop = new HashMap<>();
        prop.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        prop.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, String.class);
        prop.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, String.class);
       /* prop.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);*/
        return new DefaultKafkaConsumerFactory<>(prop);
    }
}
