package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    @Autowired
    private ModelMapper modelMapper;

    public ProductResponse createProduct(ProductRequest productRequest) {
        Product product = modelMapper.map(productRequest, Product.class);
        Product savedProduct = productRepository.save(product);
        return modelMapper.map(savedProduct, ProductResponse.class);
    }

    public Optional<ProductResponse> updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    modelMapper.map(productRequest, existingProduct);
                    Product savedProduct = productRepository.save(existingProduct);
                    return modelMapper.map(savedProduct, ProductResponse.class);
                });
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());
    }

    public void deleteProduct(Long id) {
        productRepository.findById(id)
                .map(product -> {
                    product.setActive(false);
                    productRepository.save(product);
                    return null;
                });
    }

    public Optional<ProductResponse> getProduct(Long id) {
        return productRepository.findByIdAndActiveTrue(id)
                .map(product -> modelMapper.map(product, ProductResponse.class));
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.findAll().stream()
                .filter(Product::isActive)
                .filter(product -> product.getStockQuantity() > 0)
                .filter(product -> product.getName().toLowerCase().contains(keyword.toLowerCase()))
                .map(product -> modelMapper.map(product, ProductResponse.class))
                .collect(Collectors.toList());

    }
}
