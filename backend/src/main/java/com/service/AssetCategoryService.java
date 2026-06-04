package com.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.AssetCategory;

import java.util.List;

public interface AssetCategoryService extends IService<AssetCategory> {

    List<AssetCategory> getAllActiveCategories();
}