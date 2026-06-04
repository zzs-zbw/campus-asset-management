package com.controller;

import com.common.Result;
import com.entity.SysUser;
import com.service.SysUserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthController {

    @Resource
    private SysUserService sysUserService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");
        
        SysUser user = sysUserService.login(username, password);
        
        if (user != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("token", "token_" + user.getId() + "_" + System.currentTimeMillis());
            data.put("user", user);
            return Result.success("登录成功", data);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success("退出成功");
    }
}