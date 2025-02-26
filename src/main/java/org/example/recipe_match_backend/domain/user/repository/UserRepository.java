package org.example.recipe_match_backend.domain.user.repository;

import org.example.recipe_match_backend.domain.recipe.domain.Recipe;
import org.example.recipe_match_backend.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // uid으로 유저 찾기
    Optional<User> findByUid(String uid);
    
    // nickname으로 유저 찾기
    Optional<User> findByNickname(String nickname);

}
