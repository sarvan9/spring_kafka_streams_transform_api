package com.spring.kafka.steams.supplier;


import com.spring.kafka.steams.domain.EventHeaders;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.Optional;
import java.util.stream.StreamSupport;

public class HeaderTransformerSupplier implements TransformerSupplier<String, String, KeyValue<String, String>> {

    @Override
    public Transformer<String, String, KeyValue<String, String>> get() {
        return new PoisonMessageTransformer();
    }

    private class PoisonMessageTransformer implements Transformer<String, String, KeyValue<String, String>> {

        ProcessorContext context;

        @Override
        public void init(ProcessorContext context) {
            this.context = context;
        }

        @Override
        public KeyValue<String, String> transform(String key, String value) {
            Headers headers = context.headers();
            Optional<Header> internalHeader = StreamSupport.stream(headers.spliterator(), false).filter(header -> {
                if (EventHeaders.INPUT_HEADER.equals(header.key())) {
                    return true;
                }
                return false;
            }).findFirst();

            if(internalHeader.isPresent()) {
                headers.add(EventHeaders.OUTPUT_HEADER.name(), internalHeader.get().value());
            }
            return KeyValue.pair(key, value);
        }

        @Override
        public void close() {

        }
    }
}
