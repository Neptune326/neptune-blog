package com.blog.service.impl;

import com.blog.common.exception.BusinessException;
import com.blog.common.result.ResultCode;
import com.blog.dto.AiWritingReq;
import com.blog.enums.AiWritingActionEnum;
import com.blog.service.AiChatClient;
import com.blog.service.AiService;
import com.blog.service.SysConfigService;
import com.blog.service.TagService;
import com.blog.vo.AiWritingRsp;
import com.blog.vo.TagVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * AI writing service implementation.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private static final String KEY_ENABLED = "ai_enabled";
    private static final String KEY_BASE_URL = "ai_base_url";
    private static final String KEY_MODEL = "ai_model";
    private static final String KEY_API_KEY = "ai_api_key";
    private static final String KEY_TEMPERATURE = "ai_temperature";
    private static final String KEY_MAX_TOKENS = "ai_max_tokens";
    private static final BigDecimal DEFAULT_TEMPERATURE = new BigDecimal("0.7");
    private static final int DEFAULT_MAX_TOKENS = 1200;
    private static final int MAX_PROMPT_TEXT_LENGTH = 12000;

    private final SysConfigService sysConfigService;
    private final AiChatClient aiChatClient;
    private final TagService tagService;

    @Override
    public AiWritingRsp writing(AiWritingReq req) {
        AiWritingActionEnum action = AiWritingActionEnum.fromCode(req.getAction());
        if (action == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Unsupported AI writing action");
        }
        validateRequest(action, req);

        AiConfig config = loadConfig();
        String userPrompt = buildUserPrompt(action, req);
        log.info("[AI writing] request started, action: {}, titleLength: {}, contentLength: {}",
                action.getCode(), length(req.getTitle()), length(req.getContent()));
        String result = aiChatClient.chat(
                config.baseUrl,
                config.model,
                config.apiKey,
                config.temperature,
                config.maxTokens,
                buildSystemPrompt(action),
                userPrompt
        );
        if (!StringUtils.hasText(result)) {
            throw new BusinessException(ResultCode.INTERNAL_ERROR, "AI service returned an empty response");
        }
        if (action == AiWritingActionEnum.TAGS) {
            List<String> suggestions = parseTagSuggestions(result);
            return AiWritingRsp.of(action.getCode(), result, suggestions);
        }
        return AiWritingRsp.of(action.getCode(), result);
    }

    private AiConfig loadConfig() {
        if (!sysConfigService.getBoolValue(KEY_ENABLED)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "AI feature is disabled");
        }
        String baseUrl = sysConfigService.getValue(KEY_BASE_URL);
        String model = sysConfigService.getValue(KEY_MODEL);
        String apiKey = sysConfigService.getValue(KEY_API_KEY);
        if (!StringUtils.hasText(baseUrl)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "AI base URL is not configured");
        }
        if (!StringUtils.hasText(model)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "AI model is not configured");
        }
        if (!StringUtils.hasText(apiKey)) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "AI API key is not configured");
        }

        AiConfig config = new AiConfig();
        config.baseUrl = baseUrl;
        config.model = model;
        config.apiKey = apiKey;
        config.temperature = parseTemperature(sysConfigService.getValue(KEY_TEMPERATURE));
        config.maxTokens = sysConfigService.getIntValue(KEY_MAX_TOKENS, DEFAULT_MAX_TOKENS);
        if (config.maxTokens <= 0) {
            config.maxTokens = DEFAULT_MAX_TOKENS;
        }
        return config;
    }

    private void validateRequest(AiWritingActionEnum action, AiWritingReq req) {
        if (action == AiWritingActionEnum.CUSTOM && !StringUtils.hasText(req.getCustomPrompt())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Custom prompt is required");
        }
        if ((action == AiWritingActionEnum.SUMMARY
                || action == AiWritingActionEnum.OUTLINE
                || action == AiWritingActionEnum.TAGS
                || action == AiWritingActionEnum.CONTINUE)
                && !StringUtils.hasText(req.getContent())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Article content is required");
        }
        if (action == AiWritingActionEnum.REWRITE
                && !StringUtils.hasText(req.getSelectedText())
                && !StringUtils.hasText(req.getContent())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Text to rewrite is required");
        }
        if (action == AiWritingActionEnum.POLISH_TITLE
                && !StringUtils.hasText(req.getTitle())
                && !StringUtils.hasText(req.getContent())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "Title or content is required");
        }
    }

    private String buildSystemPrompt(AiWritingActionEnum action) {
        if (action == AiWritingActionEnum.TAGS) {
            return "You are a Chinese blog tagging assistant. Return only tag names, separated by commas.";
        }
        return "You are a Chinese blog writing assistant. Return concise, directly usable Markdown or plain text. Do not explain your process.";
    }

    private String buildUserPrompt(AiWritingActionEnum action, AiWritingReq req) {
        String tone = StringUtils.hasText(req.getTone()) ? req.getTone() : "natural";
        String title = safe(req.getTitle());
        String summary = safe(req.getSummary());
        String content = truncate(safe(req.getContent()), MAX_PROMPT_TEXT_LENGTH);
        String selectedText = truncate(safe(req.getSelectedText()), MAX_PROMPT_TEXT_LENGTH);

        return switch (action) {
            case SUMMARY -> """
                    Generate a 120-180 Chinese character summary for this blog article.
                    Title: %s
                    Content:
                    %s
                    """.formatted(title, content);
            case POLISH_TITLE -> """
                    Polish the Chinese blog title. Return 3 short title candidates, one per line.
                    Current title: %s
                    Article summary: %s
                    Article content:
                    %s
                    """.formatted(title, summary, content);
            case OUTLINE -> """
                    Generate a clear Markdown outline for this blog article.
                    Title: %s
                    Content:
                    %s
                    """.formatted(title, content);
            case TAGS -> """
                    Recommend 3 to 5 tags from the available tag list only. Return comma-separated tag names.
                    Available tags: %s
                    Title: %s
                    Summary: %s
                    Content:
                    %s
                    """.formatted(loadTagNamesText(), title, summary, content);
            case CONTINUE -> """
                    Continue this Chinese blog article in a %s tone. Return Markdown content that can be appended directly.
                    Title: %s
                    Existing content:
                    %s
                    """.formatted(tone, title, content);
            case REWRITE -> """
                    Rewrite the following text in a %s tone. Keep the meaning and return only the rewritten content.
                    Title: %s
                    Text:
                    %s
                    """.formatted(tone, title, StringUtils.hasText(selectedText) ? selectedText : content);
            case CUSTOM -> """
                    Follow the custom writing instruction for this blog article.
                    Instruction: %s
                    Title: %s
                    Summary: %s
                    Content:
                    %s
                    """.formatted(safe(req.getCustomPrompt()), title, summary, content);
        };
    }

    private String loadTagNamesText() {
        List<TagVO> tags = tagService.list();
        if (CollectionUtils.isEmpty(tags)) {
            return "";
        }
        return tags.stream()
                .map(TagVO::getName)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(", "));
    }

    private List<String> parseTagSuggestions(String result) {
        List<TagVO> tags = tagService.list();
        if (CollectionUtils.isEmpty(tags)) {
            return Collections.emptyList();
        }
        Set<String> existingNames = tags.stream()
                .map(TagVO::getName)
                .filter(StringUtils::hasText)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> existingLowerNames = existingNames.stream()
                .map(name -> name.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());
        String normalizedResult = result
                .replace('\uFF0C', ',')
                .replace('\u3001', ',')
                .replace('\uFF1B', ';');
        return Arrays.stream(normalizedResult.split("[,;\\n]"))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .filter(name -> existingLowerNames.contains(name.toLowerCase(Locale.ROOT)))
                .distinct()
                .limit(5)
                .toList();
    }

    private BigDecimal parseTemperature(String value) {
        if (!StringUtils.hasText(value)) {
            return DEFAULT_TEMPERATURE;
        }
        try {
            BigDecimal temperature = new BigDecimal(value);
            if (temperature.compareTo(BigDecimal.ZERO) < 0 || temperature.compareTo(new BigDecimal("2")) > 0) {
                return DEFAULT_TEMPERATURE;
            }
            return temperature;
        } catch (NumberFormatException e) {
            log.warn("[AI writing] invalid temperature config: {}, use default: {}", value, DEFAULT_TEMPERATURE);
            return DEFAULT_TEMPERATURE;
        }
    }

    private String truncate(String value, int maxLength) {
        if (value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private int length(String value) {
        return value == null ? 0 : value.length();
    }

    private static class AiConfig {
        private String baseUrl;
        private String model;
        private String apiKey;
        private BigDecimal temperature;
        private Integer maxTokens;
    }
}
