package org.example.recipe_match_backend.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAllergyRepository extends JpaRepository<UserAllergy, Long> {
}
