<template>
  <div class="permission-management">
    <h2>权限管理</h2>
    <el-button type="primary" @click="addPermission">添加权限</el-button>
    <el-table :data="permissions" style="width: 100%">
      <el-table-column prop="permissionName" label="权限名称" width="180"></el-table-column>
      <el-table-column prop="permissionCode" label="权限编码" width="180"></el-table-column>
      <el-table-column prop="permissionDesc" label="权限描述"></el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'">
            {{ scope.row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150">
        <template slot-scope="scope">
          <el-button size="mini" @click="editPermission(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="deletePermission(scope.row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :visible.sync="dialogVisible" title="权限信息">
      <el-form :model="currentPermission" label-width="80px">
        <el-form-item label="权限名称">
          <el-input v-model="currentPermission.permissionName"></el-input>
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input v-model="currentPermission.permissionCode"></el-input>
        </el-form-item>
        <el-form-item label="权限描述">
          <el-input v-model="currentPermission.permissionDesc" type="textarea"></el-input>
        </el-form-item>
        <el-form-item label="父权限">
          <el-select v-model="currentPermission.parentId" placeholder="请选择父权限">
            <el-option
              v-for="item in parentPermissions"
              :key="item.id"
              :label="item.permissionName"
              :value="item.id">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="currentPermission.status" active-value="1" inactive-value="0"></el-switch>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="savePermission">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import api from '@/utils/api';

export default {
  name: 'PermissionManagement',
  data() {
    return {
      permissions: [],
      dialogVisible: false,
      currentPermission: {
        id: null,
        permissionName: '',
        permissionCode: '',
        permissionDesc: '',
        parentId: 0,
        status: 1
      },
      parentPermissions: []
    };
  },
  mounted() {
    this.loadPermissions();
  },
  methods: {
    async loadPermissions() {
      try {
        const response = await api.get('/api/permission/list');
        this.permissions = response.data.data || [];
        // 获取所有父权限（顶级权限）
        this.parentPermissions = this.permissions.filter(p => p.parentId === 0);
      } catch (error) {
        console.error('加载权限失败:', error);
        // 模拟数据用于演示
        this.permissions = [
          { id: 1, permissionName: '用户管理', permissionCode: 'USER_MGT', permissionDesc: '用户管理权限', parentId: 0, status: 1 },
          { id: 2, permissionName: '角色管理', permissionCode: 'ROLE_MGT', permissionDesc: '角色管理权限', parentId: 0, status: 1 },
          { id: 3, permissionName: '菜单管理', permissionCode: 'MENU_MGT', permissionDesc: '菜单管理权限', parentId: 0, status: 1 }
        ];
      }
    },
    addPermission() {
      this.currentPermission = {
        id: null,
        permissionName: '',
        permissionCode: '',
        permissionDesc: '',
        parentId: 0,
        status: 1
      };
      this.dialogVisible = true;
    },
    editPermission(permission) {
      this.currentPermission = { ...permission };
      this.dialogVisible = true;
    },
    async savePermission() {
      try {
        if (this.currentPermission.id) {
          await api.put('/api/permission/update', this.currentPermission);
          this.$message.success('权限更新成功');
        } else {
          await api.post('/api/permission/create', this.currentPermission);
          this.$message.success('权限创建成功');
        }
        this.dialogVisible = false;
        this.loadPermissions();
      } catch (error) {
        console.error('保存权限失败:', error);
        this.$message.error('保存权限失败');
      }
    },
    async deletePermission(id) {
      try {
        await api.delete(`/api/permission/delete/${id}`);
        this.$message.success('权限删除成功');
        this.loadPermissions();
      } catch (error) {
        console.error('删除权限失败:', error);
        this.$message.error('删除权限失败');
      }
    }
  }
};
</script>

<style scoped>
.permission-management {
  padding: 20px;
}
</style>