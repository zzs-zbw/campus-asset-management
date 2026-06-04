package com.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.entity.AssetInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface AssetInfoMapper extends BaseMapper<AssetInfo> {

    @Select("SELECT * FROM v_asset_statistics")
    Map<String, Object> getStatistics();

    @Select("SELECT * FROM v_category_statistics ORDER BY sort_order")
    List<Map<String, Object>> getCategoryStatistics();

    @Select("SELECT * FROM v_maintenance_statistics")
    List<Map<String, Object>> getMaintenanceStatistics();

    List<AssetInfo> selectAssetDetailList(@Param("keyword") String keyword,
                                           @Param("categoryId") Long categoryId,
                                           @Param("status") Integer status,
                                           @Param("building") String building,
                                           @Param("floor") String floor,
                                           @Param("roomNumber") String roomNumber);
}