package org.example.recipe_match_backend.domain.recipe.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeRequest;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeUpdateRequest;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeIdAndUserUidResponse;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeResponse;
import org.example.recipe_match_backend.domain.recipe.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping("/recipe")
    public RecipeResponse find(@RequestParam Long recipeId, @RequestParam String userUid){
        return recipeService.find(recipeId, userUid);
    }

    @GetMapping("/recipeAll")
    public List<RecipeResponse> findAll(){
        return recipeService.findAll();
    }

    @PostMapping("/recipe")
    public ResponseEntity<RecipeIdAndUserUidResponse> create(@RequestPart RecipeRequest request,
                                                             @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

        if (files != null) {
            request.setFiles(files);
        }

        return ResponseEntity.ok(recipeService.save(request));
    }

    @PatchMapping("/recipe/{recipeId}")
    public ResponseEntity<RecipeIdAndUserUidResponse> update(@PathVariable Long recipeId, @RequestPart RecipeUpdateRequest request,
                                                             @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

        if (files != null) {
            request.setFiles(files);
        }

        return ResponseEntity.ok(recipeService.update(recipeId, request));
    }

    @DeleteMapping("/recipe/{recipeId}")
    public void delete(@PathVariable Long recipeId){
        recipeService.delete(recipeId);
    }
}