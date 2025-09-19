package com.example.aicodereviewapi.service.openai.impl;

import com.example.aicodereviewapi.service.openai.OpenaiService;
import com.example.aicodereviewapi.util.ChatTemplates;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenaiServiceImpl implements OpenaiService {

    private final OpenAIClient client;

    public String chat(String code, String language) {
        String input = String.format(ChatTemplates.CODE_REVIEW_TEMPLATE, language, language, code);

        ResponseCreateParams createParams =
                ResponseCreateParams.builder().input(input).model(ChatModel.GPT_3_5_TURBO).build();

        return client.responses().create(createParams).output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(outputText -> outputText.text())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No text output returned from OpenAI"));
    }
}
