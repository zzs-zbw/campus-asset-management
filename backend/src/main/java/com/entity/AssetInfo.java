package com.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("asset_info")
public class AssetInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @ExcelIgnore
    private Long id;

    @ExcelProperty(value = "资产编号", index = 0)
    @ColumnWidth(15)
    private String assetCode;

    @ExcelProperty(value = "资产名称", index = 1)
    @ColumnWidth(25)
    private String assetName;

    @ExcelProperty(value = "分类ID", index = 2)
    @ColumnWidth(10)
    private Long categoryId;

    @ExcelProperty(value = "品牌", index = 3)
    @ColumnWidth(15)
    private String brand;

    @ExcelProperty(value = "型号", index = 4)
    @ColumnWidth(20)
    private String model;

    @ExcelProperty(value = "序列号", index = 5)
    @ColumnWidth(20)
    @TableField(exist = false)
    private String serialNumber;

    @ExcelProperty(value = "采购日期", index = 6)
    @ColumnWidth(15)
    private LocalDate purchaseDate;

    @ExcelProperty(value = "采购单价", index = 7)
    @ColumnWidth(15)
    private BigDecimal purchasePrice;

    @ExcelProperty(value = "总价值", index = 8)
    @ColumnWidth(15)
    private BigDecimal totalValue;

    @ExcelProperty(value = "保修截止日期", index = 9)
    @ColumnWidth(15)
    @TableField(exist = false)
    private LocalDate warrantyEndDate;

    @ExcelProperty(value = "所属楼宇", index = 10)
    @ColumnWidth(12)
    @TableField(exist = false)
    private String building;

    @ExcelProperty(value = "楼层", index = 11)
    @ColumnWidth(10)
    @TableField(exist = false)
    private String floor;

    @ExcelProperty(value = "机房编号", index = 12)
    @ColumnWidth(12)
    @TableField(exist = false)
    private String roomNumber;

    @ExcelProperty(value = "存放位置", index = 13)
    @ColumnWidth(30)
    private String location;

    @ExcelProperty(value = "使用状态", index = 14)
    @ColumnWidth(10)
    private Integer status;

    @ExcelProperty(value = "责任人", index = 15)
    @ColumnWidth(12)
    private String responsiblePerson;

    @ExcelProperty(value = "备注说明", index = 16)
    @ColumnWidth(40)
    private String remark;

    @TableField(exist = false)
    @ExcelIgnore
    private Long creatorId;

    @TableField(exist = false)
    @ExcelIgnore
    private String categoryName;

    @TableField(exist = false)
    @ExcelIgnore
    private String statusText;

    @ExcelIgnore
    private LocalDateTime createTime;

    @ExcelIgnore
    private LocalDateTime updateTime;
}
