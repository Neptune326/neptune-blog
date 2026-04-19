-- =====================================================
-- 重置管理员密码
-- 将 admin 账号密码重置为 admin123
-- BCrypt 哈希：$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpOsl7iKVSHDK
-- =====================================================

USE blog;

-- 如果 admin 账号不存在则插入，存在则更新密码
INSERT INTO admin (username, password, role, create_time, create_by, update_time, update_by)
VALUES (
    'admin',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpOsl7iKVSHDK',
    'super',
    NOW(), 'system', NOW(), 'system'
)
ON DUPLICATE KEY UPDATE
    password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBpOsl7iKVSHDK',
    role = 'super';

SELECT '密码已重置为 admin123' AS result;
