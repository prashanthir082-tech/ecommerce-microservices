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
public class UserServiceClientConfig {
    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean("pureBuilder")
    public RestClient.Builder primaryRestClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public UserServiceClient userServiceClientInterface(@Qualifier("loadBalancedBuilder") RestClient.Builder builder) {
        RestClient restClient = builder
                .defaultStatusHandler(HttpStatusCode::is4xxClientError, (request, response) -> Optional.empty())
                .baseUrl("http://user-service")
                .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory
                .builderFor(adapter)
                .build();

        return factory.createClient(UserServiceClient.class);
    }
}
