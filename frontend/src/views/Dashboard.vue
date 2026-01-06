<template>
  <div class="dashboard">
    <header class="dashboard-header">
      <h1>仪表板</h1>
      <button @click="handleLogout" class="logout-btn">退出登录</button>
    </header>
    
    <div class="dashboard-content">
      <h2>欢迎回来，{{ username }}！</h2>
      <p>这是您的个人仪表板页面。</p>
    </div>
  </div>
</template>

<script>
import api from '../utils/api'

export default {
  name: 'Dashboard',
  data() {
    return {
      username: '用户'
    }
  },
  mounted() {
    // 从token中获取用户名信息（这里简化处理）
    const token = localStorage.getItem('token');
    if (!token) {
      this.$router.push('/login');
    }
  },
  methods: {
    handleLogout() {
      localStorage.removeItem('token');
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.dashboard {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 2rem;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e1e5e9;
}

.dashboard-header h1 {
  color: #333;
  margin: 0;
}

.logout-btn {
  padding: 0.5rem 1rem;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.logout-btn:hover {
  transform: translateY(-2px);
}

.dashboard-content {
  background: white;
  padding: 2rem;
  border-radius: 10px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.dashboard-content h2 {
  color: #333;
  margin-bottom: 1rem;
}

.dashboard-content p {
  color: #666;
  line-height: 1.6;
}
</style>