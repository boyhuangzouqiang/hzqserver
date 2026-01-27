<template>
  <div class="user-management">
    <h2>用户管理</h2>
    <el-button type="primary" @click="addUser">添加用户</el-button>
    <el-table :data="users" style="width: 100%" border>
      <el-table-column prop="username" label="用户名" width="180"></el-table-column>
      <el-table-column prop="email" label="邮箱" width="180"></el-table-column>
      <el-table-column prop="phone" label="手机号"></el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template slot-scope="scope">
          <el-button size="small" @click="editUser(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="deleteUser(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <!-- 添加/编辑用户对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名"></el-input>
        </el-form-item>
        <el-form-item label="密码" v-if="!isEdit">
          <el-input type="password" v-model="form.password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="请输入邮箱"></el-input>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" active-value="1" inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveUser">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/utils/api.js';
// 引入Element Plus组件
import { ElButton, ElTable, ElTableColumn, ElDialog, ElForm, ElFormItem, ElInput, ElSwitch, ElTag, ElMessage } from 'element-plus';

export default {
  name: 'UserManagement',
  data() {
    return {
      users: [],
      dialogVisible: false,
      isEdit: false,
      dialogTitle: '',
      form: {
        id: null,
        username: '',
        password: '',
        email: '',
        phone: '',
        status: 1
      }
    };
  },
  created() {
    this.loadUsers();
  },
  methods: {
    async loadUsers() {
      try {
        const response = await api.get('/authservice/user/list');
        this.users = response.data.data || [];
      } catch (error) {
        console.error('加载用户列表失败:', error);
        this.$message.error('加载用户列表失败');
      }
    },
    addUser() {
      this.isEdit = false;
      this.dialogTitle = '添加用户';
      this.form = {
        id: null,
        username: '',
        password: '',
        email: '',
        phone: '',
        status: 1
      };
      this.dialogVisible = true;
    },
    editUser(user) {
      this.isEdit = true;
      this.dialogTitle = '编辑用户';
      this.form = { ...user };
      this.dialogVisible = true;
    },
    async saveUser() {
      try {
        if (this.isEdit) {
          await api.put('/api/user/update', this.form);
          this.$message.success('用户更新成功');
        } else {
          await api.post('/api/user/create', this.form);
          this.$message.success('用户创建成功');
        }
        this.dialogVisible = false;
        this.loadUsers();
      } catch (error) {
        console.error('保存用户失败:', error);
        this.$message.error('保存用户失败');
      }
    },
    async deleteUser(id) {
      try {
        await api.delete(`/api/user/delete/${id}`);
        this.$message.success('用户删除成功');
        this.loadUsers();
      } catch (error) {
        console.error('删除用户失败:', error);
        this.$message.error('删除用户失败');
      }
    }
  }
};
</script>

<style scoped>
.user-management {
  padding: 20px;
}
</style>