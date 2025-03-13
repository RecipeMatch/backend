package org.example.recipe_match_backend.domain.allergy.repository;

import org.example.recipe_match_backend.domain.allergy.domain.Allergy;
import org.example.recipe_match_backend.domain.ingredient.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {
    Optional<Allergy> findByAllergyName(String allergyName);
}
