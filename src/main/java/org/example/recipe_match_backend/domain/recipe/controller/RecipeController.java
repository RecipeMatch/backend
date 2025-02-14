package org.example.recipe_match_backend.domain.recipe.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeIdAndUserIdRequest;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeRequest;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeUpdateRequest;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeIdAndUserUidResponse;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeAllResponse;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeResponse;
import org.example.recipe_match_backend.domain.recipe.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/recipe")
    public RecipeResponse find(@RequestBody RecipeIdAndUserIdRequest request){
        return recipeService.find(request.getRecipeId(), request.getUserId());
    }

    @GetMapping("/recipeAll")
    public List<RecipeAllResponse> findAll(){
        return recipeService.findAll();
    }

    @PostMapping("/recipe")
    public ResponseEntity<RecipeIdAndUserUidResponse> create(@RequestBody RecipeRequest request){
        return ResponseEntity.ok(recipeService.save(request));
    }

    @PatchMapping("/recipe/{recipeId}")
    public ResponseEntity<RecipeIdAndUserUidResponse> update(@PathVariable Long recipeId, @RequestBody RecipeUpdateRequest request) {
        return ResponseEntity.ok(recipeService.update(recipeId, request));
    }

    @DeleteMapping("/recipe/{recipeId}")
    public void delete(@PathVariable Long recipeId){
        recipeService.delete(recipeId);
    }
}