<template>
  <div class="dashboard-container">
    <div class="welcome-section">
      <h2 class="welcome-title">欢迎回来!</h2>
      <p class="welcome-subtitle">今天是 {{ currentDate }}</p>
    </div>
    
    <div class="stats-grid">
      <!-- 已用空间卡片 -->
      <el-card class="stat-card orange-card">
        <div class="stat-content">
          <div class="stat-icon">
            <i class="el-icon-folder-opened"></i>
          </div>
          <div class="stat-info">
            <div class="stat-number">49/50 GB</div>
            <div class="stat-label">已用空间</div>
          </div>
        </div>
      </el-card>
      
      <!-- 收入卡片 -->
      <el-card class="stat-card green-card">
        <div class="stat-content">
          <div class="stat-icon">
            <i class="el-icon-sell"></i>
          </div>
          <div class="stat-info">
            <div class="stat-number">$34,245</div>
            <div class="stat-label">收入</div>
          </div>
        </div>
      </el-card>
      
      <!-- 已修复问题卡片 -->
      <el-card class="stat-card red-card">
        <div class="stat-content">
          <div class="stat-icon">
            <i class="el-icon-warning"></i>
          </div>
          <div class="stat-info">
            <div class="stat-number">75</div>
            <div class="stat-label">已修复问题</div>
          </div>
        </div>
      </el-card>
      
      <!-- 关注者卡片 -->
      <el-card class="stat-card blue-card">
        <div class="stat-content">
          <div class="stat-icon">
            <i class="el-icon-user-solid"></i>
          </div>
          <div class="stat-info">
            <div class="stat-number">+245</div>
            <div class="stat-label">关注者</div>
          </div>
        </div>
      </el-card>
    </div>
    
    <div class="charts-grid">
      <!-- 日销售额图表 -->
      <el-card class="chart-card green-card">
        <div class="chart-header">
          <h3>日销售额</h3>
          <div class="chart-trend">
            <i class="el-icon-caret-top"></i> 55% 增长
          </div>
        </div>
        <div class="chart-content">
          <div class="chart-placeholder">
            <span>折线图占位符</span>
          </div>
        </div>
        <div class="chart-footer">
          <small>更新于 4 分钟前</small>
        </div>
      </el-card>
      
      <!-- 邮件订阅图表 -->
      <el-card class="chart-card orange-card">
        <div class="chart-header">
          <h3>邮件订阅</h3>
          <div class="chart-trend">
            上次活动表现
          </div>
        </div>
        <div class="chart-content">
          <div class="chart-placeholder">
            <span>柱状图占位符</span>
          </div>
        </div>
        <div class="chart-footer">
          <small>活动发送于 2 天前</small>
        </div>
      </el-card>
      
      <!-- 已完成任务图表 -->
      <el-card class="chart-card red-card">
        <div class="chart-header">
          <h3>已完成任务</h3>
          <div class="chart-trend">
            上次活动表现
          </div>
        </div>
        <div class="chart-content">
          <div class="chart-placeholder">
            <span>面积图占位符</span>
          </div>
        </div>
        <div class="chart-footer">
          <small>活动发送于 2 天前</small>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ElCard } from 'element-plus'

export default {
  name: 'Dashboard',
  components: {
    ElCard
  },
  computed: {
    currentDate() {
      const now = new Date();
      const year = now.getFullYear();
      const month = String(now.getMonth() + 1).padStart(2, '0');
      const day = String(now.getDate()).padStart(2, '0');
      return `${year}年${month}月${day}日`;
    }
  },
  methods: {
    goTo(path) {
      this.$router.push(path);
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}

.welcome-section {
  margin-bottom: 20px;
}

.welcome-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.welcome-subtitle {
  font-size: 1rem;
  color: #909399;
  margin-top: 5px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border: none;
  height: 100px;
  overflow: hidden;
}

.stat-card.orange-card {
  background-color: #ff9800;
  color: white;
}

.stat-card.green-card {
  background-color: #4caf50;
  color: white;
}

.stat-card.red-card {
  background-color: #f44336;
  color: white;
}

.stat-card.blue-card {
  background-color: #2196f3;
  color: white;
}

.stat-content {
  display: flex;
  align-items: center;
  height: 100%;
  padding: 15px;
}

.stat-icon {
  font-size: 2.5rem;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-number {
  font-size: 1.8rem;
  font-weight: bold;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 0.9rem;
  opacity: 0.9;
}

.charts-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.chart-card {
  border-radius: 10px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border: none;
}

.chart-card.green-card {
  background-color: #4caf50;
  color: white;
}

.chart-card.orange-card {
  background-color: #ff9800;
  color: white;
}

.chart-card.red-card {
  background-color: #f44336;
  color: white;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
}

.chart-header h3 {
  margin: 0;
  font-size: 1.2rem;
}

.chart-trend {
  font-size: 0.9rem;
  opacity: 0.9;
}

.chart-trend i {
  margin-right: 5px;
}

.chart-content {
  padding: 10px 0 20px 0;
}

.chart-placeholder {
  height: 150px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: rgba(255, 255, 255, 0.15);
  border-radius: 5px;
}

.chart-footer {
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding-top: 10px;
}

.chart-footer small {
  opacity: 0.8;
}
</style>