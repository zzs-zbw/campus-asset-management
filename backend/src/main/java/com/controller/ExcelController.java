package com.controller;

import com.common.Result;
import com.entity.AssetInfo;
import com.service.AssetInfoService;
import com.util.ExcelUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/excel")
@CrossOrigin
public class ExcelController {

    private static final Logger log = LoggerFactory.getLogger(ExcelController.class);

    @Resource
    private ExcelUtil excelUtil;

    @Resource
    private AssetInfoService assetInfoService;

    @GetMapping("/export/template")
    public void exportTemplate(HttpServletResponse response) throws IOException {
        excelUtil.exportTemplate(response);
    }

    @GetMapping("/export/assets")
    public void exportAssets(HttpServletResponse response) throws IOException {
        List<AssetInfo> list = assetInfoService.list();
        excelUtil.exportAssetList(response, list);
    }

    @PostMapping("/import/assets")
    public Result<String> importAssets(@RequestParam("file") MultipartFile file,
                                        @RequestParam(defaultValue = "append") String mode) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || (!originalFilename.endsWith(".xlsx") && !originalFilename.endsWith(".xls"))) {
            return Result.error("请上传Excel文件（.xlsx或.xls格式）");
        }

        List<AssetInfo> list;
        try {
            list = excelUtil.importAssetList(file.getInputStream());
        } catch (Exception e) {
            log.error("解析Excel文件失败: ", e);
            return Result.error("解析Excel文件失败: " + e.getMessage());
        }

        if (list.isEmpty()) {
            return Result.error("未读取到有效数据，请检查Excel文件内容");
        }

        int successCount = 0;
        int failCount = 0;
        StringBuilder failReasons = new StringBuilder();

        Set<String> existingCodes = new HashSet<>();
        List<AssetInfo> existingAssets = assetInfoService.list();
        for (AssetInfo a : existingAssets) {
            if (a.getAssetCode() != null) {
                existingCodes.add(a.getAssetCode());
            }
        }

        try {
            if ("overwrite".equals(mode)) {
                assetInfoService.remove(null);
                existingCodes.clear();
            }

            for (AssetInfo asset : list) {
                try {
                    if (asset.getAssetCode() == null || asset.getAssetCode().isEmpty()) {
                        asset.setAssetCode(generateUniqueAssetCode(existingCodes));
                    } else if (existingCodes.contains(asset.getAssetCode())) {
                        if ("merge".equals(mode)) {
                            AssetInfo existing = assetInfoService.lambdaQuery()
                                    .eq(AssetInfo::getAssetCode, asset.getAssetCode())
                                    .one();
                            if (existing != null) {
                                asset.setId(existing.getId());
                                assetInfoService.updateById(asset);
                                successCount++;
                                continue;
                            }
                        } else if ("update".equals(mode)) {
                            AssetInfo existing = assetInfoService.lambdaQuery()
                                    .eq(AssetInfo::getAssetCode, asset.getAssetCode())
                                    .one();
                            if (existing != null) {
                                asset.setId(existing.getId());
                                assetInfoService.updateById(asset);
                                successCount++;
                            } else {
                                failCount++;
                            }
                            continue;
                        } else {
                            asset.setAssetCode(generateUniqueAssetCode(existingCodes));
                        }
                    }

                    if (asset.getCategoryId() == null) {
                        asset.setCategoryId(1L);
                    }
                    if (asset.getStatus() == null) {
                        asset.setStatus(1);
                    }
                    if (asset.getTotalValue() == null) {
                        asset.setTotalValue(java.math.BigDecimal.ZERO);
                    }

                    assetInfoService.save(asset);
                    existingCodes.add(asset.getAssetCode());
                    successCount++;
                } catch (Exception e) {
                    log.error("保存资产失败 [{}]: {}", asset.getAssetName(), e.getMessage());
                    failCount++;
                    if (failCount <= 5) {
                        failReasons.append("[").append(asset.getAssetName()).append("]: ").append(e.getMessage()).append("; ");
                    }
                }
            }
        } catch (Exception e) {
            log.error("导入数据异常: ", e);
            return Result.error("导入数据异常: " + e.getMessage());
        }

        String msg = "共读取 " + list.size() + " 条数据，成功 " + successCount + " 条";
        if (failCount > 0) {
            msg += "，失败 " + failCount + " 条";
            if (failReasons.length() > 0) {
                msg += "。失败原因：" + failReasons.toString();
            }
        }
        return Result.success(msg);
    }

    private String generateUniqueAssetCode(Set<String> existingCodes) {
        long count = assetInfoService.count() + 1;
        String code;
        int maxRetry = 100;
        do {
            code = "ZC" + String.format("%06d", count);
            count++;
            maxRetry--;
        } while (existingCodes.contains(code) && maxRetry > 0);
        return code;
    }
}
