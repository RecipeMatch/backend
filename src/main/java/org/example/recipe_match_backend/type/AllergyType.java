package org.example.recipe_match_backend.type;

import org.example.recipe_match_backend.global.exception.type.AllergyTypeException;
import org.example.recipe_match_backend.global.exception.type.DifficultyTypeException;

public enum AllergyType {
    EGG("알류"),
    MILK("우유"),
    MEMIL("메밀"),
    PEANUT("땅콩"),
    SOY("대두"),
    WHEAT("밀"),
    PINENUT("잣"),
    WALNUT("호두"),
    CRAB("게"),
    SHRIMP("새우"),
    SQUID("오징어"),
    MACKEREL("고등어"),
    SHELLFISH("조개류"),
    PEACH("복숭아"),
    TOMATO("토마토"),
    CHICKEN("닭고기"),
    PORK("돼지고기"),
    BEEF("쇠고기"),
    SULFITE("아황산류");

    private final String displayName;

    AllergyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * displayName(한글)으로부터 해당 Enum을 찾아내는 메서드
     */
    public static AllergyType fromDisplayName(String displayName) {
        for (AllergyType type : AllergyType.values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new AllergyTypeException();
    }

}
