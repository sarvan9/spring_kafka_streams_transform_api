package com.spring.kafka.steams.publish;

import com.spring.kafka.steams.domain.EventHeaders;
import com.spring.kafka.steams.domain.KafkaProperties;
import com.spring.kafka.steams.kafka.handler.KafkaProducerHandler;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class EventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventPublisher.class);

    @Autowired
    KafkaProducerHandler producerHandler;

    @Autowired
    KafkaProperties kafkaConfig;

    private static final String CONST_INPUT_HEADER_VALUE="INPUT_HEADER_VALUE";

    public void publishEvents(int eventsCount) {

        for (int i = 0; i < eventsCount; i++) {

            UUID key = UUID.randomUUID();

            ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<>(kafkaConfig.getSourceTopic(), key.toString(), "Event" + i);

            String headerValue = CONST_INPUT_HEADER_VALUE+" "+i;
            producerRecord.headers().add(EventHeaders.INPUT_HEADER.name(), headerValue.getBytes(StandardCharsets.UTF_8));

            producerHandler.publishEvent(producerRecord, (metadata, ex) -> {
                if (ex == null) {
                    // the record was successfully sent
                    LOGGER.info("Received new metadata. \n" +
                            "Topic:" + metadata.topic() + "\n" +
                            "Partition: " + metadata.partition() + "\n" +
                            "Offset: " + metadata.offset() + "\n" +
                            "Timestamp: " + metadata.timestamp());
                } else {
                    LOGGER.error("Error while publishing event : {}", ex.getLocalizedMessage());
                }
            });

        }
    }
}
