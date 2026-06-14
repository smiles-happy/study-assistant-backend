package com.example.studyassistant;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatController {
    private final JdbcTemplate jdbcTemplate;

    public StatController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/stat")
    public String stat(@RequestParam int uid) {
        jdbcTemplate.update("CALL proc_study_stat(?)", uid);
        return "统计完成";
    }
}
