package org.example.recipe_match_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RecipeMatchBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecipeMatchBackendApplication.class, args);
    }

}
