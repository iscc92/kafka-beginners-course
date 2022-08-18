package io.kafka.beginners.demo;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class ProducerDemoWithCallback {

    private static final Logger log = LoggerFactory.getLogger(ProducerDemoWithCallback.class.getSimpleName());

    public static void main(String[] args) {
        log.info("KAFKA PRODUCER");

        //create Producer Properties
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());

        // create the Producer
        KafkaProducer<String,String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i < 10; i++) {

            // create a Producer record
            ProducerRecord<String,String> producerRecord =
                    new ProducerRecord<>("demo_java","Hello World!" + 1);

            // send Data - asynchronous operation
            producer.send(producerRecord, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    // execute every time a record is successfully sent or an exception is thrown
                    if (exception == null) {
                        log.info("Received new metadata/ \n" +
                                "Topic: " + metadata.topic() + "\n" +
                                "Partition: " + metadata.partition() + "\n" +
                                "Offset: " + metadata.offset() + "\n" +
                                "Timestamp: " + metadata.timestamp());
                    } else {
                        log.error("Error while producing", exception);
                    }
                }
            });

            //to avoid StickyPartitioner behaviour (sending all messages to same partition for performance)
            try {
                Thread.sleep(1000);
            } catch(InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        // flush data - asynch
        producer.flush();

        // flush and close producer
        producer.close();
    }

}
