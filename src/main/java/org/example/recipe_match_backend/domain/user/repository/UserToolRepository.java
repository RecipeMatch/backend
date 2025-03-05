package org.example.recipe_match_backend.domain.user.repository;

import org.example.recipe_match_backend.domain.user.domain.UserTool;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserToolRepository extends JpaRepository<UserTool, Long> {
}
