package org.example.recipe_match_backend.domain.searchhistory.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.recipe_match_backend.domain.searchhistory.domain.SearchHistory;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class SearchHistoryRequest {

    private List<SearchHistory> searchHistories;

}
