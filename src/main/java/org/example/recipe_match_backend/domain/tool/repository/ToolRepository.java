package org.example.recipe_match_backend.domain.tool.repository;

import org.example.recipe_match_backend.domain.tool.domain.Tool;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ToolRepository extends JpaRepository<Tool, Long> {
    Optional<Tool> findByToolName(String toolName);
}
