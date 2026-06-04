package com.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.PageResult;
import com.common.Result;
import com.entity.AssetInfo;
import com.service.AssetInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/asset")
@CrossOrigin
public class AssetInfoController {

    @Resource
    private AssetInfoService assetInfoService;

    @GetMapping("/page")
    public Result<PageResult<AssetInfo>> page(@RequestParam(defaultValue = "1") Integer current,
                                               @RequestParam(defaultValue = "10") Integer size,
                                               @RequestParam(required = false) String keyword,
                                               @RequestParam(required = false) Long categoryId,
                                               @RequestParam(required = false) Integer status,
                                               @RequestParam(required = false) String building,
                                               @RequestParam(required = false) String floor,
                                               @RequestParam(required = false) String roomNumber) {
        Page<AssetInfo> page = new Page<>(current, size);
        IPage<AssetInfo> result = assetInfoService.getAssetPage(page, keyword, categoryId, status, building, floor, roomNumber);
        return Result.success(PageResult.of(result.getTotal(), result.getRecords()));
    }

    @GetMapping("/list")
    public Result<List<AssetInfo>> list() {
        return Result.success(assetInfoService.list());
    }

    @GetMapping("/{id}")
    public Result<AssetInfo> getById(@PathVariable Long id) {
        return Result.success(assetInfoService.getById(id));
    }

    @GetMapping("/code/{code}")
    public Result<AssetInfo> getByCode(@PathVariable String code) {
        return Result.success(assetInfoService.lambdaQuery().eq(AssetInfo::getAssetCode, code).one());
    }

    @PostMapping
    public Result<String> add(@RequestBody AssetInfo asset) {
        assetInfoService.save(asset);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody AssetInfo asset) {
        assetInfoService.updateById(asset);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        assetInfoService.removeById(id);
        return Result.success("删除成功");
    }

    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestBody List<Long> ids) {
        assetInfoService.removeByIds(ids);
        return Result.success("批量删除成功");
    }

    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(assetInfoService.getStatistics());
    }

    @GetMapping("/category-statistics")
    public Result<List<Map<String, Object>>> getCategoryStatistics() {
        return Result.success(assetInfoService.getCategoryStatistics());
    }

    @GetMapping("/maintenance-statistics")
    public Result<List<Map<String, Object>>> getMaintenanceStatistics() {
        return Result.success(assetInfoService.getMaintenanceStatistics());
    }

    @GetMapping("/generate-code")
    public Result<String> generateCode() {
        return Result.success(assetInfoService.generateAssetCode());
    }
}