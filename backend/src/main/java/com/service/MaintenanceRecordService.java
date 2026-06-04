package com.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.MaintenanceRecord;

public interface MaintenanceRecordService extends IService<MaintenanceRecord> {

    IPage<MaintenanceRecord> getMaintenancePage(Page<MaintenanceRecord> page, String keyword, Integer status);
}