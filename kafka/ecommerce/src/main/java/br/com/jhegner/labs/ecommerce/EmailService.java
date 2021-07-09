package br.com.jhegner.labs.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class EmailService {

    public static void main(String[] args) {

        var emasilService = new EmailService();
        var service = new KafkaService("ECOMMERCE_SEND_EMAIL", emasilService::parse);
        service.run();
    }

    private void parse(ConsumerRecord<String, String> record) {

        System.out.println("---------------------------------------");
        System.out.println("Enviando email...");
        System.out.println(record.key());
        System.out.println(record.value());
        System.out.println(record.partition());
        System.out.println(record.offset());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Ordem processada");
    }
}
