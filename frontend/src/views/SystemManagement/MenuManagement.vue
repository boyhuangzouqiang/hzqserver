<template>
  <div class="menu-management">
    <h2>菜单管理</h2>
    <el-button type="primary" @click="addMenu">添加菜单</el-button>
    <el-table 
      :data="menus" 
      style="width: 100%" 
      row-key="id"
      border
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
      <el-table-column prop="menuName" label="菜单名称" width="180"></el-table-column>
      <el-table-column prop="menuCode" label="菜单编码" width="180"></el-table-column>
      <el-table-column prop="menuUrl" label="菜单URL"></el-table-column>
      <el-table-column prop="orderNum" label="排序" width="80"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button size="mini" @click="editMenu(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteMenu(scope.row.id)">删除</el-button>
          <el-button size="mini" type="primary" @click="addChildMenu(scope.row)">添加子菜单</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-dialog :visible.sync="dialogVisible" :title="dialogTitle">
      <el-form :model="currentMenu" label-width="80px">
        <el-form-item label="菜单名称">
          <el-input v-model="currentMenu.menuName" placeholder="请输入菜单名称"></el-input>
        </el-form-item>
        <el-form-item label="菜单编码">
          <el-input v-model="currentMenu.menuCode" placeholder="请输入菜单编码"></el-input>
        </el-form-item>
        <el-form-item label="菜单URL">
          <el-input v-model="currentMenu.menuUrl" placeholder="请输入菜单URL"></el-input>
        </el-form-item>
        <el-form-item label="上级菜单">
          <el-select v-model="currentMenu.parentId" placeholder="请选择上级菜单" clearable>
            <el-option
              v-for="item in menuOptions"
              :key="item.id"
              :label="item.menuName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="currentMenu.orderNum" :min="0"></el-input-number>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="currentMenu.status" active-value="1" inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveMenu">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/utils/api';

export default {
  name: 'MenuManagement',
  data() {
    return {
      menus: [],
      dialogVisible: false,
      dialogTitle: '',
      currentMenu: {
        id: null,
        menuName: '',
        menuCode: '',
        menuUrl: '',
        parentId: 0,
        orderNum: 0,
        status: 1
      },
      menuOptions: [] // 用于上级菜单选择
    };
  },
  mounted() {
    this.loadMenus();
  },
  methods: {
    async loadMenus() {
      try {
        const response = await api.get('/api/menu/list');
        this.menus = response.data.data || [];
        // 构建菜单选项列表
        this.buildMenuOptions();
      } catch (error) {
        console.error('加载菜单失败:', error);
        // 模拟数据用于演示
        this.menus = [
          { id: 1, menuName: '首页', menuCode: 'HOME', menuUrl: '/dashboard', parentId: 0, orderNum: 1, status: 1 },
          { id: 2, menuName: '系统管理', menuCode: 'SYSTEM', menuUrl: '#', parentId: 0, orderNum: 2, status: 1, children: [
            { id: 3, menuName: '用户管理', menuCode: 'USER_MGT', menuUrl: '/user-management', parentId: 2, orderNum: 1, status: 1 },
            { id: 4, menuName: '菜单管理', menuCode: 'MENU_MGT', menuUrl: '/menu-management', parentId: 2, orderNum: 2, status: 1 },
            { id: 5, menuName: '角色管理', menuCode: 'ROLE_MGT', menuUrl: '/role-management', parentId: 2, orderNum: 3, status: 1 }
          ]}
        ];
        this.buildMenuOptions();
      }
    },
    buildMenuOptions() {
      // 将树形结构扁平化为选项列表
      this.menuOptions = [];
      const flattenMenus = (menus, prefix = '') => {
        menus.forEach(menu => {
          this.menuOptions.push({
            id: menu.id,
            menuName: prefix + menu.menuName
          });
          if (menu.children && menu.children.length > 0) {
            flattenMenus(menu.children, prefix + '--');
          }
        });
      };
      flattenMenus(this.menus);
    },
    addMenu() {
      this.dialogTitle = '添加菜单';
      this.currentMenu = {
        id: null,
        menuName: '',
        menuCode: '',
        menuUrl: '',
        parentId: 0,
        orderNum: 0,
        status: 1
      };
      this.dialogVisible = true;
    },
    addChildMenu(parentMenu) {
      this.dialogTitle = '添加子菜单';
      this.currentMenu = {
        id: null,
        menuName: '',
        menuCode: '',
        menuUrl: '',
        parentId: parentMenu.id,
        orderNum: 0,
        status: 1
      };
      this.dialogVisible = true;
    },
    editMenu(menu) {
      this.dialogTitle = '编辑菜单';
      this.currentMenu = { ...menu };
      this.dialogVisible = true;
    },
    async saveMenu() {
      try {
        if (this.currentMenu.id) {
          await api.put('/api/menu/update', this.currentMenu);
          this.$message.success('菜单更新成功');
        } else {
          await api.post('/api/menu/create', this.currentMenu);
          this.$message.success('菜单创建成功');
        }
        this.dialogVisible = false;
        this.loadMenus();
      } catch (error) {
        console.error('保存菜单失败:', error);
        this.$message.error('保存菜单失败');
      }
    },
    async deleteMenu(id) {
      try {
        await api.delete(`/api/menu/delete/${id}`);
        this.$message.success('菜单删除成功');
        this.loadMenus();
      } catch (error) {
        console.error('删除菜单失败:', error);
        this.$message.error('删除菜单失败');
      }
    }
  }
};
</script>

<style scoped>
.menu-management {
  padding: 20px;
}
</style>