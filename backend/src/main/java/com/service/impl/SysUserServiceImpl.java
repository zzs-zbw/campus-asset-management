package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.SysUser;
import com.mapper.SysUserMapper;
import com.service.SysUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void initDefaultUsers() {
        initUser("admin", "admin123", "系统管理员", 1, "信息中心", "13800138000", "admin@school.edu.cn");
        initUser("school_admin", "123456", "校级资产负责人", 2, "资产管理处", "13800138001", "school_admin@school.edu.cn");
        initUser("dept_admin", "123456", "部门资产管理员", 3, "计算机系", "13800138002", "dept_admin@school.edu.cn");
        initUser("warehouse", "123456", "仓库管理员", 4, "采购仓储中心", "13800138003", "warehouse@school.edu.cn");
        initUser("maintenance", "123456", "维修运维员", 5, "运维保障部", "13800138004", "maintenance@school.edu.cn");
        initUser("finance", "123456", "财务审核员", 6, "财务处", "13800138005", "finance@school.edu.cn");
        initUser("teacher1", "123456", "张老师", 7, "计算机系", "13800138006", "teacher1@school.edu.cn");
    }

    private void initUser(String username, String password, String realName, int role, String dept, String phone, String email) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        if (this.getOne(wrapper) != null) {
            return;
        }
        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRealName(realName);
        user.setRole(role);
        user.setStatus(1);
        user.setDepartment(dept);
        user.setPhone(phone);
        user.setEmail(email);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        this.save(user);
    }

    @Override
    public SysUser login(String username, String password) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        wrapper.eq(SysUser::getStatus, 1);
        SysUser user = this.getOne(wrapper);

        if (user != null) {
            if (passwordEncoder.matches(password, user.getPassword())) {
                updateLastLoginTime(user.getId());
                return user;
            }
            if (user.getPassword().equals(password)) {
                updateLastLoginTime(user.getId());
                return user;
            }
        }
        return null;
    }

    @Override
    public void updateLastLoginTime(Long userId) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        this.updateById(user);
    }
}
