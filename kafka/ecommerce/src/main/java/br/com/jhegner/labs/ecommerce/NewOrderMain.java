package br.com.jhegner.labs.ecommerce;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        var producer = new KafkaProducer<String, String>(properties());

        for (var i = 0; i < 100; i++) {

            var key = UUID.randomUUID().toString();

            var newOrder = "123123,231313,64564566";
            var newOrderRecord = new ProducerRecord<>("ECOMMERCE_NEW_ORDER", key, newOrder);

            var email = "Obrigado pela compra!!!";
            var emailRecord = new ProducerRecord<>("ECOMMERCE_SEND_EMAIL", key, email);

            producer.send(newOrderRecord, getCallback()).get();
            producer.send(emailRecord, getCallback()).get(1L, TimeUnit.MINUTES);
        }

    }

    private static Callback getCallback() {
        return (data, ex) -> {
            if (null != ex) {
                ex.printStackTrace();
            }
            System.out.println(
                    "Sucesso enviado:" +
                            "TOPICO:" + data.topic() + "::" +
                            "PARTICAO:" + data.partition() + "::" +
                            "POSICAO:" + data.offset() + "::" +
                            "DATA HORA:" + LocalDateTime.ofInstant(Instant.ofEpochMilli(data.timestamp()), ZoneId.systemDefault())
            );
        };
    }

    private static Properties properties() {

        var properties = new Properties();

        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        return properties;
    }
}
