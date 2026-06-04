<template>
  <div class="logs">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="操作类型">
          <el-select v-model="searchForm.operationType" placeholder="请选择" clearable>
            <el-option label="登录" value="登录" />
            <el-option label="新增" value="新增" />
            <el-option label="修改" value="修改" />
            <el-option label="删除" value="删除" />
            <el-option label="导入" value="导入" />
            <el-option label="导出" value="导出" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作模块">
          <el-select v-model="searchForm.moduleName" placeholder="请选择" clearable>
            <el-option label="资产" value="资产" />
            <el-option label="维修" value="维修" />
            <el-option label="用户" value="用户" />
            <el-option label="分类" value="分类" />
            <el-option label="系统" value="系统" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" stripe class="log-table">
        <el-table-column prop="username" label="操作用户" width="120" />
        <el-table-column prop="operationType" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeColor(row.operationType)">{{ row.operationType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="moduleName" label="操作模块" width="100" />
        <el-table-column prop="operationDesc" label="操作描述" min-width="250" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP地址" width="130" />
        <el-table-column prop="operationTime" label="操作时间" width="180" />
      </el-table>

      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadData"
        @current-change="loadData"
        class="pagination"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getLogPage } from '@/api/log'

const searchForm = reactive({
  operationType: '',
  moduleName: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])

const loadData = async () => {
  try {
    const res = await getLogPage({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error('加载数据失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    operationType: '',
    moduleName: ''
  })
  handleSearch()
}

const getOperationTypeColor = (type) => {
  const colors = {
    '登录': 'success',
    '新增': 'primary',
    '修改': 'warning',
    '删除': 'danger',
    '导入': 'info',
    '导出': 'info'
  }
  return colors[type] || 'info'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.logs {
  padding: 10px;
}

.search-form {
  margin-bottom: 20px;
}

.log-table {
  margin-bottom: 20px;
}

.pagination {
  display: flex;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .search-form .el-form-item {
    width: 100%;
  }
  
  .log-table {
    font-size: 12px;
  }
}
</style>