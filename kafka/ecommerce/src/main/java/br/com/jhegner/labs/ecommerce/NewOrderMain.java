package br.com.jhegner.labs.ecommerce;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException, TimeoutException {

        try (
                var dispactherOrder = new KafkaDispatcher<Order>();
                var dispactherEmail = new KafkaDispatcher<Email>()
        ) {

            for (var i = 0; i < 10; i++) {

                var userId = UUID.randomUUID().toString();
                var orderId = UUID.randomUUID().toString();
                var ammount = BigDecimal.valueOf(Math.random() * 5000 + 1);
                var newOrder = new Order(userId, orderId, ammount);
                dispactherOrder.send("ECOMMERCE_NEW_ORDER", userId, newOrder);

                var email = new Email("Novo Email de Pedido", "Obrigado pela compra!!!");
                dispactherEmail.send("ECOMMERCE_SEND_EMAIL", userId, email);
            }
        }
    }
}
