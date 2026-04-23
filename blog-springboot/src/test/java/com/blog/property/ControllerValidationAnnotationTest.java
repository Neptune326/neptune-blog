package com.blog.property;

import com.blog.controller.admin.AdminUserController;
import com.blog.controller.admin.AiAdminController;
import com.blog.controller.admin.ArticleAdminController;
import com.blog.controller.admin.ArticleHistoryAdminController;
import com.blog.controller.admin.ArticleImportExportController;
import com.blog.controller.admin.AuthAdminController;
import com.blog.controller.admin.CategoryAdminController;
import com.blog.controller.admin.CommentAdminController;
import com.blog.controller.admin.DashboardAdminController;
import com.blog.controller.admin.DevToolController;
import com.blog.controller.admin.FriendLinkAdminController;
import com.blog.controller.admin.LogAdminController;
import com.blog.controller.admin.MessageAdminController;
import com.blog.controller.admin.SeriesAdminController;
import com.blog.controller.admin.SysConfigAdminController;
import com.blog.controller.admin.TagAdminController;
import com.blog.controller.admin.UploadAdminController;
import com.blog.controller.admin.VisitStatController;
import com.blog.controller.frontend.AboutFrontController;
import com.blog.controller.frontend.ArticleFrontController;
import com.blog.controller.frontend.ArticleLikeController;
import com.blog.controller.frontend.CategoryFrontController;
import com.blog.controller.frontend.CommentFrontController;
import com.blog.controller.frontend.FriendLinkFrontController;
import com.blog.controller.frontend.MessageFrontController;
import com.blog.controller.frontend.SiteConfigFrontController;
import com.blog.controller.frontend.SitemapController;
import com.blog.controller.frontend.TagFrontController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Controller 校验注解测试
 */
class ControllerValidationAnnotationTest {

    @Test
    void allControllers_shouldUseValidated() {
        List<Class<?>> controllerClasses = List.of(
                AdminUserController.class,
                AiAdminController.class,
                ArticleAdminController.class,
                ArticleHistoryAdminController.class,
                ArticleImportExportController.class,
                AuthAdminController.class,
                CategoryAdminController.class,
                CommentAdminController.class,
                DashboardAdminController.class,
                DevToolController.class,
                FriendLinkAdminController.class,
                LogAdminController.class,
                MessageAdminController.class,
                SeriesAdminController.class,
                SysConfigAdminController.class,
                TagAdminController.class,
                UploadAdminController.class,
                VisitStatController.class,
                AboutFrontController.class,
                ArticleFrontController.class,
                ArticleLikeController.class,
                CategoryFrontController.class,
                CommentFrontController.class,
                FriendLinkFrontController.class,
                MessageFrontController.class,
                SiteConfigFrontController.class,
                SitemapController.class,
                TagFrontController.class
        );

        controllerClasses.forEach(controllerClass -> Assertions.assertTrue(
                controllerClass.isAnnotationPresent(Validated.class),
                controllerClass.getSimpleName() + " 缺少 @Validated 注解"
        ));
    }
}
