package org.example.recipe_match_backend.domain.post.repository;

import org.example.recipe_match_backend.domain.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post,Long> {

}
