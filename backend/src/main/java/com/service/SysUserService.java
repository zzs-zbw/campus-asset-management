package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    SysUser login(String username, String password);

    void updateLastLoginTime(Long userId);
}