package org.example.recipe_match_backend.type;

import lombok.Getter;
import org.example.recipe_match_backend.global.exception.type.DifficultyTypeException;

@Getter
public enum DifficultyType {
    EASY("초보환영"),
    MIDDLE("보통"),
    HARD("어려움");

    private final String korName;

    DifficultyType(String korName) {
        this.korName = korName;
    }

    /**
     * 한글명(korName)으로부터 Enum 값을 찾기 위한 정적 메서드
     */
    public static DifficultyType fromKorName(String korName) {
        for (DifficultyType type : DifficultyType.values()) {
            if (type.getKorName().equals(korName)) {
                return type;
            }
        }
        throw new DifficultyTypeException();
    }
}