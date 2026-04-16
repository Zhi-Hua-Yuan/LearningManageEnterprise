package com.spt.learningmanage.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("milestone")
public class Milestone {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long tenantId;
    private Long projectId;
    /**
     * 创建者（owner）用户ID。
     * 当前阶段用于里程碑归属标识与 owner 访问过滤。
     */
    private Long userId;
    private String name;
    private Integer orderNo;
    private BigDecimal progress;
    private LocalDateTime deletedAt;
    private Integer deleteSource;

    @TableLogic
    private Integer isDelete;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
