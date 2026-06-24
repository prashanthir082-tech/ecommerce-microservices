package com.example.order.service;


import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.user.dto.UserResponse;
import com.example.order.clients.ProductServiceClient;
import com.example.order.clients.UserServiceClient;
import com.example.order.dto.CartItemRequest;
import com.example.order.model.CartItem;
import com.example.order.repository.CartItemRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

//    private final ProductRepository productRepository;
//    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;

    //@CircuitBreaker(name="productService",fallbackMethod = "addToCartFallBack")
    @Retry(name="retryBreaker",fallbackMethod = "addToCartFallBack")
    public boolean addToCart(String userId, CartItemRequest request) {
//        Optional<Product> productOpt = productRepository.findById(request.getProductId());
//        if(productOpt.isEmpty())
//            return false;
//
//        Product product = productOpt.get();
//        if(product.getStockQuantity() < request.getQuantity())
//            return false;
//
//        Optional<User> userOpt = userRepository.findById(Integer.valueOf(userId));
//        if(userOpt.isEmpty())
//            return false;
//
//        User user = userOpt.get();

       ProductResponse productResponse = productServiceClient.getProductDetails(request.getProductId());
        if(productResponse == null || productResponse.getStockQuantity() < request.getQuantity())
            return false;

        UserResponse userResponse = userServiceClient.getUserDetails(userId);
        if(userResponse == null){
            return false;
        }

        CartItem existingCartitem = cartItemRepository.findByUserIdAndProductId(userId, Long.valueOf(request.getProductId()));
        if(existingCartitem != null){
            //update the quantity and price
            existingCartitem.setQuantity(existingCartitem.getQuantity() + request.getQuantity());
            existingCartitem.setPrice(existingCartitem.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(existingCartitem);
        }else{
            // create a new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUserId(userId);
            cartItem.setProductId(Long.valueOf(request.getProductId()));
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(cartItem);
        }
    return true;
    }

    public boolean addToCartFallBack(String userId, CartItemRequest request,Exception exception) {
        System.out.println("Fallback called");
        return false;
    }


    public boolean deleteItemFromCart(String userId, String productId) {
//        Optional<Product> productOpt = productRepository.findById(Long.valueOf(productId));
//        Optional<User> userOpt = userRepository.findById(Integer.valueOf(userId));
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, Long.valueOf(productId));
        if(cartItem != null){
            cartItemRepository.delete(cartItem);
            return true;
        }
    return false;
    }

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(userId);

    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(userId);

    }
}
