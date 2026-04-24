package com.blog.property;

import com.blog.common.result.Result;
import com.blog.controller.admin.UploadAdminController;
import com.blog.service.SysConfigService;
import com.blog.vo.UploadFileVO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Path;

class UploadAdminControllerTest {

    @TempDir
    Path tempDir;

    @Test
    void uploadFile_emptyFile_shouldReject() {
        SysConfigService sysConfigService = Mockito.mock(SysConfigService.class);
        UploadAdminController controller = controller(sysConfigService);
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", new byte[0]);

        Result<UploadFileVO> result = controller.uploadFile(file);

        Assertions.assertEquals(400, result.getCode());
        Assertions.assertEquals("文件不能为空", result.getMessage());
    }

    @Test
    void uploadFile_sizeExceedsLimit_shouldReject() {
        SysConfigService sysConfigService = Mockito.mock(SysConfigService.class);
        Mockito.when(sysConfigService.getIntValue("upload_file_max_size_mb", 10)).thenReturn(1);
        Mockito.when(sysConfigService.getValue("upload_file_allowed_exts")).thenReturn(".pdf");
        UploadAdminController controller = controller(sysConfigService);
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf",
                new byte[1024 * 1024 + 1]);

        Result<UploadFileVO> result = controller.uploadFile(file);

        Assertions.assertEquals(400, result.getCode());
        Assertions.assertEquals("文件大小不能超过 1MB", result.getMessage());
    }

    @Test
    void uploadFile_extensionNotAllowed_shouldReject() {
        SysConfigService sysConfigService = Mockito.mock(SysConfigService.class);
        Mockito.when(sysConfigService.getIntValue("upload_file_max_size_mb", 10)).thenReturn(10);
        Mockito.when(sysConfigService.getValue("upload_file_allowed_exts")).thenReturn(".pdf,.docx");
        UploadAdminController controller = controller(sysConfigService);
        MockMultipartFile file = new MockMultipartFile("file", "archive.zip", "application/zip", "demo".getBytes());

        Result<UploadFileVO> result = controller.uploadFile(file);

        Assertions.assertEquals(400, result.getCode());
        Assertions.assertTrue(result.getMessage().contains(".pdf,.docx"));
    }

    @Test
    void uploadFile_uppercaseExtension_shouldAccept() {
        SysConfigService sysConfigService = Mockito.mock(SysConfigService.class);
        Mockito.when(sysConfigService.getIntValue("upload_file_max_size_mb", 10)).thenReturn(10);
        Mockito.when(sysConfigService.getValue("upload_file_allowed_exts")).thenReturn(".pdf,.docx");
        UploadAdminController controller = controller(sysConfigService);
        MockMultipartFile file = new MockMultipartFile("file", "REPORT.PDF", "application/pdf", "demo".getBytes());

        Result<UploadFileVO> result = controller.uploadFile(file);

        Assertions.assertEquals(200, result.getCode());
        Assertions.assertNotNull(result.getData());
        Assertions.assertEquals(".pdf", result.getData().getExtension());
    }

    @Test
    void uploadFile_success_shouldReturnMetadata() {
        SysConfigService sysConfigService = Mockito.mock(SysConfigService.class);
        Mockito.when(sysConfigService.getIntValue("upload_file_max_size_mb", 10)).thenReturn(10);
        Mockito.when(sysConfigService.getValue("upload_file_allowed_exts")).thenReturn(".pdf,.docx");
        UploadAdminController controller = controller(sysConfigService);
        MockMultipartFile file = new MockMultipartFile("file", "report.pdf", "application/pdf", "demo".getBytes());

        Result<UploadFileVO> result = controller.uploadFile(file);

        Assertions.assertEquals(200, result.getCode());
        Assertions.assertNotNull(result.getData());
        Assertions.assertEquals("report.pdf", result.getData().getOriginalName());
        Assertions.assertEquals("application/pdf", result.getData().getContentType());
        Assertions.assertEquals(4L, result.getData().getSize());
        Assertions.assertEquals(".pdf", result.getData().getExtension());
        Assertions.assertTrue(result.getData().getFileName().endsWith(".pdf"));
        Assertions.assertTrue(result.getData().getUrl().contains("/uploads/"));
    }

    private UploadAdminController controller(SysConfigService sysConfigService) {
        UploadAdminController controller = new UploadAdminController(sysConfigService);
        ReflectionTestUtils.setField(controller, "uploadPath", tempDir.toString());
        ReflectionTestUtils.setField(controller, "urlPrefix", "http://localhost:8080/uploads/");
        return controller;
    }
}
