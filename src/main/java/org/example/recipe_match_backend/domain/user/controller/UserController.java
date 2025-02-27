package org.example.recipe_match_backend.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeResponse;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.dto.request.AddInfoRequest;
import org.example.recipe_match_backend.domain.user.dto.request.OAuthRequest;
import org.example.recipe_match_backend.domain.user.dto.request.RefreshRequest;
import org.example.recipe_match_backend.domain.user.dto.response.TokenIncludeNicknameResponse;
import org.example.recipe_match_backend.domain.user.dto.response.TokenResponse;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    // 사용자 로그인, uid(email) 전달 받음
    @PostMapping("/login")
    public ResponseEntity<TokenIncludeNicknameResponse> userLogin(@RequestBody OAuthRequest request) throws Exception {
        return ResponseEntity.ok(userService.userLogin(request));
    }

    // 기존 회원 정보 추가 (닉네임, 전화번호)
    @PutMapping("/updateInfo")
    public ResponseEntity<Void> updateInfo(@RequestBody AddInfoRequest request){
        User user = userRepository.findByUid(request.getUid()).get();
        userService.updateInfo(request, user);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // accessToken 재발급 (프론트로부터 refreshToken 전달 받음)
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> recreateToken(@RequestBody RefreshRequest refreshRequest){
        return ResponseEntity.ok(userService.recreateToken(refreshRequest));
    }


    @GetMapping("/recipes")
    public ResponseEntity<List<RecipeResponse>> userRecipes(@RequestParam("uid") String uid) {
        List<RecipeResponse> userRecipes = userService.getUserRecipes(uid);
        return ResponseEntity.ok(userRecipes);
    }

    // 사용자가 좋아요 누른 레시피 목록 조회 (uid를 쿼리 파라미터로 전달)
    @GetMapping("/recipesLike")
    public ResponseEntity<List<RecipeResponse>> recipeLikes(@RequestParam("uid") String uid) {
        List<RecipeResponse> recipeLikes = userService.getUserRecipeLikes(uid);
        return ResponseEntity.ok(recipeLikes);
    }

    // 사용자가 즐겨찾기 누른 레시피 목록 조회 (uid를 쿼리 파라미터로 전달)
    @GetMapping("/recipesBookmark")
    public ResponseEntity<List<RecipeResponse>> recipeBookmarks(@RequestParam("uid") String uid) {
        List<RecipeResponse> recipeBookmarks = userService.getUserRecipeBookmarks(uid);
        return ResponseEntity.ok(recipeBookmarks);
    }

}
