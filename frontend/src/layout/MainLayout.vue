<template>
  <el-container class="main-layout">
    <!-- 顶部导航栏 -->
    <el-header class="top-header">
      <div class="header-content">
        <div class="header-center">
          <h2>HzqServer Admin</h2>
        </div>
        <div class="header-right">
          <el-input
            placeholder="搜索..."
            prefix-icon="el-icon-search"
            size="small"
            style="width: 200px; margin-right: 15px;"
          ></el-input>
          <el-dropdown>
            <span class="el-dropdown-link">
              管理员<i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item @click="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </el-header>

    <!-- 主容器 -->
    <el-container>
      <!-- 侧边栏 -->
      <el-aside width="250px" class="sidebar">
        <!-- 移除logo部分，使菜单向上移动 -->
        <!-- <div class="logo">
          <i class="el-icon-platform-eleme logo-icon"></i>
          <h3>HzqServer Admin</h3>
        </div> -->
        <SidebarMenu />
      </el-aside>

      <!-- 主内容区域 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import SidebarMenu from '../components/SidebarMenu.vue'

// 引入Element Plus组件
import { ElContainer, ElAside, ElHeader, ElMain, ElMenu, ElMenuItem, ElDropdown, ElDropdownMenu, ElDropdownItem, ElInput, ElCard } from 'element-plus'

export default {
  name: 'MainLayout',
  components: {
    SidebarMenu,
    ElContainer,
    ElAside,
    ElHeader,
    ElMain,
    ElMenu,
    ElMenuItem,
    ElDropdown,
    ElDropdownMenu,
    ElDropdownItem,
    ElInput,
    ElCard
  },
  computed: {
    getCurrentPageTitle() {
      const route = this.$route;
      return route.meta.title || route.name || '首页';
    }
  },
  methods: {
    logout() {
      // 清除token并跳转到登录页
      localStorage.removeItem('token');
      this.$router.push('/login');
    }
  }
}
</script>

<style scoped>
.main-layout {
  height: 100vh;
  display: flex;
  flex-direction: column;
}

.top-header {
  background-color: white;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  border-bottom: 1px solid #e6e6e6;
  height: 60px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.header-content {
  display: flex;
  justify-content: space-between;
  width: 100%;
}

.header-left h2 {
  margin: 0;
  color: #333;
  font-weight: 500;
  font-size: 18px;
}

.header-right {
  display: flex;
  align-items: center;
}

.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  font-size: 14px;
}

.sidebar {
  background-color: #222b3a;
  color: #aeb9c5;
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
  border-right: 1px solid #2a3747;
  transition: all 0.3s ease;
  width: 250px !important;
  min-width: 250px !important;
  max-width: 250px !important;
  position: fixed;
  top: 60px;
  left: 0;
  bottom: 0;
  z-index: 999;
}

/* 移除logo区域后，调整侧边栏样式 */
.sidebar {
  background-color: #222b3a;
  color: #aeb9c5;
  box-shadow: 2px 0 6px rgba(0,21,41,.35);
  border-right: 1px solid #2a3747;
  transition: all 0.3s ease;
  width: 250px !important;
  min-width: 250px !important;
  max-width: 250px !important;
  position: fixed;
  top: 60px;
  left: 0;
  bottom: 0;
  z-index: 999;
}

/* 移除logo区域 */
.logo {
  display: none;
}

.logo-icon {
  font-size: 24px;
  margin-right: 8px;
}

.main-content {
  background-color: #f5f7fa;
  padding: 0;
  height: calc(100vh - 60px);
  color: #303133;
  margin-left: 250px;
  overflow-y: auto;
  /* 只有当内容超过高度时才显示滚动条 */
  overflow-x: hidden;
  /* 隐藏默认滚动条样式，使用自定义样式 */
  scrollbar-width: thin;
  scrollbar-color: #c0c4cc #f5f7fa;
}

/* 自定义滚动条样式 */
.main-content::-webkit-scrollbar {
  width: 8px;
}

.main-content::-webkit-scrollbar-track {
  background: #f5f7fa;
}

.main-content::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
}

.main-content::-webkit-scrollbar-thumb:hover {
  background: #909399;
}
</style>