package com.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.AssetCategory;
import com.mapper.AssetCategoryMapper;
import com.service.AssetCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetCategoryServiceImpl extends ServiceImpl<AssetCategoryMapper, AssetCategory> implements AssetCategoryService {

    @Override
    public List<AssetCategory> getAllActiveCategories() {
        LambdaQueryWrapper<AssetCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AssetCategory::getStatus, 1);
        wrapper.orderByAsc(AssetCategory::getSortOrder);
        return this.list(wrapper);
    }
}