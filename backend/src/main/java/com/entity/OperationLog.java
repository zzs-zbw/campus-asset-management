package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String username;

    private String operationType;

    private String module;

    private String content;

    private String ipAddress;

    private LocalDateTime createTime;

    @TableField(exist = false)
    private String moduleName;

    @TableField(exist = false)
    private String operationDesc;

    @TableField(exist = false)
    private LocalDateTime operationTime;
}
