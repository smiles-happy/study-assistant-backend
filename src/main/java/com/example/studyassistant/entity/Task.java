package com.example.studyassistant.entity;

import java.util.Date;

public class Task {
    private Integer task_id;
    private Integer user_id;
    private Integer cate_id;
    private String title;
    private String content;
    private Integer priority;
    private Date deadline;
    private Integer status;

    // Getter/Setter
    public Integer getTask_id() { return task_id; }
    public void setTask_id(Integer task_id) { this.task_id = task_id; }

    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }

    public Integer getCate_id() { return cate_id; }
    public void setCate_id(Integer cate_id) { this.cate_id = cate_id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Date getDeadline() { return deadline; }
    public void setDeadline(Date deadline) { this.deadline = deadline; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}