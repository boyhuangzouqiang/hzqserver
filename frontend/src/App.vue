<template>
  <div id="app">
    <MainLayout v-if="isAuthenticated" :key="$route.fullPath" />
    <router-view v-else />
  </div>
</template>

<script>
import MainLayout from './layout/MainLayout.vue'

export default {
  name: 'App',
  components: {
    MainLayout
  },
  data() {
    return {
      token: localStorage.getItem('token')
    }
  },
  computed: {
    isAuthenticated() {
      return !!this.token
    }
  },
  watch: {
    '$route'(to, from) {
      // 当路由变化时，重新检查认证状态
      this.token = localStorage.getItem('token')
    }
  },
  mounted() {
    // 监听storage事件，当token变化时更新状态
    window.addEventListener('storage', this.handleStorageChange)
  },
  beforeDestroy() {
    window.removeEventListener('storage', this.handleStorageChange)
  },
  methods: {
    handleStorageChange(event) {
      if (event.key === 'token') {
        this.token = event.newValue
      }
    }
  }
}
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  height: 100vh;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  flex-direction: column;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>