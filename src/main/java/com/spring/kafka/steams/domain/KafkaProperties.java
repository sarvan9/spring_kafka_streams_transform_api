package com.spring.kafka.steams.domain;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class KafkaProperties {

    @Value("${kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${kafka.sourceTopic}")
    private String sourceTopic;

    @Value("${kafka.destinationTopic}")
    private String destinationTopic;

}
