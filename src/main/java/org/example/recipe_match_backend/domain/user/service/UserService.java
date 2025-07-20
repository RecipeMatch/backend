package org.example.recipe_match_backend.domain.user.service;

import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.ingredient.domain.Ingredient;
import org.example.recipe_match_backend.domain.ingredient.repository.IngredientRepository;
import org.example.recipe_match_backend.domain.post.dto.response.post.PostResponse;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeBookMark;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeImage;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeLike;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeResponse;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeBookMarkRepository;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeLikeRepository;
import org.example.recipe_match_backend.domain.tool.domain.Tool;
import org.example.recipe_match_backend.domain.tool.repository.ToolRepository;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.dto.request.AddInfoRequest;
import org.example.recipe_match_backend.domain.user.dto.request.OAuthRequest;
import org.example.recipe_match_backend.domain.user.dto.request.RefreshRequest;
import org.example.recipe_match_backend.domain.user.dto.response.TokenIncludeNicknameResponse;
import org.example.recipe_match_backend.domain.user.dto.response.TokenResponse;
import org.example.recipe_match_backend.global.exception.login.InvalidTokenException;
import org.example.recipe_match_backend.global.exception.user.UserNotFoundException;
import org.example.recipe_match_backend.global.jwt.JwtTokenProvider;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.global.jwt.redis.TokenService;
import org.example.recipe_match_backend.type.AllergyType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeBookMarkRepository recipeBookMarkRepository;
    private final AmazonS3Client amazonS3Client;
    private final ToolRepository toolRepository;
    private final IngredientRepository ingredientRepository;
    private final TokenService tokenService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    /**
     * 사용자 로그인 (신규 회원, 기존 회원)
     */
    @Transactional
    public TokenIncludeNicknameResponse userLogin(OAuthRequest request) throws Exception{

        // UID 추출
        String uid = request.getUid();

        Optional<User> users = userRepository.findByUid(uid);

        if(users.isPresent()){
            // 기존 회원 로그인
            User user = users.get();
            return ExistingMemberLogin(user);
        } else {
            // 신규 회원 로그인
            return NewMemberLogin(uid);
        }
    }

    /**
     * [기존 회원 로그인]
     * Google 회원 이름이 바뀌었을 경우 회원 정보 갱신
     * accessToken과 refreshToken 발행 후 반환
     */
    private TokenIncludeNicknameResponse ExistingMemberLogin(User user) {

        String accessToken = jwtTokenProvider.createAccessToken(user.getUid());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUid());

        return new TokenIncludeNicknameResponse(accessToken, refreshToken, user.getNickname());
    }

    /**
     * [신규 회원 로그인]
     * uid, name 바탕으로 db에 유저 정보 저장
     * 그러고 나서 accessToken과 refreshToken 발행 후 반환
     */
    private TokenIncludeNicknameResponse NewMemberLogin(String uid) {
        String nickname = "User" + String.format("%010d", (long)(Math.random() * 10000000000L));

        User user = User.builder()
                .uid(uid)
                .nickname(nickname)
                .build();

        userRepository.save(user);

        String accessToken = jwtTokenProvider.createAccessToken(user.getUid());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUid());

        return new TokenIncludeNicknameResponse(accessToken, refreshToken, nickname);
    }

    /**
     * 회원의 추가 정보 저장
     */
    @Transactional
    public void updateInfo(AddInfoRequest request){
        User user = userRepository.findByUid(request.getUid())
                .orElseThrow(UserNotFoundException::new);

        user.updateInfo(request);

        user.getAllergies().clear();
        user.getUserTools().clear();
        user.getUserIngredients().clear();

        // 알레르기 처리
        user.addAllergy(request.getAllergyNames());

        // 도구 처리
        if (request.getToolNames() != null) {
            for (String toolName : request.getToolNames()) {
                Tool tool = toolRepository.findByToolName(toolName)
                        .orElseGet(() -> toolRepository.save(new Tool(toolName)));
                // 편의 메서드 사용
                user.addTool(tool);
            }
        }

        // 재료 처리
        if (request.getIngredientNames() != null) {
            for (String ingredientName : request.getIngredientNames()) {
                Ingredient ingredient = ingredientRepository.findByIngredientName(ingredientName)
                        .orElseGet(() -> ingredientRepository.save(new Ingredient(ingredientName)));
                // 편의 메서드 사용
                user.addIngredient(ingredient);
            }
        }

        userRepository.save(user);
    }

    /**
     * [토근 재발급]
     * refreshToken 검증
     * 검증되었다면 accessToken, refreshToken 재발급
     */
    @Transactional
    public TokenResponse recreateToken(RefreshRequest refreshRequest) {

        if(!jwtTokenProvider.validateRefreshToken(refreshRequest.getRefreshToken())){
            throw new InvalidTokenException();
        }
        String uid = jwtTokenProvider.getUid(refreshRequest.getRefreshToken());

        User user = userRepository.findByUid(uid)
                .orElseThrow(UserNotFoundException::new);

        String accessToken = jwtTokenProvider.createAccessToken(user.getUid());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getUid());

        tokenService.storeRefreshToken(user.getId().toString(), refreshToken);

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * 사용자가 작성한 레시피 목록 조회
     */
    public List<RecipeResponse> getUserRecipes(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(UserNotFoundException::new);

        // user.getRecipes() : 사용자가 작성한 레시피들
        return user.getRecipes().stream()
                .map(recipe -> {
                    // 좋아요 여부, 좋아요 개수
                    boolean recipeLike = recipe.getRecipeLikes().stream()
                            .anyMatch(like -> like.getUser().equals(user));
                    int likeSize = recipe.getRecipeLikes().size();

                    // 즐겨찾기 여부, 즐겨찾기 개수
                    boolean recipeBookMark = recipe.getRecipeFavorites().stream()
                            .anyMatch(bookmark -> bookmark.getUser().equals(user));
                    int bookMarkSize = recipe.getRecipeFavorites().size();

                    List<String> urls = new ArrayList<>();
                    for(RecipeImage recipeImage:recipe.getRecipeImages()){
                        urls.add(""+amazonS3Client.getUrl(bucketName, recipeImage.getToken()));
                    }

                    // Recipe 엔티티 → RecipeResponse DTO 변환
                    return new RecipeResponse(recipe, recipeLike, likeSize, recipeBookMark, bookMarkSize, urls);
                })
                .collect(Collectors.toList());
    }


    /**
     * 사용자가 좋아요 누른 레시피 목록 조회
     */
    public List<RecipeResponse> getUserRecipeLikes(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(UserNotFoundException::new);

        // user가 좋아요 누른 RecipeLike 리스트
        List<RecipeLike> recipeLikes = recipeLikeRepository.findAllByUser(user);

        return recipeLikes.stream()
                .map(recipeLike -> {
                    Recipe recipe = recipeLike.getRecipe();

                    // user가 좋아요 누른 레시피이므로, recipeLike = true
                    boolean recipeLikeBool = true;
                    int likeSize = recipe.getRecipeLikes().size();

                    boolean recipeBookMark = recipe.getRecipeFavorites().stream()
                            .anyMatch(bookmark -> bookmark.getUser().equals(user));
                    int bookMarkSize = recipe.getRecipeFavorites().size();

                    List<String> urls = new ArrayList<>();
                    for(RecipeImage recipeImage:recipe.getRecipeImages()){
                        urls.add(""+amazonS3Client.getUrl(bucketName, recipeImage.getToken()));
                    }

                    return new RecipeResponse(recipe, recipeLikeBool, likeSize, recipeBookMark, bookMarkSize, urls);
                })
                .distinct() // 혹시 중복이 있을 수 있으니 distinct()
                .collect(Collectors.toList());
    }


    /**
     * 사용자가 즐겨찾기 누른 레시피 목록 조회
     */
    public List<RecipeResponse> getUserRecipeBookmarks(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(UserNotFoundException::new);

        // user가 즐겨찾기 누른 RecipeBookMark 리스트
        List<RecipeBookMark> recipeBookmarks = recipeBookMarkRepository.findAllByUser(user);

        return recipeBookmarks.stream()
                .map(bookmark -> {
                    Recipe recipe = bookmark.getRecipe();

                    boolean recipeLike = recipe.getRecipeLikes().stream()
                            .anyMatch(like -> like.getUser().equals(user));
                    int likeSize = recipe.getRecipeLikes().size();

                    // user가 즐겨찾기 누른 레시피이므로, recipeBookMark = true
                    boolean recipeBookMark = true;
                    int bookMarkSize = recipe.getRecipeFavorites().size();

                    List<String> urls = new ArrayList<>();
                    for(RecipeImage recipeImage:recipe.getRecipeImages()){
                        urls.add(""+amazonS3Client.getUrl(bucketName, recipeImage.getToken()));
                    }

                    return new RecipeResponse(recipe, recipeLike, likeSize, recipeBookMark, bookMarkSize, urls);
                })
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 작성한 게시물 조회
     */
    public List<PostResponse> getUserPosts(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(UserNotFoundException::new);

        return user.getPosts().stream()
                .map(PostResponse::from)
                .collect(Collectors.toList());
    }
}
