package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.MaintenanceRecord;
import com.mapper.MaintenanceRecordMapper;
import com.service.MaintenanceRecordService;
import org.springframework.stereotype.Service;

@Service
public class MaintenanceRecordServiceImpl extends ServiceImpl<MaintenanceRecordMapper, MaintenanceRecord> implements MaintenanceRecordService {

    @Override
    public IPage<MaintenanceRecord> getMaintenancePage(Page<MaintenanceRecord> page, String keyword, Integer status) {
        LambdaQueryWrapper<MaintenanceRecord> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(MaintenanceRecord::getAssetCode, keyword)
                    .or().like(MaintenanceRecord::getReporter, keyword));
        }
        
        if (status != null) {
            wrapper.eq(MaintenanceRecord::getStatus, status);
        }
        
        wrapper.orderByDesc(MaintenanceRecord::getReportTime);
        
        return this.page(page, wrapper);
    }
}