package com.blog.service;

import com.blog.dto.AiWritingReq;
import com.blog.vo.AiWritingRsp;

/**
 * AI service.
 */
public interface AiService {

    AiWritingRsp writing(AiWritingReq req);
}
