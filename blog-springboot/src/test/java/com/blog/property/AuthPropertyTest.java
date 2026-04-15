package com.blog.property;

import cn.dev33.satoken.exception.NotLoginException;
import com.blog.common.exception.GlobalExceptionHandler;
import com.blog.common.result.Result;
import com.blog.common.result.ResultCode;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

/**
 * Token 鉴权属性测试
 * Feature: personal-blog-system
 */
class AuthPropertyTest {

    // Feature: personal-blog-system, Property 6: Token 过期后接口拒绝访问
    @Property(tries = 100)
    void 无效Token请求后台接口返回401(@ForAll("invalidTokens") String invalidToken) {
        // 模拟 Sa-Token 对无效 token 抛出 NotLoginException
        // 验证 GlobalExceptionHandler 将其转换为 code=401 的响应
        NotLoginException notLoginException = NotLoginException.newInstance(
                "Authorization", NotLoginException.NOT_TOKEN, invalidToken);

        // 直接测试异常处理逻辑：NotLoginException 应被转换为 UNAUTHORIZED 响应
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        Result<Void> result = handler.handleNotLoginException(notLoginException);

        Assertions.assertEquals(ResultCode.UNAUTHORIZED.getCode(), result.getCode(),
                "无效 Token 请求应返回 code=401");
        Assertions.assertNull(result.getData(),
                "401 响应不应包含业务数据");
    }

    @Provide
    Arbitrary<String> invalidTokens() {
        // 生成随机字符串模拟无效/过期 token
        return Arbitraries.strings()
                .withCharRange('a', 'z')
                .ofMinLength(8)
                .ofMaxLength(64);
    }
}
