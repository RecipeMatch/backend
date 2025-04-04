package org.example.recipe_match_backend.domain.recipe.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeRequest;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeSearchRequest;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeSortRequest;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipe.RecipeUpdateRequest;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeIdAndUserUidResponse;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeResponse;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeSaveResponse;
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
    public ResponseEntity<RecipeSaveResponse> create(@RequestPart RecipeRequest request,
                                                     @RequestPart(value = "files", required = false) List<MultipartFile> files) throws IOException {

        if (files != null) {
            request.setFiles(files);
        }

        return ResponseEntity.ok(recipeService.save(request));
    }

    @PatchMapping("/recipe/{recipeId}")
    public ResponseEntity<RecipeSaveResponse> update(@PathVariable Long recipeId, @RequestPart RecipeUpdateRequest request,
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

    /**
     * -----------------------------------------------------------------------------------------------------------------------------
     * 레시피 좋아요, 즐겨찾기 순으로 정렬 로직
     * [ Request 정보 ]
     * private List<Long> recipeIds;    -> 먼저 사용자가 지정한 부분에 따른 레시피를 먼저 반환하고 그 레시피 ID들을 List 형태로 서버로 보내는 방식
     * private String sortBy;           -> "like" 또는 "bookmark" 중 하나를 선택해서 보내는 방식
     * -----------------------------------------------------------------------------------------------------------------------------
     * [ Response 정보 ]
     * 기존 레시피 정보 방식
     * -----------------------------------------------------------------------------------------------------------------------------
     */
    @PostMapping("/recipe/sort")
    public List<RecipeResponse> sortRecipes(@RequestBody RecipeSortRequest request){
        return recipeService.sortRecipes(request);
    }

    @PostMapping("/recipeSearch")
    public List<RecipeResponse> findSearch(@RequestBody RecipeSearchRequest request) {
        return recipeService.findSearch(request);
    }

}