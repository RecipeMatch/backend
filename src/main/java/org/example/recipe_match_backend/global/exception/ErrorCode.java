package org.example.recipe_match_backend.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.recipe_match_backend.type.DifficultyType;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    // common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ECO001", "서버 오류가 발생했습니다."),

    // user
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "EU001", "사용자를 찾을 수 없습니다."),

    // Recipe
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "ERE001", "레시피를 찾을 수 없습니다."),

    // login
    DUPLICATE_USER(HttpStatus.CONFLICT, "EL001", "이미 존재하는 사용자입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "EL002", "유효하지 않은 토큰입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "EL003", "접근이 거부되었습니다."),

    // rating 관련
    INVALID_RATING_VALUE(HttpStatus.BAD_REQUEST, "ERA001", "별점은 1과 5 사이여야 합니다."),

    // comment 관련
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ECM001", "댓글을 찾을 수 없습니다."),
    USER_NOT_AUTH(HttpStatus.UNAUTHORIZED, "ECM002", "댓글을 삭제할 사용자 권한이 없습니다."),
    COMMENT_NOT_MATCH_RECIPE(HttpStatus.NOT_FOUND, "ECM003", "해당 댓글은 요청한 레시피에 속하지 않습니다."),

    // type 관련
    DIFFICULTY_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "ECM001", "일치하는 난이도가 없습니다"),
    ALLERGY_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "ECM001", "일치하는 알레르기가 없습니다");


    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
