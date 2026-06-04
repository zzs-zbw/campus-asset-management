package com.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.OperationLog;

public interface OperationLogService extends IService<OperationLog> {

    IPage<OperationLog> getLogPage(Page<OperationLog> page, String operationType, String moduleName);
}