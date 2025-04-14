package org.example.recipe_match_backend.global.api.naverProducts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.recipe_match_backend.global.api.naverProducts.dto.ProductDto;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProductRecommendationResponse {
    private boolean hasMissing;             // 사용자가 레시피 재료 전부 가지고 있는지 아닌지 여부 판단
    private List<ProductDto> products;      // 상품 정보 리스트
}
