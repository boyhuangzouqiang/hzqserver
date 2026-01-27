<template>
  <el-menu
    :default-active="$route.path"
    class="sidebar-menu"
    :unique-opened="true"
    :collapse="false"
    router
    background-color="#222b3a"
    text-color="#aeb9c5"
    active-text-color="#409EFF"
    :default-openeds="[]"
    :collapse-transition="false"
  >
    <!-- 首页菜单项 -->
    <el-menu-item index="/dashboard">
      <i class="el-icon-house" style="color: #409EFF;"></i>
      <span>首页</span>
    </el-menu-item>

    <!-- 系统管理下拉菜单 -->
    <el-sub-menu ref="systemManagementSubmenu" index="system-management" popper-class="no-arrow-submenu" :teleported="false">
      <template #title>
        <i class="el-icon-setting"></i>
        <span>系统管理</span>
      </template>
      
      <el-menu-item index="/user-management">
        <i class="el-icon-user" style="color: #409EFF;"></i>
        <span>用户管理</span>
      </el-menu-item>
      
      <el-menu-item index="/role-management">
        <i class="el-icon-s-custom" style="color: #409EFF;"></i>
        <span>角色管理</span>
      </el-menu-item>
      
      <el-menu-item index="/menu-management">
        <i class="el-icon-menu" style="color: #409EFF;"></i>
        <span>菜单管理</span>
      </el-menu-item>
      
      <el-menu-item index="/permission-management">
        <i class="el-icon-lock" style="color: #409EFF;"></i>
        <span>权限管理</span>
      </el-menu-item>
    </el-sub-menu>
  </el-menu>
</template>

<script>
import { ElMenu, ElMenuItem, ElSubMenu } from 'element-plus'
import { nextTick, onMounted } from 'vue'

export default {
  name: 'SidebarMenu',
  components: {
    ElMenu,
    ElMenuItem,
    ElSubMenu
  },
  async mounted() {
    // 等待DOM更新完成
    await nextTick();
    
    // 查找并移除所有子菜单的箭头
    const arrows = document.querySelectorAll('.el-sub-menu__icon-arrow');
    arrows.forEach(arrow => {
      arrow.style.display = 'none';
    });
    
    // 监听DOM变化，持续移除新生成的箭头
    const observer = new MutationObserver((mutationsList) => {
      for (let mutation of mutationsList) {
        if (mutation.type === 'childList') {
          mutation.addedNodes.forEach(node => {
            if (node.nodeType === 1) { // ELEMENT_NODE
              const submenuArrows = node.querySelectorAll ? node.querySelectorAll('.el-sub-menu__icon-arrow') : [];
              submenuArrows.forEach(arrow => {
                arrow.style.display = 'none';
              });
              
              // 如果节点本身就是箭头
              if (node.classList && node.classList.contains('el-sub-menu__icon-arrow')) {
                node.style.display = 'none';
              }
            }
          });
        }
      }
    });

    // 开始观察
    observer.observe(document.body, {
      childList: true,
      subtree: true
    });
  }
}
</script>

<style scoped>
.sidebar-menu {
  border: none;
  background-color: #222b3a;
  height: 100%;
  padding: 0;
  overflow-y: auto;
}

.sidebar-menu .el-menu-item {
  color: #aeb9c5;
  height: 50px;
  line-height: 50px;
  background-color: #222b3a;
  border-left: 3px solid transparent;
  transition: all 0.3s ease;
  padding: 0 15px 0 20px;
  display: flex;
  align-items: center;
  width: 100%;
  margin: 0;
  border-radius: 0;
  font-size: 14px;
  font-weight: 500;
  position: relative;
}

.sidebar-menu .el-menu-item:hover {
  background-color: #2a3445 !important;
  color: #fff;
  border-left: 3px solid #409EFF;
  margin: 0;
  border-radius: 0;
  font-size: 14px;
  font-weight: 500;
  position: relative;
}

.sidebar-menu .el-menu-item.is-active {
  background-color: #2a3445 !important;
  color: #409EFF;
  border-left: 3px solid #409EFF;
  margin: 0;
  border-radius: 0;
  font-size: 14px;
  font-weight: 500;
  position: relative;
}

.sidebar-menu .el-sub-menu__title {
  color: #aeb9c5;
  height: 50px;
  line-height: 50px;
  background-color: #222b3a;
  border-left: 3px solid transparent;
  transition: all 0.3s ease;
  padding: 0 15px 0 20px;
  display: flex;
  align-items: center;
  width: 100%;
  margin: 0;
  border-radius: 0;
  font-size: 14px;
  font-weight: 500;
  position: relative;
}

.sidebar-menu .el-sub-menu__title:hover {
  background-color: #2a3445 !important;
  color: #409EFF;
  border-left: 3px solid #409EFF;
  margin: 0;
  border-radius: 0;
  font-size: 14px;
  font-weight: 500;
  position: relative;
}

/* 修复子菜单缩进样式 */
.sidebar-menu .el-sub-menu .el-menu-item {
  padding-left: 45px !important;
  height: 45px;
  line-height: 45px;
  margin: 0;
  border-radius: 0;
  font-size: 13px;
  font-weight: 400;
  position: relative;
}



/* 修复子菜单背景色 */
.sidebar-menu .el-menu {
  background-color: #2a3445;
}

/* 子菜单项悬停和激活状态 */
.sidebar-menu .el-sub-menu .el-menu-item:hover {
  background-color: #313a4b !important;
  color: #fff;
}

.sidebar-menu .el-sub-menu .el-menu-item.is-active {
  background-color: #313a4b !important;
  color: #409EFF;
  border-left: 3px solid #409EFF;
}



/* 完全隐藏所有子菜单的箭头 */
.sidebar-menu .el-sub-menu__title .el-sub-menu__icon-arrow {
  display: none !important;
  visibility: hidden !important;
  opacity: 0 !important;
  width: 0 !important;
  height: 0 !important;
  margin: 0 !important;
  padding: 0 !important;
}

/* 完全隐藏SVG箭头 */
.sidebar-menu .el-sub-menu__title svg {
  display: none !important;
  visibility: hidden !important;
  opacity: 0 !important;
  width: 0 !important;
  height: 0 !important;
  margin: 0 !important;
  padding: 0 !important;
}

/* 图标样式 */
.el-icon-house, .el-icon-setting, .el-icon-user, .el-icon-menu, .el-icon-s-custom, .el-icon-lock {
  margin-right: 8px;
  width: 20px;
  text-align: center;
  font-size: 16px;
}

/* 一级菜单字体样式 */
.sidebar-menu .el-menu-item span,
.sidebar-menu .el-sub-menu__title span {
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* 二级菜单字体样式 */
.sidebar-menu .el-sub-menu .el-menu-item span {
  font-size: 13px;
  font-weight: 400;
  letter-spacing: 0.3px;
}
</style>