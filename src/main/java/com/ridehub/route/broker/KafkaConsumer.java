package com.ridehub.route.broker;

import com.ridehub.common.kafka.broker.GenericKafkaConsumer;
import com.ridehub.common.kafka.service.KafkaUtilityService;
import org.springframework.stereotype.Component;

@Component("kafkaConsumer")
public class KafkaConsumer extends GenericKafkaConsumer {

    public KafkaConsumer(KafkaUtilityService kafkaUtilityService) {
        super(kafkaUtilityService);
    }

}
