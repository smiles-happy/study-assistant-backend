package com.example.studyassistant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
public class TaskController {
    private final JdbcTemplate jdbcTemplate;

    public TaskController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 任务列表查询（使用user_id，和你原来的表结构一致）
    @GetMapping("/task/list")
    public List<Map<String, Object>> getTaskList(@RequestParam Integer user_id) {
        String sql = "SELECT * FROM study_task WHERE user_id = ? ORDER BY deadline ASC";
        return jdbcTemplate.queryForList(sql, user_id);
    }

    // 新增任务
    @PostMapping("/task/add")
    public String addTask(
            @RequestParam Integer user_id,
            @RequestParam Integer cate_id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam Integer priority,
            @RequestParam String deadline) {
        String sql = "INSERT INTO study_task(user_id, cate_id, title, content, priority, deadline, status, remark) VALUES (?, ?, ?, ?, ?, ?, 0, '')";
        int rows = jdbcTemplate.update(sql, user_id, cate_id, title, content, priority, deadline);
        return rows > 0 ? "success" : "fail";
    }

    // 删除任务
    @PostMapping("/task/delete")
    public String deleteTask(@RequestParam Integer id) {
        String sql = "DELETE FROM study_task WHERE task_id = ?";
        return jdbcTemplate.update(sql, id) > 0 ? "success" : "fail";
    }

    // 更新任务状态
    @PostMapping("/task/update/status")
    public String updateStatus(@RequestParam Integer id, @RequestParam Integer status) {
        String sql = "UPDATE study_task SET status = ? WHERE task_id = ?";
        return jdbcTemplate.update(sql, status, id) > 0 ? "success" : "fail";
    }

    // 更新任务标题
    @PostMapping("/task/update")
    public String updateTask(@RequestParam Integer id, @RequestParam String title) {
        String sql = "UPDATE study_task SET title = ? WHERE task_id = ?";
        return jdbcTemplate.update(sql, title, id) > 0 ? "success" : "fail";
    }

    // 更新任务备注
    @PostMapping("/task/update/remark")
    public String updateRemark(@RequestParam Integer id, @RequestParam String remark) {
        String sql = "UPDATE study_task SET remark = ? WHERE task_id = ?";
        return jdbcTemplate.update(sql, remark, id) > 0 ? "success" : "fail";
    }
}