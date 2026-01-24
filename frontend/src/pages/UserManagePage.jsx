import React, { useEffect, useState } from 'react';
import { useMessage, useConfirm } from '../components/Dialog';
import { listUsers, createUser, resetUserPassword, toggleUserStatus } from '../services/admin.js';

export default function UserManagePage() {
  const { success, error: showError, warning } = useMessage();
  const { confirm } = useConfirm();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [formData, setFormData] = useState({ username: '', password: '', role: 'USER' });
  const [validationError, setValidationError] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    setLoading(true);
    setValidationError('');
    try {
      const resp = await listUsers({ keyword: searchKeyword });
      if (resp.code === 0 && resp.data) {
        setUsers(resp.data.list || []);
      } else {
        setValidationError(resp.message || '加载用户列表失败');
      }
    } catch (err) {
      setValidationError(err.message || '加载用户列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCreateUser = async (e) => {
    e.preventDefault();
    if (!formData.username || !formData.password) {
      setValidationError('用户名和密码不能为空');
      return;
    }
    try {
      const resp = await createUser(formData);
      if (resp.code === 0) {
        success('创建成功');
        setShowCreateForm(false);
        setFormData({ username: '', password: '', role: 'USER' });
        loadUsers();
      } else {
        setValidationError(resp.message || '创建失败');
      }
    } catch (err) {
      setValidationError(err.message || '创建失败');
    }
  };

  const handleResetPassword = async (userId) => {
    const newPassword = prompt('请输入新密码（至少6位）：');
    if (!newPassword || newPassword.length < 6) {
      showError('密码格式不正确');
      return;
    }
    try {
      const resp = await resetUserPassword({ userId, newPassword });
      if (resp.code === 0) {
        success('重置密码成功');
      } else {
        showError(resp.message || '重置密码失败');
      }
    } catch (err) {
      showError(err.message || '重置密码失败');
    }
  };

  const handleToggleStatus = async (userId, currentStatus) => {
    const action = currentStatus === 1 ? '禁用' : '启用';
    const newStatus = currentStatus === 1 ? 0 : 1;
    
    const confirmed = await confirm(`确定要${action}该用户吗？`, `${action}确认`);
    if (!confirmed) return;
    
    try {
      const resp = await toggleUserStatus({ userId, status: newStatus });
      if (resp.code === 0) {
        success(`${action}成功`);
        loadUsers();
      } else {
        showError(resp.message || `${action}失败`);
      }
    } catch (err) {
      showError(err.message || `${action}失败`);
    }
  };

  return (
    <div className="user-manage-page">
      <div className="page-header">
        <h2>用户管理</h2>
        <button className="btn btn-primary" onClick={() => setShowCreateForm(!showCreateForm)}>
          {showCreateForm ? '取消' : '创建用户'}
        </button>
      </div>

      {validationError && <div className="error-message">{validationError}</div>}

      {showCreateForm && (
        <div className="create-form-wrapper">
          <form className="create-form" onSubmit={handleCreateUser}>
            <div className="form-group">
              <label>用户名：</label>
              <input
                type="text"
                value={formData.username}
                onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                placeholder="请输入用户名"
              />
            </div>
            <div className="form-group">
              <label>密码：</label>
              <input
                type="password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                placeholder="请输入密码（至少6位）"
              />
            </div>
            <div className="form-group">
              <label>角色：</label>
              <select
                value={formData.role}
                onChange={(e) => setFormData({ ...formData, role: e.target.value })}
              >
                <option value="USER">普通用户</option>
                <option value="ADMIN">管理员</option>
              </select>
            </div>
            <div className="form-actions">
              <button type="submit" className="btn btn-primary">
                确定创建
              </button>
              <button
                type="button"
                className="btn btn-default"
                onClick={() => setShowCreateForm(false)}
              >
                取消
              </button>
            </div>
          </form>
        </div>
      )}

      <div className="search-bar">
        <input
          type="text"
          placeholder="搜索用户名或角色"
          value={searchKeyword}
          onChange={(e) => setSearchKeyword(e.target.value)}
        />
        <button className="btn btn-default" onClick={loadUsers}>
          搜索
        </button>
      </div>

      {loading ? (
        <div className="loading">加载中...</div>
      ) : (
        <table className="user-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>用户名</th>
              <th>角色</th>
              <th>状态</th>
              <th>最近登录</th>
              <th>创建时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {users.length === 0 ? (
              <tr>
                <td colSpan="7" style={{ textAlign: 'center', color: '#999' }}>
                  暂无数据
                </td>
              </tr>
            ) : (
              users.map((user) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.username}</td>
                  <td>{user.role === 'ADMIN' ? '管理员' : '普通用户'}</td>
                  <td>
                    <span className={user.status === 1 ? 'status-active' : 'status-disabled'}>
                      {user.status === 1 ? '启用' : '禁用'}
                    </span>
                  </td>
                  <td>{user.lastLoginTime || '-'}</td>
                  <td>{user.createdTime}</td>
                  <td>
                    <button
                      className="btn-link"
                      onClick={() => handleResetPassword(user.id)}
                    >
                      重置密码
                    </button>
                    {/* 管理员不显示禁用按钮 */}
                    {user.role !== 'ADMIN' && (
                      <button
                        className="btn-link"
                        onClick={() => handleToggleStatus(user.id, user.status)}
                      >
                        {user.status === 1 ? '禁用' : '启用'}
                      </button>
                    )}
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      )}
    </div>
  );
}