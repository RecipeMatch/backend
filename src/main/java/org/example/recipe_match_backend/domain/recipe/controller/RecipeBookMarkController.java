package org.example.recipe_match_backend.domain.recipe.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeIdAndUserIdRequest;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeAllResponse;
import org.example.recipe_match_backend.domain.recipe.service.RecipeBookMarkService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RecipeBookMarkController {

    private final RecipeBookMarkService bookMarkService;

    @PostMapping("/recipe/bookmark")
    public ResponseEntity<Long> recipeBookMark(@RequestBody RecipeIdAndUserIdRequest request){
        return ResponseEntity.ok(bookMarkService.recipeBookMark(request));
    }

    @GetMapping("/bookmark")
    public List<RecipeAllResponse> findAllBookMark(@RequestParam Long userId){
        return bookMarkService.findBookMark(userId);
    }

}
