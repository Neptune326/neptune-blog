package com.blog.client;

import com.blog.common.exception.BusinessException;
import com.blog.common.result.ResultCode;
import com.blog.service.AiChatClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * OpenAI-compatible chat completions client.
 */
@Slf4j
@Component
public class OpenAiCompatibleClient implements AiChatClient {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(60);

    @Override
    public String chat(String baseUrl,
                       String model,
                       String apiKey,
                       BigDecimal temperature,
                       Integer maxTokens,
                       String systemPrompt,
                       String userPrompt) {
        String endpoint = buildEndpoint(baseUrl);
        Map<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));
        requestBody.put("temperature", temperature);
        requestBody.put("max_tokens", maxTokens);
        requestBody.put("stream", false);

        try {
            ChatCompletionRsp response = buildClient()
                    .post()
                    .uri(endpoint)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(ChatCompletionRsp.class);
            return extractContent(response);
        } catch (RestClientException e) {
            log.error("[AI writing] OpenAI-compatible request failed, endpoint: {}, model: {}", endpoint, model, e);
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI service request failed");
        }
    }

    private RestClient buildClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        return RestClient.builder().requestFactory(factory).build();
    }

    private String buildEndpoint(String baseUrl) {
        String normalized = baseUrl.trim();
        while (normalized.endsWith("/")) {
            normalized = normalized.substring(0, normalized.length() - 1);
        }
        if (normalized.endsWith("/v1")) {
            return normalized + "/chat/completions";
        }
        return normalized + "/v1/chat/completions";
    }

    private String extractContent(ChatCompletionRsp response) {
        if (response == null || CollectionUtils.isEmpty(response.getChoices())) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI service returned an empty response");
        }
        ChatChoice choice = response.getChoices().get(0);
        if (choice == null || choice.getMessage() == null || !StringUtils.hasText(choice.getMessage().getContent())) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI service returned an empty response");
        }
        return choice.getMessage().getContent().trim();
    }

    @Data
    private static class ChatCompletionRsp {
        private List<ChatChoice> choices;
    }

    @Data
    private static class ChatChoice {
        private ChatMessage message;
    }

    @Data
    private static class ChatMessage {
        private String content;
    }
}
