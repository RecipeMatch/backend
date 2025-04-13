package org.example.recipe_match_backend.global.api.naverProducts.dto.response;

import lombok.Data;
import org.example.recipe_match_backend.global.api.naverProducts.dto.ItemDto;

import java.util.List;

@Data
public class NaverShoppingResponse {
    private List<ItemDto> items;
}
