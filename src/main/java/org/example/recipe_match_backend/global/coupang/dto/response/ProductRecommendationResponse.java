package org.example.recipe_match_backend.global.coupang.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.example.recipe_match_backend.global.coupang.dto.ProductDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ProductRecommendationResponse {
    private boolean hasMissing;
    private List<ProductDto> products;
}
