package com.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.entity.AssetInfo;

import java.util.List;
import java.util.Map;

public interface AssetInfoService extends IService<AssetInfo> {

    IPage<AssetInfo> getAssetPage(Page<AssetInfo> page, String keyword, Long categoryId, Integer status, 
                                   String building, String floor, String roomNumber);

    Map<String, Object> getStatistics();

    List<Map<String, Object>> getCategoryStatistics();

    List<Map<String, Object>> getMaintenanceStatistics();

    String generateAssetCode();
}