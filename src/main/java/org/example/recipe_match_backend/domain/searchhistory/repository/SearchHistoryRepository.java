package org.example.recipe_match_backend.domain.searchhistory.repository;

import org.example.recipe_match_backend.domain.searchhistory.domain.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory,Long>,SearchHistoryRepositoryCustom {
}
