package org.example.recipe_match_backend.global.coupang.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.example.recipe_match_backend.global.coupang.dto.ProductDto;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProductRecommendationResponse {
    private boolean hasMissing;
    private List<ProductDto> products;
}
