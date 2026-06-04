package com.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.PageResult;
import com.common.Result;
import com.entity.SysUser;
import com.service.SysUserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/page")
    public Result<PageResult<SysUser>> page(
        @RequestParam(defaultValue = "1") Integer current,
        @RequestParam(defaultValue = "10") Integer size) {
        Page<SysUser> page = new Page<>(current, size);
        IPage<SysUser> result = sysUserService.page(page);
        List<SysUser> records = result.getRecords();
        records.forEach(u -> u.setPassword(null));
        return Result.success(PageResult.of(result.getTotal(), records));
    }

    @GetMapping("/list")
    public Result<PageResult<SysUser>> list(
        @RequestParam(defaultValue = "1") Integer current,
        @RequestParam(defaultValue = "10") Integer size) {
        Page<SysUser> page = new Page<>(current, size);
        IPage<SysUser> result = sysUserService.page(page);
        return Result.success(PageResult.of(result.getTotal(), result.getRecords()));
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user != null) {
            user.setPassword(null);
        }
        return Result.success(user);
    }

    @PostMapping
    public Result<String> add(@RequestBody SysUser user) {
        user.setPassword(passwordEncoder.encode("123456"));
        sysUserService.save(user);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody SysUser user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            SysUser existingUser = sysUserService.getById(user.getId());
            user.setPassword(existingUser.getPassword());
        }
        sysUserService.updateById(user);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        sysUserService.removeById(id);
        return Result.success("删除成功");
    }

    @PutMapping("/{id}/status")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        sysUserService.updateById(user);
        return Result.success("状态更新成功");
    }
}