package org.example.recipe_match_backend.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeBookMark;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeLike;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipe.RecipeResponse;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeBookMarkRepository;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeLikeRepository;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.dto.request.AddInfoRequest;
import org.example.recipe_match_backend.domain.user.dto.request.OAuthRequest;
import org.example.recipe_match_backend.domain.user.dto.request.RefreshRequest;
import org.example.recipe_match_backend.domain.user.dto.response.TokenIncludeNicknameResponse;
import org.example.recipe_match_backend.domain.user.dto.response.TokenResponse;
import org.example.recipe_match_backend.domain.user.dto.response.UserRecipeResponse;
import org.example.recipe_match_backend.global.exception.login.InvalidTokenException;
import org.example.recipe_match_backend.global.exception.user.UserNotFoundException;
import org.example.recipe_match_backend.global.jwt.JwtTokenProvider;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeBookMarkRepository recipeBookMarkRepository;

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
    public void updateInfo(AddInfoRequest request, User user){
        user.updateInfo(request);
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

        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * 사용자가 작성한 레시피 목록 조회
     */
    public List<UserRecipeResponse> getUserRecipes(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(UserNotFoundException::new);

        return user.getRecipes().stream()
                .map(UserRecipeResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 좋아요 누른 레시피 목록 조회
     */
    public List<UserRecipeResponse> getUserRecipeLikes(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(UserNotFoundException::new);

        List<RecipeLike> recipeLikes = recipeLikeRepository.findAllByUser(user);
        // RecipeLike 엔티티에서 Recipe 를 꺼내서 DTO 로 변환
        return recipeLikes.stream()
                .map(recipeLike -> new UserRecipeResponse(recipeLike.getRecipe()))
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 즐겨찾기 누른 레시피 목록 조회
     */
    public List<UserRecipeResponse> getUserRecipeBookmarks(String uid) {
        User user = userRepository.findByUid(uid)
                .orElseThrow(UserNotFoundException::new);

        List<RecipeBookMark> recipeBookmarks = recipeBookMarkRepository.findAllByUser(user);
        // RecipeBookMark 엔티티에서 Recipe 를 꺼내서 DTO 로 변환
        return recipeBookmarks.stream()
                .map(recipeBookmark -> new UserRecipeResponse(recipeBookmark.getRecipe()))
                .distinct()
                .collect(Collectors.toList());
    }
}
