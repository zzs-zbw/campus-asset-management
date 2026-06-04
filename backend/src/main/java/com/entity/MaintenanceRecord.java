package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("maintenance_record")
public class MaintenanceRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long assetId;

    private String assetCode;

    private String faultDescription;

    private LocalDateTime reportTime;

    private String reporter;

    private Integer status;

    private BigDecimal maintenanceCost;

    private LocalDateTime maintenanceTime;

    private String maintenanceResult;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableField(exist = false)
    private Long reporterId;

    @TableField(exist = false)
    private String maintenanceReason;

    @TableField(exist = false)
    private String maintenanceMethod;

    @TableField(exist = false)
    private LocalDateTime startTime;

    @TableField(exist = false)
    private LocalDateTime endTime;

    @TableField(exist = false)
    private String handler;

    @TableField(exist = false)
    private String assetName;

    @TableField(exist = false)
    private String statusText;
}
