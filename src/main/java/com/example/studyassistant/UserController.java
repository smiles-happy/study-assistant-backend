package com.example.studyassistant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {

    private final JdbcTemplate jdbcTemplate;

    public UserController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ========== 1. 用户注册 ==========
    @PostMapping("/user/register")
    public String register(@RequestParam String username, @RequestParam String password) {
        String checkSql = "SELECT COUNT(*) FROM user WHERE username = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, username);
        if (count > 0) return "用户名已存在！";

        String avatar = "https://api.dicebear.com/7.x/avataaars/svg?seed=" + username;
        String insertSql = "INSERT INTO user(username, password, name, avatar) VALUES (?, ?, ?, ?)";
        int rows = jdbcTemplate.update(insertSql, username, password, username, avatar);
        return rows > 0 ? "success" : "注册失败，请稍后重试";
    }

    // ========== 2. 用户登录 ==========
    @PostMapping("/user/login")
    public Map<String, Object> login(@RequestParam String username, @RequestParam String password) {
        String sql = "SELECT user_id FROM user WHERE username = ? AND password = ?";
        try {
            Integer user_id = jdbcTemplate.queryForObject(sql, Integer.class, username, password);
            return Map.of("code", 200, "user_id", user_id);
        } catch (Exception e) {
            return Map.of("code", 500, "msg", "用户名或密码错误");
        }
    }

    // ========== 3. 获取用户信息 ==========
    @PostMapping("/user/info")
    public Map<String, Object> getUserInfo(@RequestParam Integer user_id) {
        String sql = "SELECT name, avatar FROM user WHERE user_id = ?";
        try {
            Map<String, Object> userInfo = jdbcTemplate.queryForMap(sql, user_id);
            return Map.of("code", 200, "data", userInfo);
        } catch (Exception e) {
            return Map.of("code", 500, "msg", "获取用户信息失败");
        }
    }

    // ========== 4. 统一更新用户信息（昵称+头像+密码） ==========
    @PostMapping("/user/update")
    public Map<String, Object> updateUser(
            @RequestParam Integer user_id,
            // 同时兼容 name 和 username 两个参数名，前端传哪个都能正常接收
            @RequestParam(required = false, name = "name") String name,
            @RequestParam(required = false, name = "username") String username,
            @RequestParam(required = false) String avatar,
            @RequestParam String oldPassword,
            @RequestParam(required = false) String newPassword) {

        // 1. 先校验旧密码，身份验证不通过直接返回
        if (!checkOldPassword(user_id, oldPassword)) {
            return Map.of("code", 400, "msg", "原密码错误");
        }

        // 2. 合并昵称参数：优先用 name，name 为空则 fallback 用 username
        String finalName = (name != null && !name.isEmpty()) ? name : username;

        // 3. 动态拼接更新SQL，哪个字段有值就更新哪个
        StringBuilder sql = new StringBuilder("UPDATE user SET ");
        List<Object> params = new ArrayList<>();

        // 更新显示昵称（只改 name 字段，不碰登录账号 username）
        if (finalName != null && !"".equals(finalName)) {
            sql.append("name = ?, ");
            params.add(finalName);
        }

        // 更新头像地址
        if (avatar != null && !"".equals(avatar)) {
            sql.append("avatar = ?, ");
            params.add(avatar);
        }

        // 更新登录密码
        if (newPassword != null && !"".equals(newPassword)) {
            if (newPassword.equals(oldPassword)) {
                return Map.of("code", 400, "msg", "新密码不能与旧密码相同");
            }
            sql.append("password = ?, ");
            params.add(newPassword);
        }

        // 4. 没有任何要更新的内容，直接返回提示
        if (params.isEmpty()) {
            return Map.of("code", 400, "msg", "没有需要更新的内容");
        }

        // 5. 去掉末尾多余的逗号，拼接 WHERE 条件
        sql.delete(sql.length() - 2, sql.length());
        sql.append(" WHERE user_id = ?");
        params.add(user_id);

        // 6. 执行更新操作，捕获异常
        try {
            jdbcTemplate.update(sql.toString(), params.toArray());
            return Map.of("code", 200, "msg", "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("code", 500, "msg", "更新失败，请稍后重试");
        }
    }

    // ========== 5. 单独修改密码（保留兼容旧前端代码） ==========
    @PostMapping("/user/password")
    public String updatePassword(
            @RequestParam Integer user_id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        if (!checkOldPassword(user_id, oldPassword)) {
            return "旧密码错误！";
        }

        String updateSql = "UPDATE user SET password = ? WHERE user_id = ?";
        int rows = jdbcTemplate.update(updateSql, newPassword, user_id);
        return rows > 0 ? "success" : "密码修改失败，请稍后重试";
    }

    // ========== 公共方法：校验旧密码（消除重复代码） ==========
    private boolean checkOldPassword(Integer user_id, String oldPassword) {
        String checkSql = "SELECT COUNT(*) FROM user WHERE user_id = ? AND password = ?";
        try {
            int count = jdbcTemplate.queryForObject(checkSql, Integer.class, user_id, oldPassword);
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }
}