package org.example.recipe_match_backend.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.recipe_match_backend.global.api.naverProducts.dto.ProductDto;
import org.example.recipe_match_backend.global.api.naverProducts.dto.response.NaverShoppingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverShoppingClient {

    @Value("${naver.shopping.client-id}")
    private String clientId;

    @Value("${naver.shopping.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<ProductDto> searchProducts(String keyword, int size) {
        try {
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);         // 이거 진행하면 안되는 이유가 뭘까? 기존에는 UTF-8을 진행해야하는데 음..
            String url = "https://openapi.naver.com/v1/search/shop.json?query=" + keyword + "&display=" + size;

            System.out.println(clientId + " " + clientSecret);

            HttpHeaders headers = new HttpHeaders();
            headers.set("X-Naver-Client-Id", clientId);
            headers.set("X-Naver-Client-Secret", clientSecret);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<NaverShoppingResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    NaverShoppingResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().getItems().stream()
                        .map(item -> ProductDto.builder()
                                .name(item.getTitle().replaceAll("<.*?>", ""))
                                .price(Integer.parseInt(item.getLprice()))
                                .imageUrl(item.getImage())
                                .productUrl(item.getLink())
                                .build())
                        .toList();
            }
        } catch (Exception e) {
            log.error("네이버 ShoppingClient Error: {}", e.getMessage());
        }
        return new ArrayList<>();
    }
}

