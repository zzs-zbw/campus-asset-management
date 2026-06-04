package com.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.entity.AssetCategory;
import com.entity.AssetImportDTO;
import com.entity.AssetInfo;
import com.service.AssetCategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExcelUtil {

    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    @Resource
    private AssetCategoryService assetCategoryService;

    private static final DateTimeFormatter DATE_FMT1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_FMT2 = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final DateTimeFormatter DATE_FMT3 = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    private static final Long DEFAULT_CATEGORY_ID = 1L;

    public void exportAssetList(HttpServletResponse response, List<AssetInfo> list) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("资产列表", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        EasyExcel.write(response.getOutputStream(), AssetInfo.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("资产列表")
                .doWrite(list);
    }

    public void exportTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("资产导入模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        List<AssetImportDTO> data = new ArrayList<>();
        AssetImportDTO template = new AssetImportDTO();
        template.setAssetCode("ZC2026001");
        template.setAssetName("示例资产");
        template.setCategoryName("计算机设备");
        template.setBrand("联想");
        template.setModel("ThinkCentre");
        template.setSerialNumber("SN2026001");
        template.setPurchasePrice("4500.00");
        template.setTotalValue("4500.00");
        template.setPurchaseDate("2026-01-15");
        template.setWarrantyEndDate("2028-01-15");
        template.setBuilding("实训楼A");
        template.setFloor("3楼");
        template.setRoomNumber("301机房");
        template.setStatusText("正常");
        template.setResponsiblePerson("张老师");
        template.setRemark("示例数据，请删除后填写实际数据");
        data.add(template);

        EasyExcel.write(response.getOutputStream(), AssetImportDTO.class)
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .sheet("资产导入模板")
                .doWrite(data);
    }

    public List<AssetInfo> importAssetList(InputStream inputStream) {
        Map<String, Long> categoryMap = buildCategoryMap();

        List<AssetInfo> result = new ArrayList<>();

        EasyExcel.read(inputStream, AssetImportDTO.class, new AnalysisEventListener<AssetImportDTO>() {
            @Override
            public void invoke(AssetImportDTO dto, AnalysisContext context) {
                AssetInfo asset = convertToAssetInfo(dto, categoryMap);
                if (asset != null) {
                    result.add(asset);
                }
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext context) {
                log.info("Excel数据读取完成，共解析 {} 条有效数据", result.size());
            }
        }).sheet().headRowNumber(1).doRead();

        return result;
    }

    private Map<String, Long> buildCategoryMap() {
        Map<String, Long> map = new HashMap<>();
        List<AssetCategory> categories = assetCategoryService.list();
        for (AssetCategory cat : categories) {
            map.put(cat.getCategoryName(), cat.getId());
            if (StringUtils.hasText(cat.getCategoryCode())) {
                map.put(cat.getCategoryCode(), cat.getId());
            }
        }
        return map;
    }

    private AssetInfo convertToAssetInfo(AssetImportDTO dto, Map<String, Long> categoryMap) {
        if (!StringUtils.hasText(dto.getAssetName())) {
            log.warn("跳过无资产名称的行");
            return null;
        }

        AssetInfo asset = new AssetInfo();
        asset.setAssetName(dto.getAssetName().trim());
        asset.setAssetCode(StringUtils.hasText(dto.getAssetCode()) ? dto.getAssetCode().trim() : null);
        asset.setBrand(StringUtils.hasText(dto.getBrand()) ? dto.getBrand().trim() : null);
        asset.setModel(StringUtils.hasText(dto.getModel()) ? dto.getModel().trim() : null);
        asset.setSerialNumber(StringUtils.hasText(dto.getSerialNumber()) ? dto.getSerialNumber().trim() : null);
        asset.setLocation(StringUtils.hasText(dto.getLocation()) ? dto.getLocation().trim() : null);
        asset.setResponsiblePerson(StringUtils.hasText(dto.getResponsiblePerson()) ? dto.getResponsiblePerson().trim() : null);
        asset.setRemark(StringUtils.hasText(dto.getRemark()) ? dto.getRemark().trim() : null);

        Long categoryId = resolveCategoryId(dto.getCategoryName(), categoryMap);
        asset.setCategoryId(categoryId != null ? categoryId : DEFAULT_CATEGORY_ID);

        asset.setPurchasePrice(parseBigDecimal(dto.getPurchasePrice()));
        BigDecimal totalValue = parseBigDecimal(dto.getTotalValue());
        asset.setTotalValue(totalValue != null ? totalValue : BigDecimal.ZERO);

        asset.setPurchaseDate(parseLocalDate(dto.getPurchaseDate()));
        asset.setWarrantyEndDate(parseLocalDate(dto.getWarrantyEndDate()));

        asset.setBuilding(StringUtils.hasText(dto.getBuilding()) ? dto.getBuilding().trim() : null);
        asset.setFloor(StringUtils.hasText(dto.getFloor()) ? dto.getFloor().trim() : null);
        asset.setRoomNumber(StringUtils.hasText(dto.getRoomNumber()) ? dto.getRoomNumber().trim() : null);

        asset.setStatus(resolveStatus(dto.getStatusText()));

        if (!StringUtils.hasText(dto.getLocation()) && StringUtils.hasText(dto.getBuilding())) {
            StringBuilder loc = new StringBuilder(dto.getBuilding().trim());
            if (StringUtils.hasText(dto.getFloor())) {
                loc.append(" ").append(dto.getFloor().trim());
            }
            if (StringUtils.hasText(dto.getRoomNumber())) {
                loc.append(" ").append(dto.getRoomNumber().trim());
            }
            asset.setLocation(loc.toString());
        }

        return asset;
    }

    private Long resolveCategoryId(String categoryName, Map<String, Long> categoryMap) {
        if (!StringUtils.hasText(categoryName)) {
            return DEFAULT_CATEGORY_ID;
        }
        String trimmed = categoryName.trim();
        if (categoryMap.containsKey(trimmed)) {
            return categoryMap.get(trimmed);
        }
        for (Map.Entry<String, Long> entry : categoryMap.entrySet()) {
            if (entry.getKey() != null && entry.getKey().contains(trimmed)) {
                return entry.getValue();
            }
        }
        try {
            return Long.parseLong(trimmed);
        } catch (NumberFormatException e) {
            log.warn("无法识别的分类: {}，使用默认分类ID: {}", trimmed, DEFAULT_CATEGORY_ID);
            return DEFAULT_CATEGORY_ID;
        }
    }

    private Integer resolveStatus(String statusText) {
        if (!StringUtils.hasText(statusText)) {
            return 1;
        }
        String trimmed = statusText.trim();
        switch (trimmed) {
            case "正常": return 1;
            case "维修中": return 2;
            case "闲置": return 3;
            case "已报废": return 4;
            default:
                try {
                    int val = Integer.parseInt(trimmed);
                    if (val >= 1 && val <= 4) return val;
                    return 1;
                } catch (NumberFormatException e) {
                    log.warn("无法识别的状态: {}，默认设为正常", trimmed);
                    return 1;
                }
        }
    }

    private BigDecimal parseBigDecimal(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            String cleaned = value.trim().replaceAll("[,，\\s]", "");
            return new BigDecimal(cleaned);
        } catch (NumberFormatException e) {
            log.warn("无法解析金额: {}", value);
            return null;
        }
    }

    private LocalDate parseLocalDate(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        String trimmed = value.trim();
        DateTimeFormatter[] formatters = {DATE_FMT1, DATE_FMT2, DATE_FMT3};
        for (DateTimeFormatter fmt : formatters) {
            try {
                return LocalDate.parse(trimmed, fmt);
            } catch (Exception ignored) {
            }
        }
        try {
            return LocalDate.parse(trimmed);
        } catch (Exception e) {
            log.warn("无法解析日期: {}", value);
            return null;
        }
    }
}
