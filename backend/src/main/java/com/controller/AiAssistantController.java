package com.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.Result;
import com.entity.AssetCategory;
import com.entity.AssetInfo;
import com.entity.MaintenanceRecord;
import com.entity.SysUser;
import com.service.AssetCategoryService;
import com.service.AssetInfoService;
import com.service.MaintenanceRecordService;
import com.service.SysUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/ai")
@CrossOrigin
public class AiAssistantController {

    private static final Logger log = LoggerFactory.getLogger(AiAssistantController.class);

    @Resource
    private AssetInfoService assetInfoService;

    @Resource
    private AssetCategoryService assetCategoryService;

    @Resource
    private MaintenanceRecordService maintenanceRecordService;

    @Resource
    private SysUserService sysUserService;

    @PostMapping("/chat")
    public Result<Map<String, Object>> chat(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        if (question == null || question.trim().isEmpty()) {
            return Result.error("请输入问题");
        }

        question = question.trim();
        String answer = processQuestion(question);

        Map<String, Object> data = new HashMap<>();
        data.put("answer", answer);
        data.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return Result.success(data);
    }

    private String processQuestion(String question) {
        String q = question.toLowerCase();

        if (containsAny(q, "你好", "您好", "嗨", "hi", "hello", "你是谁", "介绍")) {
            return greet();
        }
        if (containsAny(q, "资产总数", "多少资产", "资产数量", "总共有多少", "共有多少", "设备总数", "设备数量")) {
            return answerAssetCount();
        }
        if (containsAny(q, "资产总值", "总价值", "总金额", "价值多少", "多少钱", "资产价值")) {
            return answerAssetValue();
        }
        if (containsAny(q, "统计", "概览", "概况", "数据总览", "总体情况")) {
            return answerStatistics();
        }
        if (containsAny(q, "分类统计", "各类", "分类情况", "分类数量", "每种分类", "各分类")) {
            return answerCategoryStatistics();
        }
        if (containsAny(q, "维修", "故障", "报修", "损坏")) {
            return answerMaintenance(question);
        }
        if (containsAny(q, "闲置", "未使用", "空闲")) {
            return answerIdleAssets();
        }
        if (containsAny(q, "报废", "淘汰", "已报废")) {
            return answerScrappedAssets();
        }
        if (containsAny(q, "正常", "状态正常", "可用")) {
            return answerNormalAssets();
        }
        if (containsAny(q, "分类", "有哪些分类", "类别", "类型")) {
            return answerCategories();
        }
        if (containsAny(q, "用户", "管理员", "人员", "员工", "谁在管")) {
            return answerUsers();
        }
        if (containsAny(q, "楼宇", "楼", "实训楼", "教学楼", "建筑")) {
            return answerBuildingAssets(question);
        }
        if (containsAny(q, "品牌", "联想", "戴尔", "华为", "惠普", "苹果")) {
            return answerBrandAssets(question);
        }
        if (containsAny(q, "最贵", "最值钱", "价值最高", "单价最高")) {
            return answerMostExpensive();
        }
        if (containsAny(q, "最近", "最新", "新添加", "新入库")) {
            return answerRecentAssets();
        }
        if (containsAny(q, "责任人", "负责人", "谁负责", "归属")) {
            return answerResponsiblePerson(question);
        }
        if (containsAny(q, "采购", "购买", "购置", "入库")) {
            return answerPurchaseInfo();
        }
        if (containsAny(q, "折旧", "贬值", "老化")) {
            return answerDepreciation();
        }
        if (containsAny(q, "盘点", "清查", "核查")) {
            return answerInventory();
        }
        if (containsAny(q, "帮助", "能做什么", "功能", "怎么用", "使用说明")) {
            return answerHelp();
        }
        if (containsAny(q, "导出", "导入", "excel", "批量")) {
            return answerImportExport();
        }
        if (containsAny(q, "二维码", "扫码", "扫描")) {
            return answerQrCode();
        }

        return answerByKeyword(question);
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String greet() {
        return "您好！我是校园实训楼固定资产管理系统的AI助手。\n\n" +
                "我可以帮您查询和分析资产数据，例如：\n" +
                "📊 输入\"资产统计\"查看整体数据\n" +
                "🔢 输入\"资产总数\"查看数量\n" +
                "💰 输入\"资产总值\"查看价值\n" +
                "🔧 输入\"维修情况\"查看报修信息\n" +
                "🏢 输入\"楼宇分布\"查看楼宇资产\n" +
                "📋 输入\"帮助\"查看更多功能\n\n" +
                "请问有什么可以帮您的？";
    }

    private String answerAssetCount() {
        long total = assetInfoService.count();
        long normal = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 1));
        long maintenance = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 2));
        long idle = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 3));
        long scrapped = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 4));

        return String.format("📊 资产总数统计：\n\n" +
                "📦 资产总数：%d 台/件\n" +
                "✅ 正常使用：%d 台/件\n" +
                "🔧 维修中：%d 台/件\n" +
                "💤 闲置：%d 台/件\n" +
                "❌ 已报废：%d 台/件\n\n" +
                "资产完好率：%.1f%%", total, normal, maintenance, idle, scrapped,
                total > 0 ? (double) normal / total * 100 : 0);
    }

    private String answerAssetValue() {
        List<AssetInfo> allAssets = assetInfoService.list();
        BigDecimal totalValue = allAssets.stream()
                .map(AssetInfo::getTotalValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal purchaseValue = allAssets.stream()
                .map(AssetInfo::getPurchasePrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return String.format("💰 资产价值统计：\n\n" +
                "💵 资产总价值：¥%,.2f\n" +
                "🛒 采购总金额：¥%,.2f\n" +
                "📦 资产总数：%d 台/件\n" +
                "📊 平均单价：¥%,.2f",
                totalValue, purchaseValue, allAssets.size(),
                allAssets.size() > 0 ? totalValue.divide(new BigDecimal(allAssets.size()), 2, BigDecimal.ROUND_HALF_UP) : BigDecimal.ZERO);
    }

    private String answerStatistics() {
        long total = assetInfoService.count();
        long normal = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 1));
        long maintenance = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 2));
        long idle = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 3));
        long scrapped = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 4));

        List<AssetInfo> allAssets = assetInfoService.list();
        BigDecimal totalValue = allAssets.stream()
                .map(AssetInfo::getTotalValue)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long maintenanceCount = maintenanceRecordService.count();
        long pendingMaintenance = maintenanceRecordService.count(new LambdaQueryWrapper<MaintenanceRecord>().eq(MaintenanceRecord::getStatus, 1));

        return String.format("📊 系统数据总览：\n\n" +
                "【资产概况】\n" +
                "📦 总数：%d | ✅ 正常：%d | 🔧 维修：%d | 💤 闲置：%d | ❌ 报废：%d\n" +
                "💰 总价值：¥%,.2f\n" +
                "📈 完好率：%.1f%%\n\n" +
                "【维修概况】\n" +
                "🔧 维修记录：%d 条\n" +
                "⚠️ 待处理工单：%d 条",
                total, normal, maintenance, idle, scrapped, totalValue,
                total > 0 ? (double) normal / total * 100 : 0,
                maintenanceCount, pendingMaintenance);
    }

    private String answerCategoryStatistics() {
        List<AssetCategory> categories = assetCategoryService.list();
        StringBuilder sb = new StringBuilder("📋 分类资产统计：\n\n");

        for (AssetCategory cat : categories) {
            long count = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getCategoryId, cat.getId()));
            sb.append(String.format("🏷️ %s（%s）：%d 台/件\n", cat.getCategoryName(), cat.getCategoryCode(), count));
        }

        long uncategorized = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().isNull(AssetInfo::getCategoryId));
        if (uncategorized > 0) {
            sb.append(String.format("❓ 未分类：%d 台/件\n", uncategorized));
        }

        return sb.toString();
    }

    private String answerMaintenance(String question) {
        long total = maintenanceRecordService.count();
        long pending = maintenanceRecordService.count(new LambdaQueryWrapper<MaintenanceRecord>().eq(MaintenanceRecord::getStatus, 1));
        long processing = maintenanceRecordService.count(new LambdaQueryWrapper<MaintenanceRecord>().eq(MaintenanceRecord::getStatus, 2));
        long completed = maintenanceRecordService.count(new LambdaQueryWrapper<MaintenanceRecord>().eq(MaintenanceRecord::getStatus, 3));
        long unfixable = maintenanceRecordService.count(new LambdaQueryWrapper<MaintenanceRecord>().eq(MaintenanceRecord::getStatus, 4));

        List<MaintenanceRecord> recentRecords = maintenanceRecordService.list(
                new LambdaQueryWrapper<MaintenanceRecord>().orderByDesc(MaintenanceRecord::getCreateTime).last("LIMIT 5"));

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("🔧 维修情况统计：\n\n" +
                "📝 总工单：%d 条\n" +
                "⚠️ 待维修：%d 条\n" +
                "🔄 维修中：%d 条\n" +
                "✅ 已修好：%d 条\n" +
                "❌ 无法修复：%d 条\n", total, pending, processing, completed, unfixable));

        if (!recentRecords.isEmpty()) {
            sb.append("\n📌 最近报修记录：\n");
            for (MaintenanceRecord r : recentRecords) {
                String statusText = getStatusText(r.getStatus());
                sb.append(String.format("  · %s - %s [%s]\n",
                        r.getAssetCode() != null ? r.getAssetCode() : "未知",
                        r.getFaultDescription() != null ? r.getFaultDescription() : "无描述",
                        statusText));
            }
        }

        return sb.toString();
    }

    private String answerIdleAssets() {
        List<AssetInfo> idleAssets = assetInfoService.list(
                new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 3).last("LIMIT 20"));

        StringBuilder sb = new StringBuilder(String.format("💤 闲置资产列表（共 %d 条，显示前20条）：\n\n", idleAssets.size()));
        for (AssetInfo a : idleAssets) {
            sb.append(String.format("  · [%s] %s - %s | 位置：%s\n",
                    a.getAssetCode(), a.getAssetName(),
                    a.getBrand() != null ? a.getBrand() : "",
                    a.getLocation() != null ? a.getLocation() : "未登记"));
        }

        if (idleAssets.isEmpty()) {
            sb.append("暂无闲置资产，所有资产都在使用中。");
        }

        return sb.toString();
    }

    private String answerScrappedAssets() {
        long count = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 4));
        return String.format("❌ 已报废资产：%d 台/件\n\n如需查看详细列表，请在资产管理页面筛选\"已报废\"状态。", count);
    }

    private String answerNormalAssets() {
        long count = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().eq(AssetInfo::getStatus, 1));
        long total = assetInfoService.count();
        return String.format("✅ 正常使用资产：%d 台/件\n📊 占比：%.1f%%", count, total > 0 ? (double) count / total * 100 : 0);
    }

    private String answerCategories() {
        List<AssetCategory> categories = assetCategoryService.list();
        StringBuilder sb = new StringBuilder("🏷️ 系统资产分类：\n\n");
        for (AssetCategory cat : categories) {
            sb.append(String.format("  · %s（编码：%s，排序：%d）\n", cat.getCategoryName(), cat.getCategoryCode(), cat.getSortOrder()));
        }
        return sb.toString();
    }

    private String answerUsers() {
        List<SysUser> users = sysUserService.list();
        long adminCount = users.stream().filter(u -> u.getRole() != null && u.getRole() == 1).count();
        long schoolAdminCount = users.stream().filter(u -> u.getRole() != null && u.getRole() == 2).count();
        long deptAdminCount = users.stream().filter(u -> u.getRole() != null && u.getRole() == 3).count();
        long warehouseCount = users.stream().filter(u -> u.getRole() != null && u.getRole() == 4).count();
        long maintenanceCount = users.stream().filter(u -> u.getRole() != null && u.getRole() == 5).count();
        long financeCount = users.stream().filter(u -> u.getRole() != null && u.getRole() == 6).count();
        long normalCount = users.stream().filter(u -> u.getRole() != null && u.getRole() == 7).count();

        return String.format("👥 系统用户统计：\n\n" +
                "👤 总用户数：%d\n" +
                "🔑 超级管理员：%d 人\n" +
                "🏫 校级资产负责人：%d 人\n" +
                "🏢 部门资产管理员：%d 人\n" +
                "📦 仓库管理员：%d 人\n" +
                "🔧 维修运维员：%d 人\n" +
                "💰 财务审核员：%d 人\n" +
                "🙋 普通使用人：%d 人",
                users.size(), adminCount, schoolAdminCount, deptAdminCount,
                warehouseCount, maintenanceCount, financeCount, normalCount);
    }

    private String answerBuildingAssets(String question) {
        List<AssetInfo> allAssets = assetInfoService.list();
        Map<String, Long> buildingMap = new LinkedHashMap<>();

        for (AssetInfo a : allAssets) {
            String loc = a.getLocation();
            if (loc != null && !loc.isEmpty()) {
                String building = loc.split("[栋楼座 ]")[0].trim();
                if (building.isEmpty()) building = loc.trim();
                buildingMap.merge(building, 1L, Long::sum);
            }
        }

        if (buildingMap.isEmpty()) {
            return "🏢 暂无楼宇分布数据。请在资产信息中填写存放位置。";
        }

        StringBuilder sb = new StringBuilder("🏢 楼宇资产分布：\n\n");
        buildingMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(e -> sb.append(String.format("  · %s：%d 台/件\n", e.getKey(), e.getValue())));

        return sb.toString();
    }

    private String answerBrandAssets(String question) {
        List<AssetInfo> allAssets = assetInfoService.list();
        Map<String, Long> brandMap = new LinkedHashMap<>();

        for (AssetInfo a : allAssets) {
            if (a.getBrand() != null && !a.getBrand().isEmpty()) {
                brandMap.merge(a.getBrand().trim(), 1L, Long::sum);
            }
        }

        if (brandMap.isEmpty()) {
            return "🏷️ 暂无品牌分布数据。";
        }

        StringBuilder sb = new StringBuilder("🏷️ 品牌资产分布（Top 10）：\n\n");
        brandMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(e -> sb.append(String.format("  · %s：%d 台/件\n", e.getKey(), e.getValue())));

        return sb.toString();
    }

    private String answerMostExpensive() {
        List<AssetInfo> topAssets = assetInfoService.list(
                new LambdaQueryWrapper<AssetInfo>()
                        .isNotNull(AssetInfo::getPurchasePrice)
                        .orderByDesc(AssetInfo::getPurchasePrice)
                        .last("LIMIT 10"));

        if (topAssets.isEmpty()) {
            return "暂无价格数据。";
        }

        StringBuilder sb = new StringBuilder("💎 价值最高的资产（Top 10）：\n\n");
        for (AssetInfo a : topAssets) {
            sb.append(String.format("  · [%s] %s - ¥%,.2f\n",
                    a.getAssetCode(), a.getAssetName(), a.getPurchasePrice()));
        }

        return sb.toString();
    }

    private String answerRecentAssets() {
        List<AssetInfo> recentAssets = assetInfoService.list(
                new LambdaQueryWrapper<AssetInfo>().orderByDesc(AssetInfo::getCreateTime).last("LIMIT 10"));

        StringBuilder sb = new StringBuilder("🆕 最近入库资产：\n\n");
        for (AssetInfo a : recentAssets) {
            sb.append(String.format("  · [%s] %s | %s | 入库时间：%s\n",
                    a.getAssetCode(), a.getAssetName(),
                    a.getBrand() != null ? a.getBrand() : "",
                    a.getCreateTime() != null ? a.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "未知"));
        }

        return sb.toString();
    }

    private String answerResponsiblePerson(String question) {
        List<AssetInfo> allAssets = assetInfoService.list();
        Map<String, Long> personMap = new LinkedHashMap<>();

        for (AssetInfo a : allAssets) {
            if (a.getResponsiblePerson() != null && !a.getResponsiblePerson().isEmpty()) {
                personMap.merge(a.getResponsiblePerson().trim(), 1L, Long::sum);
            }
        }

        if (personMap.isEmpty()) {
            return "暂无责任人数据。";
        }

        StringBuilder sb = new StringBuilder("👤 责任人资产分布（Top 10）：\n\n");
        personMap.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .forEach(e -> sb.append(String.format("  · %s：负责 %d 台/件\n", e.getKey(), e.getValue())));

        return sb.toString();
    }

    private String answerPurchaseInfo() {
        List<AssetInfo> allAssets = assetInfoService.list();
        int thisYear = LocalDate.now().getYear();
        long thisYearCount = allAssets.stream()
                .filter(a -> a.getPurchaseDate() != null && a.getPurchaseDate().getYear() == thisYear)
                .count();

        BigDecimal thisYearValue = allAssets.stream()
                .filter(a -> a.getPurchaseDate() != null && a.getPurchaseDate().getYear() == thisYear)
                .map(AssetInfo::getPurchasePrice)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return String.format("🛒 采购信息：\n\n" +
                "📅 %d年采购：%d 台/件\n" +
                "💰 %d年采购金额：¥%,.2f\n" +
                "📦 资产总数：%d 台/件",
                thisYear, thisYearCount, thisYear, thisYearValue, allAssets.size());
    }

    private String answerDepreciation() {
        List<AssetInfo> allAssets = assetInfoService.list();
        long oldAssets = allAssets.stream()
                .filter(a -> a.getPurchaseDate() != null && a.getPurchaseDate().isBefore(LocalDate.now().minusYears(5)))
                .count();

        return String.format("📉 折旧信息：\n\n" +
                "📦 资产总数：%d 台/件\n" +
                "⏰ 购置超过5年的资产：%d 台/件\n" +
                "💡 建议对老旧资产进行评估，考虑是否需要更新或报废。",
                allAssets.size(), oldAssets);
    }

    private String answerInventory() {
        long total = assetInfoService.count();
        long withLocation = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().isNotNull(AssetInfo::getLocation).ne(AssetInfo::getLocation, ""));
        long withResponsible = assetInfoService.count(new LambdaQueryWrapper<AssetInfo>().isNotNull(AssetInfo::getResponsiblePerson).ne(AssetInfo::getResponsiblePerson, ""));

        return String.format("📋 盘点信息：\n\n" +
                "📦 资产总数：%d 台/件\n" +
                "📍 已登记位置：%d 台/件（%.1f%%）\n" +
                "👤 已指定责任人：%d 台/件（%.1f%%）\n" +
                "💡 建议定期盘点，确保账实相符。",
                total, withLocation, total > 0 ? (double) withLocation / total * 100 : 0,
                withResponsible, total > 0 ? (double) withResponsible / total * 100 : 0);
    }

    private String answerImportExport() {
        return "📥📤 导入导出功能说明：\n\n" +
                "【导出数据】\n" +
                "在资产管理页面点击\"导出数据\"按钮，可将所有资产数据导出为Excel文件。\n\n" +
                "【下载模板】\n" +
                "点击\"下载模板\"获取标准导入模板，按模板格式填写数据。\n\n" +
                "【导入数据】\n" +
                "支持4种导入模式：\n" +
                "· 追加模式：将数据添加到现有数据中\n" +
                "· 合并模式：按编号匹配，存在则更新，不存在则新增\n" +
                "· 更新模式：仅更新已存在的数据\n" +
                "· 覆盖模式：删除所有现有数据后导入（谨慎使用）\n\n" +
                "💡 提示：分类名称请填写系统中已有的分类（如：计算机设备、网络设备、办公设备、实验设备、家具）";
    }

    private String answerQrCode() {
        return "📱 二维码功能说明：\n\n" +
                "系统支持为每个资产生成唯一二维码，可打印贴在设备上。\n\n" +
                "使用方式：\n" +
                "1. 在资产管理页面查看资产详情\n" +
                "2. 每个资产都有唯一的资产编号\n" +
                "3. 可通过扫描二维码快速查看资产信息\n\n" +
                "💡 提示：将资产详情页面URL生成二维码，贴在设备上，扫码即可查看。";
    }

    private String answerHelp() {
        return "🤖 AI助手功能列表：\n\n" +
                "【数据查询】\n" +
                "· \"资产总数\" - 查看资产数量统计\n" +
                "· \"资产总值\" - 查看资产价值统计\n" +
                "· \"资产统计\" - 查看整体数据概览\n" +
                "· \"分类统计\" - 查看各分类资产数量\n" +
                "· \"维修情况\" - 查看维修工单统计\n" +
                "· \"闲置资产\" - 查看闲置资产列表\n" +
                "· \"最贵的资产\" - 查看价值Top10\n" +
                "· \"最近入库\" - 查看最新添加的资产\n\n" +
                "【分布查询】\n" +
                "· \"楼宇分布\" - 查看各楼宇资产分布\n" +
                "· \"品牌分布\" - 查看各品牌资产数量\n" +
                "· \"责任人\" - 查看各责任人资产数量\n\n" +
                "【管理咨询】\n" +
                "· \"采购信息\" - 查看采购数据\n" +
                "· \"折旧\" - 查看资产折旧情况\n" +
                "· \"盘点\" - 查看盘点完整度\n" +
                "· \"导入导出\" - 了解导入导出功能\n" +
                "· \"二维码\" - 了解二维码功能\n\n" +
                "💡 您也可以直接输入资产名称、编号或关键词来搜索资产。";
    }

    private String answerByKeyword(String question) {
        LambdaQueryWrapper<AssetInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(AssetInfo::getAssetName, question)
                .or().like(AssetInfo::getAssetCode, question)
                .or().like(AssetInfo::getBrand, question)
                .or().like(AssetInfo::getModel, question)
                .or().like(AssetInfo::getLocation, question)
                .or().like(AssetInfo::getResponsiblePerson, question)
                .or().like(AssetInfo::getRemark, question);

        List<AssetInfo> results = assetInfoService.list(wrapper.last("LIMIT 10"));

        if (results.isEmpty()) {
            return "抱歉，我没有找到与\"" + question + "\"相关的资产信息。\n\n" +
                    "您可以尝试：\n" +
                    "· 输入更具体的关键词\n" +
                    "· 输入\"帮助\"查看我能回答的问题类型\n" +
                    "· 在资产管理页面使用高级搜索功能";
        }

        StringBuilder sb = new StringBuilder(String.format("🔍 找到 %d 条相关资产：\n\n", results.size()));
        for (AssetInfo a : results) {
            sb.append(String.format("📦 [%s] %s\n" +
                            "   品牌：%s | 型号：%s\n" +
                            "   位置：%s | 状态：%s | 责任人：%s\n\n",
                    a.getAssetCode(), a.getAssetName(),
                    a.getBrand() != null ? a.getBrand() : "-",
                    a.getModel() != null ? a.getModel() : "-",
                    a.getLocation() != null ? a.getLocation() : "-",
                    getStatusText(a.getStatus()),
                    a.getResponsiblePerson() != null ? a.getResponsiblePerson() : "-"));
        }

        return sb.toString();
    }

    private String getStatusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 1: return "正常";
            case 2: return "维修中";
            case 3: return "闲置";
            case 4: return "已报废";
            default: return "未知";
        }
    }
}
