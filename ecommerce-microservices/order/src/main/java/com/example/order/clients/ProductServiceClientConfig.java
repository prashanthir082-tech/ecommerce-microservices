package com.example.order.clients;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.util.Optional;

@Configuration
public class ProductServiceClientConfig {

//    @Bean
//    @LoadBalanced
//    public RestClient.Builder restClientBuilder() {
//        return RestClient.builder();
//    }
//
//    @Bean
//    public ProductServiceClient restClientInterface(RestClient.Builder builder) {
//        RestClient restClient = builder
//                .defaultStatusHandler(HttpStatusCode::is4xxClientError,(request, response) -> Optional.empty())
//                .baseUrl("http://product-service")
//                .build();
//        RestClientAdapter adapter = RestClientAdapter.create(restClient);
//        HttpServiceProxyFactory factory = HttpServiceProxyFactory
//                .builderFor(adapter)
//                .build();
//
//       return factory.createClient(ProductServiceClient.class);
//    }
// 1. Give your custom load-balanced builder a specific qualifier name
    @Bean("loadBalancedBuilder")
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    // 2. Provide a clean, unintercepted primary bean that Eureka can safely consume
    @Bean
    @Primary
    public RestClient.Builder defaultRestClientBuilder() {
        return RestClient.builder();
    }
    // 3. Explicitly inject your named loadBalancedBuilder here using @Qualifier
    @Bean
    public ProductServiceClient restClientInterface(@Qualifier("loadBalancedBuilder") RestClient.Builder builder) {
        RestClient restClient = builder
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> Optional.empty())
                .baseUrl("http://product-service")
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .build();

        return factory.createClient(ProductServiceClient.class);
    }

}
