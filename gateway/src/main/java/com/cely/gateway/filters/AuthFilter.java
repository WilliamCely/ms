package com.cely.gateway.filters;

import com.cely.gateway.dtos.TokenDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthFilter implements GatewayFilter {

    private final WebClient webClient;
    @Value("${AUTH_VALIDATE_URI:http://ms-auth-server:3031/auth-server/auth/jwt}")
    private String authValidateUri;
    private static final String ACCESS_TOKEN_HEADER_NAME = "accessToken";

    public AuthFilter(){
        this.webClient = WebClient.builder().build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        if (!exchange.getRequest().getHeaders().containsHeader(HttpHeaders.AUTHORIZATION)) {
            return this.onError(exchange);
        }

        final var tokenHeder = exchange
                .getRequest()
                .getHeaders()
                .get(HttpHeaders.AUTHORIZATION).get(0);

        final  var chunks = tokenHeder.split(" ");

        if (chunks.length != 2 || !chunks[0].equals("Bearer")) {
            return this.onError(exchange);
        }

        final var token = chunks[1];

        return webClient
                .post()
            .uri(authValidateUri)
                .header(ACCESS_TOKEN_HEADER_NAME, token)
                .retrieve()
                .bodyToMono(TokenDto.class)
                .map(response->exchange)
                .flatMap(chain::filter);
    }

    private Mono<Void> onError(ServerWebExchange exchange) {
        final ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.BAD_REQUEST);
        return response.setComplete();
    }
}
