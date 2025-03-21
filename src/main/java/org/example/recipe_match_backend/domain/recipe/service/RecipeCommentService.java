package org.example.recipe_match_backend.domain.recipe.service;

import lombok.RequiredArgsConstructor;
import org.example.recipe_match_backend.domain.recipe.domain.RecipeComment;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipeComment.RecipeCommentRequest;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipeComment.RecipeCommentUpdateRequest;
import org.example.recipe_match_backend.domain.recipe.dto.request.recipeComment.RecipeCommentDeleteRequest;
import org.example.recipe_match_backend.domain.recipe.dto.response.recipeComment.RecipeCommentResponse;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeCommentRepository;
import org.example.recipe_match_backend.domain.recipe.repository.RecipeRepository;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.example.recipe_match_backend.domain.user.repository.UserRepository;
import org.example.recipe_match_backend.global.exception.recipe.RecipeNotFoundException;
import org.example.recipe_match_backend.global.exception.recipeComment.CommentNotFoundException;
import org.example.recipe_match_backend.global.exception.recipeComment.CommentNotMatchRecipe;
import org.example.recipe_match_backend.global.exception.recipeComment.UserNotAuthException;
import org.example.recipe_match_backend.global.exception.user.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RecipeCommentService {

    private final RecipeRepository recipeRepository;
    private final RecipeCommentRepository recipeCommentRepository;
    private final UserRepository userRepository;


    // 특정 레시피의 모든 댓글 조회
    public List<RecipeCommentResponse> getCommentsByRecipeId(Long recipeId) {
        // Optional: 레시피가 존재하는지 확인 (존재하지 않으면 예외 발생)
        recipeRepository.findById(recipeId)
                .orElseThrow(RecipeNotFoundException::new);

        List<RecipeComment> comments = recipeCommentRepository.findAllByRecipeId(recipeId);

        return comments.stream()
                .map(comment -> RecipeCommentResponse.builder()
                        .id(comment.getId())
                        .nickname(comment.getUser().getNickname())
                        .recipeId(comment.getRecipe().getId())
                        .content(comment.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    // 댓글 생성
    @Transactional
    public void createComment(RecipeCommentRequest request) {
        // 레시피 조회
        var recipe = recipeRepository.findById(request.getRecipeId())
                .orElseThrow(RecipeNotFoundException::new);

        // 사용자 조회 (User 엔티티는 고유 식별자(uid)를 가진다고 가정)
        User user = userRepository.findByUid(request.getUserUid())
                .orElseThrow(UserNotFoundException::new);

        // 댓글 생성 (id는 자동생성)
        RecipeComment comment = new RecipeComment(null, user, recipe, request.getContent());
        recipeCommentRepository.save(comment);

    }

    // 댓글 수정
    @Transactional
    public RecipeCommentResponse updateComment(Long commentId, RecipeCommentUpdateRequest request) {
        RecipeComment comment = recipeCommentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        // 댓글 작성자와 요청 사용자가 일치하는지 확인
        if (!comment.getUser().getUid().equals(request.getUserUid())) {
            throw new UserNotAuthException();
        }

        // 댓글 내용 수정
        comment.updateContent(request.getContent());

        return RecipeCommentResponse.builder()
                .id(comment.getId())
                .nickname(comment.getUser().getNickname())
                .recipeId(comment.getRecipe().getId())
                .content(comment.getContent())
                .build();
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, RecipeCommentDeleteRequest request) {
        RecipeComment comment = recipeCommentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        // 댓글 작성자와 요청 사용자가 일치하는지 확인
        if (!comment.getUser().getUid().equals(request.getUserUid())) {
            throw new UserNotAuthException();
        }

        recipeCommentRepository.delete(comment);
    }

}
