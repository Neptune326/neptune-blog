package com.blog.vo;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * AI writing response.
 */
@Data
public class AiWritingRsp {

    private String action;

    private String result;

    private List<String> suggestions;

    public static AiWritingRsp of(String action, String result) {
        AiWritingRsp rsp = new AiWritingRsp();
        rsp.setAction(action);
        rsp.setResult(result);
        rsp.setSuggestions(Collections.emptyList());
        return rsp;
    }

    public static AiWritingRsp of(String action, String result, List<String> suggestions) {
        AiWritingRsp rsp = of(action, result);
        rsp.setSuggestions(suggestions == null ? Collections.emptyList() : suggestions);
        return rsp;
    }
}
