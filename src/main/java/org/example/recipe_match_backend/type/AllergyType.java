package org.example.recipe_match_backend.type;

public enum AllergyType {
    EGG("알류"),
    MILK("우유"),
    MEMIL("메밀"),
    DDAENGKONG("땅콩"),
    DAEYOU("대두"),
    MIL("밀"),
    JAT("잣"),
    HOWON("호두"),
    GE("게"),
    SAEWU("새우"),
    OJINGEO("오징어"),
    GODUNGR("고등어"),
    JOGAERYU("조개류"),
    BOKSOONGWA("복숭아"),
    TOMATO("토마토"),
    CHICKEN("닭고기"),
    PIG("돼지고기"),
    BEEF("쇠고기"),
    AHWANGSANRYU("아황산류");

    private final String displayName;

    AllergyType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
