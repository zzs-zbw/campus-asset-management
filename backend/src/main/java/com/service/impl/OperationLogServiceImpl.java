package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.OperationLog;
import com.mapper.OperationLogMapper;
import com.service.OperationLogService;
import org.springframework.stereotype.Service;

@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements OperationLogService {

    @Override
    public IPage<OperationLog> getLogPage(Page<OperationLog> page, String operationType, String moduleName) {
        LambdaQueryWrapper<OperationLog> wrapper = new LambdaQueryWrapper<>();

        if (operationType != null && !operationType.isEmpty()) {
            wrapper.eq(OperationLog::getOperationType, operationType);
        }

        if (moduleName != null && !moduleName.isEmpty()) {
            wrapper.eq(OperationLog::getModule, moduleName);
        }

        wrapper.orderByDesc(OperationLog::getCreateTime);

        return this.page(page, wrapper);
    }
}
