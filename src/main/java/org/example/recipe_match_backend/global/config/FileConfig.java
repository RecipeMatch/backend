package org.example.recipe_match_backend.global.config;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Configuration;

/** 파일 타입 체크를 위한 Bean 등록 */
@Configuration
public class FileConfig {

    // 파일 타입 체크를 위한 라이브러리 Bean 등록
    public Tika getTika(){
        return new Tika();
    }

}
