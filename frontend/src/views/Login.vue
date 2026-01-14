<template>
  <div class="login-container">
    <div class="login-form">
      <!-- 登录表单 -->
      <div v-if="!showRegisterForm" class="login-view">
        <div class="login-header">
          <h1>HzqServer 认证系统</h1>
          <p>安全 · 高效 · 可靠</p>
        </div>
        
        <form @submit.prevent="handleLogin" class="form">
          <div class="input-group">
            <label for="username">用户名</label>
            <input 
              type="text" 
              id="username" 
              v-model="username" 
              placeholder="请输入用户名"
              required
            >
          </div>
          
          <div class="input-group">
            <label for="password">密码</label>
            <input 
              type="password" 
              id="password" 
              v-model="password" 
              placeholder="请输入密码"
              required
            >
          </div>
          
          <div class="remember-forgot">
            <label class="checkbox-container">
              <input type="checkbox" v-model="rememberMe">
              <span class="checkmark"></span>
              记住我
            </label>
            <a href="#" class="forgot-link">忘记密码?</a>
          </div>
          
          <button type="submit" class="login-btn" :disabled="loading">
            <span v-if="!loading">登录</span>
            <span v-else>登录中...</span>
          </button>
          
          <div v-if="error" class="error-message">{{ error }}</div>
        </form>
        
        <div class="register-link">
          <p>还没有账号? <a href="#" @click.prevent="showRegisterForm = true">立即注册</a></p>
        </div>
      </div>
      
      <!-- 注册表单 -->
      <div v-else class="register-view">
        <div class="login-header">
          <h1>用户注册</h1>
          <p>创建新账户</p>
        </div>
        
        <form @submit.prevent="handleRegister" class="form">
          <div class="input-group">
            <label for="reg-username">用户名</label>
            <input 
              type="text" 
              id="reg-username" 
              v-model="regUsername" 
              placeholder="请输入用户名"
              required
            >
          </div>
          
          <div class="input-group">
            <label for="reg-password">密码</label>
            <input 
              type="password" 
              id="reg-password" 
              v-model="regPassword" 
              placeholder="请输入密码"
              required
            >
          </div>
          
          <div class="input-group">
            <label for="reg-email">邮箱</label>
            <input 
              type="email" 
              id="reg-email" 
              v-model="regEmail" 
              placeholder="请输入邮箱"
            >
          </div>
          
          <div class="input-group">
            <label for="reg-phone">手机号</label>
            <input 
              type="tel" 
              id="reg-phone" 
              v-model="regPhone" 
              placeholder="请输入手机号"
            >
          </div>
          
          <button type="submit" class="login-btn" :disabled="loading">
            <span v-if="!loading">注册</span>
            <span v-else>注册中...</span>
          </button>
          
          <div v-if="error" class="error-message">{{ error }}</div>
        </form>
        
        <div class="register-link">
          <p>已有账号? <a href="#" @click.prevent="showRegisterForm = false">立即登录</a></p>
        </div>
      </div>
    </div>
    
    <div class="login-background">
      <div class="particles" v-for="i in 50" :key="i" :style="getParticleStyle(i)"></div>
    </div>
  </div>
</template>

<script>
import api from '../utils/api'

