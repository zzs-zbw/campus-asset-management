<template>
  <div class="maintenance">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="资产编号/上报人" clearable />
        </el-form-item>
        <el-form-item label="维修状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="待维修" :value="1" />
            <el-option label="维修中" :value="2" />
            <el-option label="已修好" :value="3" />
            <el-option label="无法修复" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增维修记录
        </el-button>
      </div>

      <el-table :data="tableData" stripe class="maintenance-table">
        <el-table-column prop="assetCode" label="资产编号" width="120" />
        <el-table-column prop="faultDescription" label="故障描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="reportTime" label="上报时间" width="180" />
        <el-table-column prop="reporter" label="上报人" width="100" />
        <el-table-column prop="maintenanceCost" label="维修费用" width="100">
          <template #default="{ row }">
            {{ row.maintenanceCost ? '¥' + row.maintenanceCost : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-form-item label="资产编号" prop="assetCode">
          <el-input v-model="formData.assetCode" placeholder="请输入资产编号" />
        </el-form-item>
        <el-form-item label="故障描述" prop="faultDescription">
          <el-input v-model="formData.faultDescription" type="textarea" :rows="3" placeholder="请描述故障情况" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上报时间" prop="reportTime">
              <el-date-picker v-model="formData.reportTime" type="datetime" placeholder="选择时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="上报人" prop="reporter">
              <el-input v-model="formData.reporter" placeholder="请输入上报人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="维修原因" prop="maintenanceReason">
          <el-input v-model="formData.maintenanceReason" type="textarea" :rows="2" placeholder="请输入维修原因" />
        </el-form-item>
        <el-form-item label="维修方式" prop="maintenanceMethod">
          <el-input v-model="formData.maintenanceMethod" placeholder="请输入维修方式" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="维修费用" prop="maintenanceCost">
              <el-input-number v-model="formData.maintenanceCost" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维修状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="待维修" :value="1" />
                <el-option label="维修中" :value="2" />
                <el-option label="已修好" :value="3" />
                <el-option label="无法修复" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="维修开始时间" prop="startTime">
              <el-date-picker v-model="formData.startTime" type="datetime" placeholder="选择时间" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维修完成时间" prop="endTime">
              <el-date-picker v-model="formData.endTime" type="datetime" placeholder="选择时间" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="维修处理人" prop="handler">
          <el-input v-model="formData.handler" placeholder="请输入维修处理人" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMaintenancePage, addMaintenance, updateMaintenance, deleteMaintenance } from '@/api/maintenance'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const searchForm = reactive({
  keyword: '',
  status: null
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const formData = reactive({
  id: null,
  assetId: null,
  assetCode: '',
  faultDescription: '',
  reportTime: null,
  reporter: '',
  reporterId: null,
  maintenanceReason: '',
  maintenanceMethod: '',
  maintenanceCost: null,
  startTime: null,
  endTime: null,
  status: 1,
  handler: '',
  remark: ''
})

const rules = {
  assetCode: [{ required: true, message: '请输入资产编号', trigger: 'blur' }],
  faultDescription: [{ required: true, message: '请描述故障情况', trigger: 'blur' }],
  reporter: [{ required: true, message: '请输入上报人', trigger: 'blur' }],
  status: [{ required: true, message: '请选择维修状态', trigger: 'change' }]
}

const loadData = async () => {
  try {
    const res = await getMaintenancePage({
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
    keyword: '',
    status: null
  })
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增维修记录'
  Object.assign(formData, {
    id: null,
    assetId: null,
    assetCode: '',
    faultDescription: '',
    reportTime: new Date(),
    reporter: userStore.userName,
    reporterId: userStore.user?.id,
    maintenanceReason: '',
    maintenanceMethod: '',
    maintenanceCost: null,
    startTime: null,
    endTime: null,
    status: 1,
    handler: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑维修记录'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    if (formData.id) {
      await updateMaintenance(formData)
      ElMessage.success('修改成功')
    } else {
      await addMaintenance(formData)
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该维修记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteMaintenance(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const getStatusType = (status) => {
  const types = { 1: 'danger', 2: 'warning', 3: 'success', 4: 'info' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 1: '待维修', 2: '维修中', 3: '已修好', 4: '无法修复' }
  return texts[status] || '未知'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.maintenance {
  padding: 10px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.maintenance-table {
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
}
</style>