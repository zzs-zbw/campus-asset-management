<template>
  <div class="assets">
    <el-card>
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="资产名称/编号/序列号/负责人" clearable />
        </el-form-item>
        <el-form-item label="设备分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择" clearable>
            <el-option v-for="cat in categories" :key="cat.id" :label="cat.categoryName" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="维修中" :value="2" />
            <el-option label="闲置" :value="3" />
            <el-option label="已报废" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属楼宇">
          <el-input v-model="searchForm.building" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <div class="toolbar">
        <el-button type="primary" @click="handleAdd" v-if="userStore.canAddAsset">
          <el-icon><Plus /></el-icon>
          新增资产
        </el-button>
        <el-button type="success" @click="handleExport" v-if="userStore.canExportAsset">
          <el-icon><Download /></el-icon>
          导出数据
        </el-button>
        <el-button type="info" @click="handleDownloadTemplate" v-if="userStore.canImportAsset">
          <el-icon><FileExcel /></el-icon>
          下载模板
        </el-button>
        <el-dropdown @command="handleImportWithMode" v-if="userStore.canImportAsset">
          <el-button type="warning">
            <el-icon><Upload /></el-icon>
            导入数据
            <el-icon class="el-icon--right"><ArrowDown /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="append">追加模式</el-dropdown-item>
              <el-dropdown-item command="merge">合并模式</el-dropdown-item>
              <el-dropdown-item command="update">更新模式</el-dropdown-item>
              <el-dropdown-item command="overwrite" divided>覆盖模式（谨慎）</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
        <el-button type="danger" @click="handleBatchDelete" :disabled="selectedRows.length === 0" v-if="userStore.canDeleteAsset">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>

      <el-table
        :data="tableData"
        stripe
        @selection-change="handleSelectionChange"
        class="asset-table"
      >
        <el-table-column type="selection" width="55" v-if="userStore.canDeleteAsset" />
        <el-table-column prop="assetCode" label="资产编号" width="120" />
        <el-table-column prop="assetName" label="资产名称" min-width="150" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="brand" label="品牌" width="100" />
        <el-table-column prop="model" label="型号" width="120" />
        <el-table-column prop="serialNumber" label="序列号" width="120" />
        <el-table-column prop="purchasePrice" label="单价" width="100">
          <template #default="{ row }">
            {{ row.purchasePrice ? '¥' + row.purchasePrice : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="存放位置" min-width="180" />
        <el-table-column prop="statusText" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ row.statusText }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="responsiblePerson" label="责任人" width="100" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)" v-if="userStore.canEditAsset">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)" v-if="userStore.canDeleteAsset">删除</el-button>
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

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px">
      <el-form :model="formData" :rules="rules" ref="formRef" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="资产编号" prop="assetCode">
              <el-input v-model="formData.assetCode" placeholder="自动生成" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="资产名称" prop="assetName">
              <el-input v-model="formData.assetName" placeholder="请输入资产名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="设备分类" prop="categoryId">
              <el-select v-model="formData.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="cat in categories" :key="cat.id" :label="cat.categoryName" :value="cat.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="formData.brand" placeholder="请输入品牌" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input v-model="formData.model" placeholder="请输入型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="序列号" prop="serialNumber">
              <el-input v-model="formData.serialNumber" placeholder="请输入序列号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="采购单价" prop="purchasePrice">
              <el-input-number v-model="formData.purchasePrice" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="总价值" prop="totalValue">
              <el-input-number v-model="formData.totalValue" :min="0" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="购置日期" prop="purchaseDate">
              <el-date-picker v-model="formData.purchaseDate" type="date" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="保修截止日期" prop="warrantyEndDate">
              <el-date-picker v-model="formData.warrantyEndDate" type="date" placeholder="选择日期" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="所属楼宇" prop="building">
              <el-input v-model="formData.building" placeholder="所属楼宇" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="楼层" prop="floor">
              <el-input v-model="formData.floor" placeholder="楼层" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="机房编号" prop="roomNumber">
              <el-input v-model="formData.roomNumber" placeholder="机房编号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="存放位置" prop="location">
          <el-input v-model="formData.location" placeholder="完整存放位置" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="使用状态" prop="status">
              <el-select v-model="formData.status" placeholder="请选择状态" style="width: 100%">
                <el-option label="正常" :value="1" />
                <el-option label="维修中" :value="2" />
                <el-option label="闲置" :value="3" />
                <el-option label="已报废" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="责任人" prop="responsiblePerson">
              <el-input v-model="formData.responsiblePerson" placeholder="请输入责任人" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注说明" prop="remark">
          <el-input v-model="formData.remark" type="textarea" :rows="3" placeholder="请输入备注说明" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="资产详情" width="900px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="资产编号">{{ detailData.assetCode }}</el-descriptions-item>
        <el-descriptions-item label="资产名称">{{ detailData.assetName }}</el-descriptions-item>
        <el-descriptions-item label="设备分类">{{ detailData.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="品牌">{{ detailData.brand }}</el-descriptions-item>
        <el-descriptions-item label="型号">{{ detailData.model }}</el-descriptions-item>
        <el-descriptions-item label="序列号">{{ detailData.serialNumber }}</el-descriptions-item>
        <el-descriptions-item label="采购单价">¥{{ detailData.purchasePrice }}</el-descriptions-item>
        <el-descriptions-item label="总价值">¥{{ detailData.totalValue }}</el-descriptions-item>
        <el-descriptions-item label="购置日期">{{ detailData.purchaseDate }}</el-descriptions-item>
        <el-descriptions-item label="保修截止日期">{{ detailData.warrantyEndDate }}</el-descriptions-item>
        <el-descriptions-item label="存放位置">{{ detailData.location }}</el-descriptions-item>
        <el-descriptions-item label="使用状态">
          <el-tag :type="getStatusType(detailData.status)">{{ detailData.statusText }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="责任人">{{ detailData.responsiblePerson }}</el-descriptions-item>
        <el-descriptions-item label="备注说明" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>维修记录</el-divider>
      <el-table :data="maintenanceRecords" stripe>
        <el-table-column prop="faultDescription" label="故障描述" />
        <el-table-column prop="reportTime" label="上报时间" width="180" />
        <el-table-column prop="reporter" label="上报人" width="100" />
        <el-table-column prop="maintenanceCost" label="维修费用" width="100">
          <template #default="{ row }">
            {{ row.maintenanceCost ? '¥' + row.maintenanceCost : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getMaintenanceStatusType(row.status)">{{ getMaintenanceStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getAssetPage, addAsset, updateAsset, deleteAsset, batchDeleteAssets, generateAssetCode } from '@/api/asset'
import { importAssets, exportAssets, getTemplate } from '@/api/asset'
import { getActiveCategories } from '@/api/category'
import { getMaintenanceByAssetId } from '@/api/maintenance'

const userStore = useUserStore()

const searchForm = reactive({
  keyword: '',
  categoryId: null,
  status: null,
  building: ''
})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const tableData = ref([])
const categories = ref([])
const selectedRows = ref([])

const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const formData = reactive({
  id: null,
  assetCode: '',
  assetName: '',
  categoryId: null,
  brand: '',
  model: '',
  serialNumber: '',
  purchasePrice: null,
  totalValue: null,
  purchaseDate: null,
  warrantyEndDate: null,
  building: '',
  floor: '',
  roomNumber: '',
  location: '',
  status: 1,
  responsiblePerson: '',
  remark: ''
})

const rules = {
  assetName: [{ required: true, message: '请输入资产名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择设备分类', trigger: 'change' }],
  serialNumber: [{ required: true, message: '请输入序列号', trigger: 'blur' }],
  status: [{ required: true, message: '请选择使用状态', trigger: 'change' }]
}

const detailVisible = ref(false)
const detailData = ref({})
const maintenanceRecords = ref([])

const loadData = async () => {
  try {
    const res = await getAssetPage({
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

const loadCategories = async () => {
  try {
    const res = await getActiveCategories()
    categories.value = res.data
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  Object.assign(searchForm, {
    keyword: '',
    categoryId: null,
    status: null,
    building: ''
  })
  handleSearch()
}

const handleAdd = async () => {
  dialogTitle.value = '新增资产'
  Object.assign(formData, {
    id: null,
    assetCode: '',
    assetName: '',
    categoryId: null,
    brand: '',
    model: '',
    serialNumber: '',
    purchasePrice: null,
    totalValue: null,
    purchaseDate: null,
    warrantyEndDate: null,
    building: '',
    floor: '',
    roomNumber: '',
    location: '',
    status: 1,
    responsiblePerson: '',
    remark: ''
  })
  try {
    const res = await generateAssetCode()
    formData.assetCode = res.data
  } catch (error) {
    console.error('生成编号失败:', error)
  }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑资产'
  Object.assign(formData, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate()
  if (!valid) return

  try {
    if (formData.id) {
      await updateAsset(formData)
      ElMessage.success('修改成功')
    } else {
      await addAsset(formData)
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
    await ElMessageBox.confirm('确定要删除该资产吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteAsset(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error('删除失败:', error)
  }
}

const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条数据吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const ids = selectedRows.value.map(row => row.id)
    await batchDeleteAssets(ids)
    ElMessage.success('批量删除成功')
    loadData()
  } catch (error) {
    console.error('批量删除失败:', error)
  }
}

const handleSelectionChange = (selection) => {
  selectedRows.value = selection
}

const handleView = async (row) => {
  detailData.value = row
  try {
    const res = await getMaintenanceByAssetId(row.id)
    maintenanceRecords.value = res.data
  } catch (error) {
    console.error('加载维修记录失败:', error)
  }
  detailVisible.value = true
}

const handleExport = async () => {
  try {
    const res = await exportAssets()
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '资产列表.xlsx'
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  }
}

const handleDownloadTemplate = async () => {
  try {
    const res = await getTemplate()
    const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '资产导入模板.xlsx'
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    window.URL.revokeObjectURL(url)
    ElMessage.success('模板下载成功')
  } catch (error) {
    console.error('下载模板失败:', error)
    ElMessage.error('下载模板失败')
  }
}

const handleImportWithMode = (mode) => {
  if (mode === 'overwrite') {
    ElMessageBox.confirm(
      '警告：覆盖模式将删除所有现有数据后导入新数据！\n\n确定要继续吗？',
      '确认覆盖',
      {
        confirmButtonText: '确定覆盖',
        cancelButtonText: '取消',
        type: 'warning',
        confirmButtonClass: 'el-button--danger'
      }
    ).then(() => {
      selectFileAndImport(mode)
    }).catch(() => {})
  } else {
    selectFileAndImport(mode)
  }
}

const selectFileAndImport = (mode) => {
  const input = document.createElement('input')
  input.type = 'file'
  input.accept = '.xlsx,.xls'
  input.onchange = async (e) => {
    const file = e.target.files[0]
    if (!file) return
    
    try {
      ElMessage.info('正在导入数据，请稍候...')
      const res = await importAssets(file, mode)
      ElMessage.success(res.data || res.message || '导入成功')
      loadData()
    } catch (error) {
      console.error('导入失败:', error)
      ElMessage.error('导入失败，请检查文件格式')
    }
  }
  input.click()
}

const getStatusType = (status) => {
  const types = { 1: 'success', 2: 'warning', 3: 'info', 4: 'danger' }
  return types[status] || 'info'
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
  loadData()
  loadCategories()
})
</script>

<style scoped>
.assets {
  padding: 10px;
}

.search-form {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}

.asset-table {
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
  
  .toolbar .el-button {
    margin: 5px;
  }
}
</style>