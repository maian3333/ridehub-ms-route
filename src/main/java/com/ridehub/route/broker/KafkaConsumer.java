package com.ridehub.route.broker;

import com.ridehub.kafka.broker.GenericKafkaConsumer;
import com.ridehub.kafka.service.KafkaUtilityService;
import org.springframework.stereotype.Component;

@Component("kafkaConsumer")
public class KafkaConsumer extends GenericKafkaConsumer {

    public KafkaConsumer(KafkaUtilityService kafkaUtilityService) {
        super(kafkaUtilityService);
    }

}
