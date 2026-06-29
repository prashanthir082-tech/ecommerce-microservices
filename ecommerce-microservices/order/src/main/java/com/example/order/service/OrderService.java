package com.example.order.service;


import com.example.order.dto.OrderResponse;
import com.example.order.model.CartItem;
import com.example.order.model.Order;
import com.example.order.model.OrderItem;
import com.example.order.model.OrderStatus;
import com.example.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final StreamBridge streamBridge;

    @Autowired
    private ModelMapper modelMapper;

    public Optional<OrderResponse> createOrder(String userId) {
        //validate valid user
//        Optional<User> userOpt = userRepository.findById(Integer.valueOf(userId));
//        if(userOpt.isEmpty()){
//            return Optional.empty();
//        }

        //validate cart items
        List<CartItem> cartItems = cartService.getCart(userId);
        if(cartItems.isEmpty()){
            return Optional.empty();
        }

//        User user = userOpt.get();
        //calculate total price
        BigDecimal totalPrice = cartItems.stream()
                .map(cartItem -> cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO,BigDecimal::add);

        //create order
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setTotalAmount(totalPrice);
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> new OrderItem(
                        null,
                        cartItem.getProductId(),
                        cartItem.getQuantity(),
                        cartItem.getPrice(),
                        cartItem.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())),
                        order))
                .toList();
        order.setItems(orderItems);

        //Save order
        Order savedOrder = orderRepository.save(order);

        //clear the cartnotification
        cartService.clearCart(userId);

//        rabbitTemplate.convertAndSend("order.exchange",
//                "order.tracking",
//                Map.of("oderId",savedOrder.getId(),"status","CREATED"));
        String emptyJson = "{}";
        streamBridge.send("createOrder-out-0",emptyJson);

        return Optional.ofNullable(modelMapper.map(savedOrder, OrderResponse.class));
    }
}
