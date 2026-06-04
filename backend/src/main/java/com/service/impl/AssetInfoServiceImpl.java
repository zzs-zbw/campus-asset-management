package com.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.entity.AssetInfo;
import com.mapper.AssetInfoMapper;
import com.service.AssetInfoService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AssetInfoServiceImpl extends ServiceImpl<AssetInfoMapper, AssetInfo> implements AssetInfoService {

    @Override
    public IPage<AssetInfo> getAssetPage(Page<AssetInfo> page, String keyword, Long categoryId, Integer status,
                                          String building, String floor, String roomNumber) {
        List<AssetInfo> records = baseMapper.selectAssetDetailList(keyword, categoryId, status, building, floor, roomNumber);

        int start = (int) ((page.getCurrent() - 1) * page.getSize());
        int end = Math.min(start + (int) page.getSize(), records.size());

        List<AssetInfo> pageRecords = records.subList(start, end);

        Page<AssetInfo> resultPage = new Page<>(page.getCurrent(), page.getSize(), records.size());
        resultPage.setRecords(pageRecords);

        return resultPage;
    }

    @Override
    public Map<String, Object> getStatistics() {
        return baseMapper.getStatistics();
    }

    @Override
    public List<Map<String, Object>> getCategoryStatistics() {
        return baseMapper.getCategoryStatistics();
    }

    @Override
    public List<Map<String, Object>> getMaintenanceStatistics() {
        return baseMapper.getMaintenanceStatistics();
    }

    @Override
    public String generateAssetCode() {
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String prefix = "ZC" + datePart;

        long count = this.count() + 1;
        String seq = String.format("%04d", count);

        return prefix + seq;
    }
}
