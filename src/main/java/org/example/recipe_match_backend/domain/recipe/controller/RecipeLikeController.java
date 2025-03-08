package org.example.recipe_match_backend.domain.recipe.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeIdAndUserIdRequest;
import org.example.recipe_match_backend.domain.recipe.service.RecipeLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeLikeController {

    private final RecipeLikeService recipeLikeService;

    @PostMapping("/like")
    public ResponseEntity<Long> recipeLike(@RequestParam Long recipeId, @RequestParam String userUid){
        return ResponseEntity.ok(recipeLikeService.recipeLike(recipeId,userUid));
    }

}
