<template>
  <div class="ai-assistant">
    <div class="ai-chat-window" v-if="isOpen">
      <div class="ai-header">
        <div class="ai-header-left">
          <el-icon><ChatDotRound /></el-icon>
          <span>AI 智能助手</span>
        </div>
        <el-icon @click="isOpen = false" class="ai-close"><Close /></el-icon>
      </div>
      <div class="ai-messages" ref="messagesRef">
        <div class="ai-message ai-welcome" v-if="messages.length === 0">
          <div class="ai-avatar">🤖</div>
          <div class="ai-bubble">
            <p>您好！我是校园资产管理系统AI助手。</p>
            <p>我可以帮您查询资产数据、统计分析、解答使用问题。</p>
            <div class="quick-questions">
              <el-tag
                v-for="q in quickQuestions"
                :key="q"
                @click="sendQuickQuestion(q)"
                class="quick-tag"
                effect="plain"
              >{{ q }}</el-tag>
            </div>
          </div>
        </div>
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['ai-message', msg.role === 'user' ? 'ai-user-msg' : 'ai-bot-msg']"
        >
          <div class="ai-avatar" v-if="msg.role === 'bot'">🤖</div>
          <div :class="['ai-bubble', msg.role === 'user' ? 'user-bubble' : 'bot-bubble']">
            <pre class="ai-text">{{ msg.content }}</pre>
          </div>
          <div class="ai-avatar user-avatar" v-if="msg.role === 'user'">👤</div>
        </div>
        <div class="ai-message ai-bot-msg" v-if="loading">
          <div class="ai-avatar">🤖</div>
          <div class="ai-bubble bot-bubble">
            <div class="ai-typing">
              <span></span><span></span><span></span>
            </div>
          </div>
        </div>
      </div>
      <div class="ai-input-area">
        <el-input
          v-model="inputText"
          placeholder="输入您的问题..."
          @keyup.enter="sendMessage"
          :disabled="loading"
          clearable
        >
          <template #append>
            <el-button @click="sendMessage" :loading="loading" type="primary">
              <el-icon><Promotion /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>
    </div>
    <div class="ai-fab" @click="isOpen = !isOpen" v-if="!isOpen">
      <el-icon :size="28"><ChatDotRound /></el-icon>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { aiChat } from '@/api/ai'

const isOpen = ref(false)
const inputText = ref('')
const loading = ref(false)
const messages = ref([])
const messagesRef = ref(null)

const quickQuestions = ['资产统计', '资产总数', '维修情况', '楼宇分布', '帮助']

const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

const sendMessage = async () => {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const res = await aiChat(text)
    messages.value.push({ role: 'bot', content: res.data.answer })
  } catch (error) {
    messages.value.push({ role: 'bot', content: '抱歉，查询出现问题，请稍后再试。' })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}

const sendQuickQuestion = (question) => {
  inputText.value = question
  sendMessage()
}
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  z-index: 9999;
}

.ai-fab {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.5);
  transition: all 0.3s;
}

.ai-fab:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 25px rgba(102, 126, 234, 0.6);
}

.ai-chat-window {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 420px;
  height: 560px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.ai-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.ai-header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
}

.ai-close {
  cursor: pointer;
  font-size: 20px;
}

.ai-close:hover {
  opacity: 0.8;
}

.ai-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f8f9fa;
}

.ai-message {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
  gap: 8px;
}

.ai-user-msg {
  justify-content: flex-end;
}

.ai-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.user-avatar {
  background: #409eff;
}

.ai-bubble {
  max-width: 300px;
  padding: 12px 16px;
  border-radius: 12px;
  word-break: break-word;
}

.bot-bubble {
  background: white;
  border: 1px solid #e8e8e8;
  border-top-left-radius: 4px;
}

.user-bubble {
  background: #409eff;
  color: white;
  border-top-right-radius: 4px;
}

.ai-text {
  margin: 0;
  white-space: pre-wrap;
  font-family: inherit;
  font-size: 14px;
  line-height: 1.6;
}

.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 12px;
}

.quick-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.quick-tag:hover {
  color: #409eff;
  border-color: #409eff;
}

.ai-typing {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.ai-typing span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #999;
  animation: typing 1.4s infinite;
}

.ai-typing span:nth-child(2) {
  animation-delay: 0.2s;
}

.ai-typing span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
    opacity: 0.4;
  }
  30% {
    transform: translateY(-8px);
    opacity: 1;
  }
}

.ai-input-area {
  padding: 12px 16px;
  border-top: 1px solid #e8e8e8;
  background: white;
}

.ai-input-area :deep(.el-input-group__append) {
  padding: 0;
}

.ai-input-area :deep(.el-input-group__append .el-button) {
  margin: 0;
  border-radius: 0 4px 4px 0;
}

@media (max-width: 768px) {
  .ai-chat-window {
    width: calc(100vw - 20px);
    height: calc(100vh - 100px);
    bottom: 10px;
    right: 10px;
  }

  .ai-fab {
    bottom: 20px;
    right: 20px;
    width: 48px;
    height: 48px;
  }
}
</style>
