package com.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.PageResult;
import com.common.Result;
import com.entity.MaintenanceRecord;
import com.service.MaintenanceRecordService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/maintenance")
@CrossOrigin
public class MaintenanceRecordController {

    @Resource
    private MaintenanceRecordService maintenanceRecordService;

    @GetMapping("/page")
    public Result<PageResult<MaintenanceRecord>> page(@RequestParam(defaultValue = "1") Integer current,
                                                      @RequestParam(defaultValue = "10") Integer size,
                                                      @RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) Integer status) {
        Page<MaintenanceRecord> page = new Page<>(current, size);
        IPage<MaintenanceRecord> result = maintenanceRecordService.getMaintenancePage(page, keyword, status);
        return Result.success(PageResult.of(result.getTotal(), result.getRecords()));
    }

    @GetMapping("/list")
    public Result<List<MaintenanceRecord>> list() {
        return Result.success(maintenanceRecordService.list());
    }

    @GetMapping("/asset/{assetId}")
    public Result<List<MaintenanceRecord>> getByAssetId(@PathVariable Long assetId) {
        return Result.success(maintenanceRecordService.lambdaQuery()
                .eq(MaintenanceRecord::getAssetId, assetId)
                .orderByDesc(MaintenanceRecord::getReportTime)
                .list());
    }

    @GetMapping("/{id}")
    public Result<MaintenanceRecord> getById(@PathVariable Long id) {
        return Result.success(maintenanceRecordService.getById(id));
    }

    @PostMapping
    public Result<String> add(@RequestBody MaintenanceRecord record) {
        maintenanceRecordService.save(record);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody MaintenanceRecord record) {
        maintenanceRecordService.updateById(record);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        maintenanceRecordService.removeById(id);
        return Result.success("删除成功");
    }
}