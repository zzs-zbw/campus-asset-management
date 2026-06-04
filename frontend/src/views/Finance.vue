<template>
  <div class="finance">
    <el-card>
      <div class="toolbar">
        <h3>财务管理概览</h3>
      </div>

      <el-row :gutter="20" class="stat-cards">
        <el-col :span="6" :xs="12">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">资产总价值</div>
            <div class="stat-value">¥{{ statistics.totalValue || '0.00' }}</div>
          </el-card>
        </el-col>
        <el-col :span="6" :xs="12">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">年度维修费用</div>
            <div class="stat-value warning">¥{{ statistics.yearlyMaintenanceCost || '0.00' }}</div>
          </el-card>
        </el-col>
        <el-col :span="6" :xs="12">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">资产总数</div>
            <div class="stat-value primary">{{ statistics.totalCount || 0 }}</div>
          </el-card>
        </el-col>
        <el-col :span="6" :xs="12">
          <el-card shadow="hover" class="stat-card">
            <div class="stat-title">平均资产价值</div>
            <div class="stat-value success">¥{{ statistics.avgValue || '0.00' }}</div>
          </el-card>
        </el-col>
      </el-row>

      <el-divider>资产价值分布</el-divider>

      <el-table :data="categoryFinance" stripe class="finance-table">
        <el-table-column prop="categoryName" label="分类名称" width="180" />
        <el-table-column prop="count" label="资产数量" width="120" />
        <el-table-column prop="totalValue" label="总价值" width="150">
          <template #default="{ row }">
            ¥{{ row.totalValue || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="avgValue" label="平均价值" width="150">
          <template #default="{ row }">
            ¥{{ row.avgValue || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="maxValue" label="最高价值" width="150">
          <template #default="{ row }">
            ¥{{ row.maxValue || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="maintenanceCost" label="维修费用" width="150">
          <template #default="{ row }">
            ¥{{ row.maintenanceCost || '0.00' }}
          </template>
        </el-table-column>
      </el-table>

      <el-divider>维修费用明细</el-divider>

      <el-table :data="maintenanceList" stripe class="finance-table">
        <el-table-column prop="assetName" label="资产名称" min-width="150" />
        <el-table-column prop="assetCode" label="资产编号" width="120" />
        <el-table-column prop="faultDescription" label="故障描述" min-width="200" />
        <el-table-column prop="maintenanceCost" label="维修费用" width="120">
          <template #default="{ row }">
            ¥{{ row.maintenanceCost || '0.00' }}
          </template>
        </el-table-column>
        <el-table-column prop="reportTime" label="报修时间" width="180" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getMaintenanceStatusType(row.status)">{{ getMaintenanceStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getStatistics, getCategoryStatistics, getMaintenanceStatistics } from '@/api/asset'
import { getMaintenancePage } from '@/api/asset'

const statistics = reactive({
  totalValue: '0.00',
  yearlyMaintenanceCost: '0.00',
  totalCount: 0,
  avgValue: '0.00'
})

const categoryFinance = ref([])
const maintenanceList = ref([])

const loadStatistics = async () => {
  try {
    const res = await getStatistics()
    const data = res.data
    statistics.totalValue = data.totalValue || '0.00'
    statistics.yearlyMaintenanceCost = data.yearlyMaintenanceCost || '0.00'
    statistics.totalCount = data.totalCount || 0
    const totalVal = parseFloat(data.totalValue) || 0
    const totalCnt = parseInt(data.totalCount) || 1
    statistics.avgValue = (totalVal / totalCnt).toFixed(2)
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadCategoryFinance = async () => {
  try {
    const res = await getCategoryStatistics()
    categoryFinance.value = res.data || []
  } catch (error) {
    console.error('加载分类统计失败:', error)
  }
}

const loadMaintenanceList = async () => {
  try {
    const res = await getMaintenancePage({ current: 1, size: 50 })
    maintenanceList.value = res.data.records || []
  } catch (error) {
    console.error('加载维修记录失败:', error)
  }
}

const getMaintenanceStatusType = (status) => {
  const types = { 1: 'danger', 2: 'warning', 3: 'success', 4: 'info' }
  return types[status] || 'info'
}

const getMaintenanceStatusText = (status) => {
  const texts = { 1: '待维修', 2: '维修中', 3: '已修好', 4: '无法修复' }
  return texts[status] || '未知'
}

onMounted(() => {
  loadStatistics()
  loadCategoryFinance()
  loadMaintenanceList()
})
</script>

<style scoped>
.finance {
  padding: 10px;
}

.toolbar h3 {
  margin: 0 0 20px 0;
  color: #333;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  padding: 10px;
}

.stat-title {
  font-size: 14px;
  color: #999;
  margin-bottom: 10px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-value.warning {
  color: #e6a23c;
}

.stat-value.primary {
  color: #409eff;
}

.stat-value.success {
  color: #67c23a;
}

.finance-table {
  margin-bottom: 20px;
}

@media (max-width: 768px) {
  .stat-card {
    margin-bottom: 10px;
  }

  .stat-value {
    font-size: 18px;
  }
}
</style>
