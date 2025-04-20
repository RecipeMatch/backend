package org.example.recipe_match_backend.global.api.chatgptProducts.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.recipe_match_backend.global.api.chatgptProducts.dto.Message;
import org.example.recipe_match_backend.global.api.chatgptProducts.dto.request.AiContentRequest;
import org.example.recipe_match_backend.global.api.chatgptProducts.dto.request.CommentRequest;
import org.example.recipe_match_backend.global.api.chatgptProducts.dto.response.CommentResponse;
import org.example.recipe_match_backend.global.config.ChatgptClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiCommentService {

    private final ChatgptClient chatgptClient;

    private final static String AI_MODEL = "gpt-4o";
    private final static String AI_ROLE = "system";
    private final static String USER_ROLE = "user";

    @Value("${AI_SECRET_KEY}")
    private String apiKey;

    public CommentResponse commentResponse(AiContentRequest request){

        List<Message> messages = new ArrayList<>();

        Message sysytemMessage = new Message(AI_ROLE, request.getSystemContent());
        Message userMessage = new Message(USER_ROLE, request.getUserContent());

        messages.add(sysytemMessage);
        messages.add(userMessage);

        CommentRequest commentRequest = new CommentRequest(AI_MODEL,messages);

        return chatgptClient.getGptComment(apiKey, commentRequest);
    }

}
