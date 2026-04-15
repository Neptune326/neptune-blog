package com.blog.property;

import com.blog.common.result.Result;
import com.blog.common.result.ResultCode;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

/**
 * 统一响应结构完整性属性测试
 * Feature: personal-blog-system
 */
class ResponsePropertyTest {

    // Feature: personal-blog-system, Property 7: 统一响应结构完整性
    @Property(tries = 100)
    void 成功响应包含完整结构(@ForAll("anyData") String data) {
        Result<String> result = Result.success(data);

        // 断言：code 字段存在且为整数
        Assertions.assertNotNull(result.getCode(), "响应体必须包含 code 字段");
        Assertions.assertInstanceOf(Integer.class, result.getCode(), "code 必须为 Integer 类型");

        // 断言：message 字段存在
        Assertions.assertNotNull(result.getMessage(), "响应体必须包含 message 字段");

        // 断言：成功响应 code=200
        Assertions.assertEquals(200, result.getCode(), "成功响应 code 应为 200");
    }

    @Property(tries = 100)
    void 错误响应包含完整结构(@ForAll("resultCodes") ResultCode resultCode) {
        Result<Void> result = Result.error(resultCode);

        // 断言：code 字段存在且为整数
        Assertions.assertNotNull(result.getCode(), "错误响应体必须包含 code 字段");
        Assertions.assertInstanceOf(Integer.class, result.getCode(), "code 必须为 Integer 类型");

        // 断言：message 字段存在
        Assertions.assertNotNull(result.getMessage(), "错误响应体必须包含 message 字段");

        // 断言：错误响应 code 与枚举一致
        Assertions.assertEquals(resultCode.getCode(), result.getCode(),
                "错误响应 code 应与 ResultCode 枚举一致");
    }

    @Property(tries = 100)
    void 空数据成功响应结构完整() {
        Result<Void> result = Result.success();

        Assertions.assertNotNull(result.getCode());
        Assertions.assertNotNull(result.getMessage());
        Assertions.assertEquals(200, result.getCode());
    }

    @Provide
    Arbitrary<String> anyData() {
        return Arbitraries.strings().ofMinLength(0).ofMaxLength(100);
    }

    @Provide
    Arbitrary<ResultCode> resultCodes() {
        return Arbitraries.of(ResultCode.values());
    }
}
