<template>
  <div class="dashboard">
    <el-row :gutter="20" class="statistics-cards">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in statistics" :key="stat.label">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon" :style="{ backgroundColor: stat.color }">
              <el-icon :size="30"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>设备状态分布</span>
            </div>
          </template>
          <div ref="pieChartRef" class="chart"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>各类设备数量统计</span>
            </div>
          </template>
          <div ref="barChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>资产价值统计</span>
            </div>
          </template>
          <div ref="valueChartRef" class="chart"></div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="info-row">
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>近期维修待处理</span>
            </div>
          </template>
          <el-table :data="pendingMaintenance" stripe>
            <el-table-column prop="assetCode" label="资产编号" />
            <el-table-column prop="faultDescription" label="故障描述" show-overflow-tooltip />
            <el-table-column prop="reportTime" label="上报时间" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>新增资产记录</span>
            </div>
          </template>
          <el-table :data="recentAssets" stripe>
            <el-table-column prop="assetCode" label="资产编号" />
            <el-table-column prop="assetName" label="资产名称" />
            <el-table-column prop="createTime" label="创建时间" />
            <el-table-column prop="status" label="状态">
              <template #default="{ row }">
                <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { getStatistics, getCategoryStatistics, getMaintenanceStatistics, getAssetPage, getMaintenancePage } from '@/api/asset'

const pieChartRef = ref(null)
const barChartRef = ref(null)
const valueChartRef = ref(null)

let pieChart = null
let barChart = null
let valueChart = null

const statistics = ref([
  { label: '资产总数', value: 0, icon: 'Box', color: '#409eff' },
  { label: '正常设备', value: 0, icon: 'CircleCheck', color: '#67c23a' },
  { label: '维修中', value: 0, icon: 'Tools', color: '#e6a23c' },
  { label: '闲置设备', value: 0, icon: 'Clock', color: '#909399' },
  { label: '已报废', value: 0, icon: 'Delete', color: '#f56c6c' },
  { label: '资产总价值', value: '0元', icon: 'Money', color: '#ff6b6b' },
  { label: '年度维修费用', value: '0元', icon: 'Wallet', color: '#4ecdc4' }
])

const pendingMaintenance = ref([])
const recentAssets = ref([])

const loadStatistics = async () => {
  try {
    const res = await getStatistics()
    const data = res.data
    statistics.value = [
      { label: '资产总数', value: data.total_count || 0, icon: 'Box', color: '#409eff' },
      { label: '正常设备', value: data.normal_count || 0, icon: 'CircleCheck', color: '#67c23a' },
      { label: '维修中', value: data.maintenance_count || 0, icon: 'Tools', color: '#e6a23c' },
      { label: '闲置设备', value: data.idle_count || 0, icon: 'Clock', color: '#909399' },
      { label: '已报废', value: data.scrapped_count || 0, icon: 'Delete', color: '#f56c6c' },
      { label: '资产总价值', value: (data.total_value || 0) + '元', icon: 'Money', color: '#ff6b6b' },
      { label: '年度维修费用', value: (data.yearly_maintenance_cost || 0) + '元', icon: 'Wallet', color: '#4ecdc4' }
    ]
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadCharts = async () => {
  try {
    const categoryRes = await getCategoryStatistics()
    const categoryData = categoryRes.data

    if (pieChart) {
      pieChart.setOption({
        tooltip: { trigger: 'item' },
        legend: { orient: 'vertical', left: 'left' },
        series: [{
          type: 'pie',
          radius: '50%',
          data: [
            { value: statistics.value[1].value, name: '正常' },
            { value: statistics.value[2].value, name: '维修中' },
            { value: statistics.value[3].value, name: '闲置' },
            { value: statistics.value[4].value, name: '已报废' }
          ],
          emphasis: {
            itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' }
          }
        }]
      })
    }

    if (barChart) {
      barChart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: categoryData.map(item => item.category_name)
        },
        yAxis: { type: 'value' },
        series: [{
          data: categoryData.map(item => item.asset_count),
          type: 'bar',
          itemStyle: { color: '#409eff' }
        }]
      })
    }

    if (valueChart) {
      valueChart.setOption({
        tooltip: { trigger: 'axis' },
        xAxis: {
          type: 'category',
          data: categoryData.map(item => item.category_name)
        },
        yAxis: { type: 'value' },
        series: [{
          data: categoryData.map(item => item.total_value),
          type: 'bar',
          itemStyle: { color: '#67c23a' }
        }]
      })
    }
  } catch (error) {
    console.error('加载图表数据失败:', error)
  }
}

const loadRecentData = async () => {
  try {
    const maintenanceRes = await getMaintenancePage({ current: 1, size: 5, status: 1 })
    pendingMaintenance.value = maintenanceRes.data.records

    const assetRes = await getAssetPage({ current: 1, size: 5 })
    recentAssets.value = assetRes.data.records
  } catch (error) {
    console.error('加载近期数据失败:', error)
  }
}

const getStatusType = (status) => {
  const types = { 1: 'success', 2: 'warning', 3: 'info', 4: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 1: '正常', 2: '维修中', 3: '闲置', 4: '已报废' }
  return texts[status] || '未知'
}

const initCharts = () => {
  pieChart = echarts.init(pieChartRef.value)
  barChart = echarts.init(barChartRef.value)
  valueChart = echarts.init(valueChartRef.value)

  window.addEventListener('resize', () => {
    pieChart && pieChart.resize()
    barChart && barChart.resize()
    valueChart && valueChart.resize()
  })
}

onMounted(async () => {
  await loadStatistics()
  initCharts()
  await loadCharts()
  await loadRecentData()
})

onUnmounted(() => {
  window.removeEventListener('resize', () => {
    pieChart && pieChart.resize()
    barChart && barChart.resize()
    valueChart && valueChart.resize()
  })
  pieChart && pieChart.dispose()
  barChart && barChart.dispose()
  valueChart && valueChart.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 10px;
}

.statistics-cards {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #999;
}

.charts-row {
  margin-bottom: 20px;
}

.chart {
  height: 350px;
  width: 100%;
}

.info-row {
  margin-bottom: 20px;
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

@media (max-width: 768px) {
  .stat-content {
    gap: 10px;
  }

  .stat-icon {
    width: 50px;
    height: 50px;
  }

  .stat-value {
    font-size: 22px;
  }

  .chart {
    height: 250px;
  }
}
</style>