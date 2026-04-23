package com.blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * AI writing request.
 */
@Data
public class AiWritingReq {

    @NotBlank(message = "AI action is required")
    private String action;

    @Size(max = 200, message = "Title must be less than 200 characters")
    private String title;

    @Size(max = 1000, message = "Summary must be less than 1000 characters")
    private String summary;

    @Size(max = 30000, message = "Content must be less than 30000 characters")
    private String content;

    @Size(max = 5000, message = "Selected text must be less than 5000 characters")
    private String selectedText;

    @Size(max = 2000, message = "Custom prompt must be less than 2000 characters")
    private String customPrompt;

    @Size(max = 30, message = "Tone must be less than 30 characters")
    private String tone;
}
