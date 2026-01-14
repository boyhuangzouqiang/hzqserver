<template>
  <div class="role-management">
    <h2>角色管理</h2>
    <el-button type="primary" @click="addRole">添加角色</el-button>
    <el-table :data="roles" style="width: 100%">
      <el-table-column prop="roleName" label="角色名称" width="180"></el-table-column>
      <el-table-column prop="roleCode" label="角色编码" width="180"></el-table-column>
      <el-table-column prop="roleDesc" label="角色描述"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button size="mini" @click="editRole(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deleteRole(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :visible.sync="dialogVisible" title="角色信息">
      <el-form :model="currentRole" label-width="80px">
        <el-form-item label="角色名称">
          <el-input v-model="currentRole.roleName"></el-input>
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input v-model="currentRole.roleCode"></el-input>
        </el-form-item>
        <el-form-item label="角色描述">
          <el-input v-model="currentRole.roleDesc" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="currentRole.status" active-value="1" inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveRole">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/utils/api';

export default {
  name: 'RoleManagement',
  data() {
    return {
      roles: [],
      dialogVisible: false,
      currentRole: {
        id: null,
        roleName: '',
        roleCode: '',
        roleDesc: '',
        status: 1
      }
    };
  },
  mounted() {
    this.loadRoles();
  },
  methods: {
    async loadRoles() {
      try {
        const response = await api.get('/api/role/list');
        this.roles = response.data.data || [];
      } catch (error) {
        console.error('加载角色失败:', error);
      }
    },
    addRole() {
      this.currentRole = {
        id: null,
        roleName: '',
        roleCode: '',
        roleDesc: '',
        status: 1
      };
      this.dialogVisible = true;
    },
    editRole(role) {
      this.currentRole = { ...role };
      this.dialogVisible = true;
    },
    async saveRole() {
      try {
        if (this.currentRole.id) {
          await api.put('/api/role/update', this.currentRole);
          this.$message.success('角色更新成功');
        } else {
          await api.post('/api/role/create', this.currentRole);
          this.$message.success('角色创建成功');
        }
        this.dialogVisible = false;
        this.loadRoles();
      } catch (error) {
        console.error('保存角色失败:', error);
      }
    },
    async deleteRole(id) {
      try {
        await api.delete(`/api/role/delete/${id}`);
        this.$message.success('角色删除成功');
        this.loadRoles();
      } catch (error) {
        console.error('删除角色失败:', error);
      }
    }
  }
};
</script>

<style scoped>
.role-management {
  padding: 20px;
}
</style>