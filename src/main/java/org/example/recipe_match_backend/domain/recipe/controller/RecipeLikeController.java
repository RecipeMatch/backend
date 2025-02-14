package org.example.recipe_match_backend.domain.recipe.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeIdAndUserIdRequest;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeIdAndUserIdResponse;
import org.example.recipe_match_backend.domain.recipe.service.RecipeLikeService;
import org.example.recipe_match_backend.domain.recipe.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class RecipeLikeController {

    private final RecipeLikeService recipeLikeService;

    @PostMapping("/like")
    public ResponseEntity<Long> recipeLike(@RequestBody RecipeIdAndUserIdRequest request){
        return ResponseEntity.ok(recipeLikeService.recipeLike(request));
    }

}
