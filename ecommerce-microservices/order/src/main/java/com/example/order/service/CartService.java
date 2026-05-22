package com.example.order.service;


import com.example.order.dto.CartItemRequest;
import com.example.order.model.CartItem;
import com.example.order.repository.CartItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

//    private final ProductRepository productRepository;
//    private final UserRepository userRepository;
    private final CartItemRepository cartItemRepository;

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
        CartItem existingCartitem = cartItemRepository.findByUserIdAndProductId(Long.valueOf(userId), Long.valueOf(request.getProductId()));
        if(existingCartitem != null){
            //update the quantity and price
            existingCartitem.setQuantity(existingCartitem.getQuantity() + request.getQuantity());
            existingCartitem.setPrice(existingCartitem.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
            cartItemRepository.save(existingCartitem);
        }else{
            // create a new cart item
            CartItem cartItem = new CartItem();
            cartItem.setUserId(Long.valueOf(userId));
            cartItem.setProductId(Long.valueOf(request.getProductId()));
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(BigDecimal.valueOf(1000.00));
            cartItemRepository.save(cartItem);
        }
    return true;
    }


    public boolean deleteItemFromCart(String userId, String productId) {
//        Optional<Product> productOpt = productRepository.findById(Long.valueOf(productId));
//        Optional<User> userOpt = userRepository.findById(Integer.valueOf(userId));
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(Long.valueOf(userId), Long.valueOf(productId));
        if(cartItem != null){
            cartItemRepository.delete(cartItem);
            return true;
        }
    return false;
    }

    public List<CartItem> getCart(String userId) {
        return cartItemRepository.findByUserId(Long.valueOf(userId));

    }

    public void clearCart(String userId) {
        cartItemRepository.deleteByUserId(Long.valueOf(userId));

    }
}
