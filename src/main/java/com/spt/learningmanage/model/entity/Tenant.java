package com.spt.learningmanage.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tenant")
public class Tenant {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String code;
    private String name;
    private Integer status;
    private Integer isDefault;
    private String remark;

    @TableLogic
    private Integer isDelete;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}

