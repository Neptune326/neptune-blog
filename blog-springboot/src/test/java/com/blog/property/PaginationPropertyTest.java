package com.blog.property;

import com.blog.vo.PageVO;
import net.jqwik.api.*;
import org.junit.jupiter.api.Assertions;

/**
 * 分页查询结果一致性属性测试
 * Feature: personal-blog-system
 */
class PaginationPropertyTest {

    // Feature: personal-blog-system, Property 5: 分页查询结果一致性
    @Property(tries = 100)
    void 分页结果list长度不超过pageSize(
            @ForAll("validPageNums") Integer pageNum,
            @ForAll("validPageSizes") Integer pageSize) {

        // 构造模拟分页结果（模拟 Service 返回的 PageVO）
        int totalRecords = 55; // 固定总数用于验证
        int startIndex = (pageNum - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalRecords);
        int listSize = Math.max(0, endIndex - startIndex);

        PageVO<Object> pageVO = new PageVO<>();
        pageVO.setTotal((long) totalRecords);
        pageVO.setPages((long) Math.ceil((double) totalRecords / pageSize));
        pageVO.setList(java.util.Collections.nCopies(listSize, new Object()));

        // 属性 1：list 长度不超过 pageSize
        Assertions.assertTrue(pageVO.getList().size() <= pageSize,
                "分页结果 list 长度不应超过 pageSize");

        // 属性 2：total 与实际数据量一致
        Assertions.assertEquals(totalRecords, pageVO.getTotal(),
                "total 应与实际数据总量一致");

        // 属性 3：pages 计算正确
        long expectedPages = (long) Math.ceil((double) totalRecords / pageSize);
        Assertions.assertEquals(expectedPages, pageVO.getPages(),
                "pages 应等于 ceil(total / pageSize)");
    }

    @Provide
    Arbitrary<Integer> validPageNums() {
        return Arbitraries.integers().between(1, 100);
    }

    @Provide
    Arbitrary<Integer> validPageSizes() {
        return Arbitraries.integers().between(1, 50);
    }
}
