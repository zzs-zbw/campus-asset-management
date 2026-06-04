package com.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.PageResult;
import com.common.Result;
import com.entity.OperationLog;
import com.service.OperationLogService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/log")
@CrossOrigin
public class OperationLogController {

    @Resource
    private OperationLogService operationLogService;

    @GetMapping("/page")
    public Result<PageResult<OperationLog>> page(@RequestParam(defaultValue = "1") Integer current,
                                                 @RequestParam(defaultValue = "10") Integer size,
                                                 @RequestParam(required = false) String operationType,
                                                 @RequestParam(required = false) String moduleName) {
        Page<OperationLog> page = new Page<>(current, size);
        IPage<OperationLog> result = operationLogService.getLogPage(page, operationType, moduleName);
        return Result.success(PageResult.of(result.getTotal(), result.getRecords()));
    }
}