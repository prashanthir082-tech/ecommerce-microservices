package com.ecommerce.notification;

import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import java.util.function.Consumer;

@Service
@Slf4j
public class OrderEventConsumer {

//    @RabbitListener(queues = "${rabbitmq.queue.name}")
//    public void handleOrderEvent(Map<String,Object> orderEvent) {
//        System.out.println("Received Order Event: "+ orderEvent);
//    }

    @Bean
    public Consumer<String>  orderCreated() {
        return event -> {
             log.info("Received Order Created Event");
        };
    }
}
