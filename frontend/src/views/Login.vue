<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-header">
        <h1>校园实训楼固定资产资产管理系统</h1>
        <p>Asset Management System</p>
      </div>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            prefix-icon="User"
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            prefix-icon="Lock"
            size="large"
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="handleLogin" :loading="loading" style="width: 100%">
            登 录
          </el-button>
        </el-form-item>
      </el-form>
      <div class="login-footer">
        <p>测试账号：</p>
        <p>超级管理员：admin / admin123</p>
        <p>校级资产负责人：school_admin / 123456</p>
        <p>部门资产管理员：dept_admin / 123456</p>
        <p>仓库管理员：warehouse / 123456</p>
        <p>维修运维员：maintenance / 123456</p>
        <p>财务审核员：finance / 123456</p>
        <p>普通使用人：teacher1 / 123456</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = ref({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate()
  if (!valid) return

  loading.value = true
  try {
    await userStore.loginAction(loginForm.value)
    ElMessage.success('登录成功')
    router.push('/')
  } catch (error) {
    console.error('登录失败:', error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-box {
  width: 100%;
  max-width: 450px;
  background: white;
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.login-header h1 {
  font-size: 24px;
  color: #333;
  margin-bottom: 10px;
}

.login-header p {
  font-size: 14px;
  color: #999;
}

.login-form {
  margin-bottom: 20px;
}

.login-footer {
  text-align: left;
  color: #999;
  font-size: 12px;
  line-height: 1.8;
}

.login-footer p {
  margin: 2px 0;
}

.login-footer p:first-child {
  font-weight: bold;
  color: #666;
}

@media (max-width: 768px) {
  .login-box {
    padding: 30px 20px;
  }

  .login-header h1 {
    font-size: 20px;
  }
}
</style>
