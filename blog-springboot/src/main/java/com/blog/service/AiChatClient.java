package com.blog.service;

import java.math.BigDecimal;

/**
 * OpenAI-compatible chat client.
 */
public interface AiChatClient {

    String chat(String baseUrl,
                String model,
                String apiKey,
                BigDecimal temperature,
                Integer maxTokens,
                String systemPrompt,
                String userPrompt);
}
