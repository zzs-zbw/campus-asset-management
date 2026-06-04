package com.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String realName;

    private Integer role;

    private Integer status;

    private String phone;

    private String email;

    private String department;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime lastLoginTime;

    public String getRoleName() {
        if (role == null) return "未知";
        switch (role) {
            case 1: return "超级管理员";
            case 2: return "校级资产负责人";
            case 3: return "部门资产管理员";
            case 4: return "仓库/采购管理员";
            case 5: return "维修运维员";
            case 6: return "财务审核员";
            case 7: return "普通使用人";
            default: return "未知";
        }
    }
}
