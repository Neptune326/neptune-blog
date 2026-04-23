package com.blog.enums;

import java.util.Arrays;

/**
 * AI writing action.
 */
public enum AiWritingActionEnum {

    SUMMARY("summary"),
    POLISH_TITLE("polish_title"),
    OUTLINE("outline"),
    TAGS("tags"),
    CONTINUE("continue"),
    REWRITE("rewrite"),
    CUSTOM("custom");

    private final String code;

    AiWritingActionEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static AiWritingActionEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(action -> action.code.equals(code))
                .findFirst()
                .orElse(null);
    }
}
