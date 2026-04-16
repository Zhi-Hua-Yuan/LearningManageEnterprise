package com.spt.learningmanage.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("task")
public class Task {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long tenantId;
    private Long projectId;
    private Long milestoneId;
    /**
     * 创建者（owner）用户ID。
     * 当前阶段按 owner 语义存储，暂不表示协作成员或执行人。
     */
    private Long userId;
    private String title;
    private String description;
    private Integer status;
    private Integer priority;
    private LocalDate dueDate;
    private LocalDateTime completedAt;
    @TableLogic
    private Integer isDelete;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
