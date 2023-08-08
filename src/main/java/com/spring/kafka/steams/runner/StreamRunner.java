package com.spring.kafka.steams.runner;

import com.spring.kafka.steams.domain.KafkaProperties;
import com.spring.kafka.steams.domain.StreamProperties;
import com.spring.kafka.steams.supplier.HeaderTransformerSupplier;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class StreamRunner implements ApplicationRunner {

    @Autowired
    KafkaProperties kafkaConfig;

    @Autowired
    StreamProperties streamConfig;


    @Override
    public void run(ApplicationArguments args) throws Exception {

        Properties streamConfig = getStreamConfig();
        StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> inputStream =  builder.stream(kafkaConfig.getSourceTopic());
        inputStream.transform(new HeaderTransformerSupplier()).to(kafkaConfig.getDestinationTopic());

        Topology headerTransformTopology = builder.build();

        KafkaStreams streams = new KafkaStreams(headerTransformTopology,streamConfig);

        //Starts the stream processing
        streams.start();

        //Shutdowns the stream
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

    }

    private Properties getStreamConfig() {

        Properties streamConfig = new Properties();
        streamConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams_transform_api_app");
        streamConfig.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        streamConfig.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        streamConfig.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        return streamConfig;
    }
}
