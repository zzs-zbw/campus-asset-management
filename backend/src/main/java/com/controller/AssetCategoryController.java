package com.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.common.PageResult;
import com.common.Result;
import com.entity.AssetCategory;
import com.service.AssetCategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/category")
@CrossOrigin
public class AssetCategoryController {

    @Resource
    private AssetCategoryService assetCategoryService;

    @GetMapping("/page")
    public Result<PageResult<AssetCategory>> page(
        @RequestParam(defaultValue = "1") Integer current,
        @RequestParam(defaultValue = "10") Integer size) {
        Page<AssetCategory> page = new Page<>(current, size);
        IPage<AssetCategory> result = assetCategoryService.page(page);
        return Result.success(PageResult.of(result.getTotal(), result.getRecords()));
    }

    @GetMapping("/list")
    public Result<List<AssetCategory>> list() {
        List<AssetCategory> list = assetCategoryService.list();
        return Result.success(list);
    }

    @GetMapping("/active")
    public Result<List<AssetCategory>> getActiveCategories() {
        List<AssetCategory> list = assetCategoryService.getAllActiveCategories();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<AssetCategory> getById(@PathVariable Long id) {
        return Result.success(assetCategoryService.getById(id));
    }

    @PostMapping
    public Result<String> add(@RequestBody AssetCategory category) {
        assetCategoryService.save(category);
        return Result.success("添加成功");
    }

    @PutMapping
    public Result<String> update(@RequestBody AssetCategory category) {
        assetCategoryService.updateById(category);
        return Result.success("修改成功");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        assetCategoryService.removeById(id);
        return Result.success("删除成功");
    }
}