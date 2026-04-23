package com.blog.property;

import com.blog.common.exception.BusinessException;
import com.blog.common.result.ResultCode;
import com.blog.dto.AiWritingReq;
import com.blog.service.AiChatClient;
import com.blog.service.SysConfigService;
import com.blog.service.TagService;
import com.blog.service.impl.AiServiceImpl;
import com.blog.vo.AiWritingRsp;
import com.blog.vo.TagVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

class AiServiceImplTest {

    @Test
    void writing_disabled_shouldThrowBusinessException() {
        SysConfigService sysConfigService = Mockito.mock(SysConfigService.class);
        AiChatClient aiChatClient = Mockito.mock(AiChatClient.class);
        TagService tagService = Mockito.mock(TagService.class);
        AiServiceImpl service = new AiServiceImpl(sysConfigService, aiChatClient, tagService);

        Mockito.when(sysConfigService.getBoolValue("ai_enabled")).thenReturn(false);

        BusinessException ex = Assertions.assertThrows(BusinessException.class,
                () -> service.writing(req("summary")));
        Assertions.assertEquals(ResultCode.BAD_REQUEST, ex.getResultCode());
        Mockito.verifyNoInteractions(aiChatClient);
    }

    @Test
    void writing_missingApiKey_shouldThrowBusinessException() {
        SysConfigService sysConfigService = Mockito.mock(SysConfigService.class);
        AiChatClient aiChatClient = Mockito.mock(AiChatClient.class);
        TagService tagService = Mockito.mock(TagService.class);
        AiServiceImpl service = new AiServiceImpl(sysConfigService, aiChatClient, tagService);

        Mockito.when(sysConfigService.getBoolValue("ai_enabled")).thenReturn(true);
        Mockito.when(sysConfigService.getValue("ai_base_url")).thenReturn("https://api.example.com");
        Mockito.when(sysConfigService.getValue("ai_model")).thenReturn("demo-model");
        Mockito.when(sysConfigService.getValue("ai_api_key")).thenReturn("");

        BusinessException ex = Assertions.assertThrows(BusinessException.class,
                () -> service.writing(req("summary")));
        Assertions.assertEquals(ResultCode.BAD_REQUEST, ex.getResultCode());
        Mockito.verifyNoInteractions(aiChatClient);
    }

    @Test
    void writing_invalidAction_shouldThrowBusinessException() {
        SysConfigService sysConfigService = Mockito.mock(SysConfigService.class);
        AiChatClient aiChatClient = Mockito.mock(AiChatClient.class);
        TagService tagService = Mockito.mock(TagService.class);
        AiServiceImpl service = new AiServiceImpl(sysConfigService, aiChatClient, tagService);

        BusinessException ex = Assertions.assertThrows(BusinessException.class,
                () -> service.writing(req("unknown")));
        Assertions.assertEquals(ResultCode.BAD_REQUEST, ex.getResultCode());
        Mockito.verifyNoInteractions(aiChatClient);
    }

    @Test
    void writing_emptyAiResponse_shouldThrowBusinessException() {
        SysConfigService sysConfigService = configuredSysConfigService();
        AiChatClient aiChatClient = Mockito.mock(AiChatClient.class);
        TagService tagService = Mockito.mock(TagService.class);
        AiServiceImpl service = new AiServiceImpl(sysConfigService, aiChatClient, tagService);

        Mockito.when(aiChatClient.chat(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())).thenReturn("");

        BusinessException ex = Assertions.assertThrows(BusinessException.class,
                () -> service.writing(req("summary")));
        Assertions.assertEquals(ResultCode.INTERNAL_ERROR, ex.getResultCode());
    }

    @Test
    void writing_tags_shouldOnlyReturnExistingTags() {
        SysConfigService sysConfigService = configuredSysConfigService();
        AiChatClient aiChatClient = Mockito.mock(AiChatClient.class);
        TagService tagService = Mockito.mock(TagService.class);
        AiServiceImpl service = new AiServiceImpl(sysConfigService, aiChatClient, tagService);

        Mockito.when(aiChatClient.chat(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
                Mockito.any(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn("Vue3, Unknown, MySQL");
        Mockito.when(tagService.list()).thenReturn(List.of(tag("Vue3"), tag("MySQL")));

        AiWritingRsp rsp = service.writing(req("tags"));

        Assertions.assertEquals(List.of("Vue3", "MySQL"), rsp.getSuggestions());
    }

    private SysConfigService configuredSysConfigService() {
        SysConfigService sysConfigService = Mockito.mock(SysConfigService.class);
        Mockito.when(sysConfigService.getBoolValue("ai_enabled")).thenReturn(true);
        Mockito.when(sysConfigService.getValue("ai_base_url")).thenReturn("https://api.example.com");
        Mockito.when(sysConfigService.getValue("ai_model")).thenReturn("demo-model");
        Mockito.when(sysConfigService.getValue("ai_api_key")).thenReturn("sk-demo");
        Mockito.when(sysConfigService.getValue("ai_temperature")).thenReturn("0.7");
        Mockito.when(sysConfigService.getIntValue("ai_max_tokens", 1200)).thenReturn(1200);
        return sysConfigService;
    }

    private AiWritingReq req(String action) {
        AiWritingReq req = new AiWritingReq();
        req.setAction(action);
        req.setTitle("Demo title");
        req.setContent("Demo content");
        return req;
    }

    private TagVO tag(String name) {
        TagVO tag = new TagVO();
        tag.setName(name);
        return tag;
    }
}
