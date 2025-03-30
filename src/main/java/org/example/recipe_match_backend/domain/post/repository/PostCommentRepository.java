package org.example.recipe_match_backend.domain.post.repository;

import org.example.recipe_match_backend.domain.post.domain.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
}
