package com.spring.kafka.steams.domain;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class StreamProperties {

    @Value("${kafka.bootstrapServers}")
    private String applicationId;
}
