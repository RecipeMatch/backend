package org.example.recipe_match_backend.domain.searchhistory.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeResponse;
import org.example.recipe_match_backend.domain.searchhistory.service.SearchHistoryService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    @PostMapping("/recommended")
    public List<RecipeResponse> recommended(@RequestParam String userUid,@RequestParam Boolean userInfo){
        return searchHistoryService.recommended_Recipe(userUid,userInfo);
    }

}