export default {
  name: 'Login',
  data() {
    return {
      // 登录相关
      username: '',
      password: '',
      rememberMe: false,
      // 注册相关
      showRegisterForm: false,
      regUsername: '',
      regPassword: '',
      regEmail: '',
      regPhone: '',
      // 通用
      loading: false,
      error: ''
    }
  },
  methods: {
    async handleLogin() {
      this.loading = true;
      this.error = '';
      
      try {
        // 使用查询参数而不是请求体，与后端控制器匹配
        const response = await api.post(`/authservice/auth/login?username=${encodeURIComponent(this.username)}&password=${encodeURIComponent(this.password)}`);
        
        if (response.data.success) {
          // 存储token
          localStorage.setItem('token', response.data.token);
          
          // 跳转到仪表板
          this.$router.push('/dashboard');
        } else {
          this.error = response.data.message || '登录失败';
        }
      } catch (error) {
        console.error('Login error:', error);
        if (error.response) {
          // 服务器响应了错误状态
          if (error.response.status === 401) {
            this.error = error.response.data.message || '用户名或密码错误';
          } else {
            this.error = error.response.data.message || `登录失败: ${error.response.status}`;
          }
        } else if (error.request) {
          // 请求已发出但没有收到响应（网络错误等）
          this.error = '网络错误，请检查后端服务是否正常运行';
        } else {
          // 其他错误
          this.error = error.message || '登录失败，请稍后重试';
        }
      } finally {
        this.loading = false;
      }
    },
    async handleRegister() {
      this.loading = true;
      this.error = '';
      
      try {
        const userData = {
          username: this.regUsername,
          password: this.regPassword
        };
        
        // 如果提供了邮箱或手机号，也包含在请求中
        if (this.regEmail) userData.email = this.regEmail;
        if (this.regPhone) userData.phone = this.regPhone;
        
        const response = await api.post('/authservice/auth/register', userData);
        
        if (response.data.success) {
          // 注册成功，自动跳转到登录页面
          this.error = '注册成功，请登录';
          this.showRegisterForm = false;
          // 清空表单
          this.regUsername = '';
          this.regPassword = '';
          this.regEmail = '';
          this.regPhone = '';
        } else {
          this.error = response.data.message || '注册失败';
        }
      } catch (error) {
        console.error('Register error:', error);
        if (error.response) {
          // 服务器响应了错误状态
          this.error = error.response.data.message || `注册失败: ${error.response.status}`;
        } else if (error.request) {
          // 请求已发出但没有收到响应（网络错误等）
          this.error = '网络错误，请检查后端服务是否正常运行';
        } else {
          // 其他错误
          this.error = error.message || '注册失败，请稍后重试';
        }
      } finally {
        this.loading = false;
      }
    },
    getParticleStyle(index) {
      // 生成随机位置和动画效果
      const size = Math.random() * 10 + 2;
      const left = Math.random() * 100;
      const top = Math.random() * 100;
      const animationDuration = Math.random() * 20 + 10;
      const animationDelay = Math.random() * 5;
      
      return {
        width: `${size}px`,
        height: `${size}px`,
        left: `${left}%`,
        top: `${top}%`,
        animationDuration: `${animationDuration}s`,
        animationDelay: `${animationDelay}s`,
        opacity: Math.random() * 0.5 + 0.1
      };
    }
  }
}
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.particles {
  position: absolute;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  animation: float 10s infinite linear;
}

@keyframes float {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 0.3;
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
    opacity: 0.6;
  }
  100% {
    transform: translateY(0) rotate(360deg);
    opacity: 0.3;
  }
}

.login-form {
  background: rgba(255, 255, 255, 0.95);
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 15px 35px rgba(0, 0, 0, 0.2);
  width: 100%;
  max-width: 400px;
  z-index: 2;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transform: scale(1);
  transition: transform 0.3s ease;
}

.login-form:hover {
  transform: scale(1.02);
}

.login-header {
  text-align: center;
  margin-bottom: 2rem;
}

.login-header h1 {
  color: #333;
  margin-bottom: 0.5rem;
  font-size: 1.8rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
}

.login-header p {
  color: #666;
  font-size: 0.9rem;
}

.form {
  display: flex;
  flex-direction: column;
}

.input-group {
  margin-bottom: 1.5rem;
}

.input-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: #555;
  font-weight: 500;
}

.input-group input {
  width: 100%;
  padding: 0.8rem 1rem;
  border: 2px solid #e1e5e9;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  box-sizing: border-box;
}

.input-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.remember-forgot {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  font-size: 0.9rem;
}

.checkbox-container {
  display: flex;
  align-items: center;
  cursor: pointer;
  user-select: none;
}

.checkbox-container input {
  display: none;
}

.checkmark {
  width: 18px;
  height: 18px;
  background-color: #eee;
  border: 2px solid #ddd;
  border-radius: 4px;
  margin-right: 8px;
  transition: all 0.3s ease;
}

.checkbox-container input:checked ~ .checkmark {
  background-color: #667eea;
  border-color: #667eea;
}

.checkmark:after {
  content: "";
  position: absolute;
  display: none;
}

.checkbox-container input:checked ~ .checkmark:after {
  display: block;
}

.checkbox-container .checkmark:after {
  left: 6px;
  top: 2px;
  width: 6px;
  height: 12px;
  border: solid white;
  border-width: 0 3px 3px 0;
  transform: rotate(45deg);
}

.forgot-link {
  color: #667eea;
  text-decoration: none;
  transition: color 0.3s ease;
}

.forgot-link:hover {
  color: #5a6fd8;
  text-decoration: underline;
}

.login-btn {
  width: 100%;
  padding: 0.8rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  margin-bottom: 1rem;
}

.login-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
}

.login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.error-message {
  color: #e74c3c;
  text-align: center;
  margin-top: 1rem;
  padding: 0.5rem;
  background: #ffeaea;
  border-radius: 4px;
  font-size: 0.9rem;
}

.register-link {
  text-align: center;
  margin-top: 1rem;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.register-link a:hover {
  color: #5a6fd8;
  text-decoration: underline;
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-form {
    margin: 1rem;
    padding: 1.5rem;
  }
  
  .login-header h1 {
    font-size: 1.5rem;
  }
  
  .remember-forgot {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
}
</style>