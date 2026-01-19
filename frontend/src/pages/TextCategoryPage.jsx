import React, { useState, useEffect } from 'react';
import { listTextCategories, createTextCategory, updateTextCategory, deleteTextCategory } from '../services/textCategory';
import { useMessage, useConfirm } from '../components/Dialog';

export default function TextCategoryPage() {
  const { success, error: showError, warning } = useMessage();
  const { confirm } = useConfirm();
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [showUpdateForm, setShowUpdateForm] = useState(false);
  const [currentCategory, setCurrentCategory] = useState(null);
  const [formData, setFormData] = useState({ categoryName: '', description: '' });

  useEffect(() => {
    loadCategories();
  }, []);

  const loadCategories = async () => {
    setLoading(true);
    try {
      const resp = await listTextCategories({ pageNo: 1, pageSize: 100 });
      if (resp.code === 0 && resp.data) {
        setCategories(resp.data.list || []);
      } else {
        throw new Error(resp.message || '加载文案分类失败');
      }
    } catch (err) {
      console.error('加载文案分类失败:', err);
      showError(err.message || '加载文案分类失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!formData.categoryName) {
      warning('分类名称不能为空');
      return;
    }

    try {
      const resp = await createTextCategory(formData);
      if (resp.code === 0) {
        success('创建成功');
        setShowCreateForm(false);
        setFormData({ categoryName: '', description: '' });
        loadCategories();
      } else {
        throw new Error(resp.message || '创建失败');
      }
    } catch (err) {
      console.error('创建文案分类失败:', err);
      showError(err.message || '创建失败');
    }
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    if (!formData.categoryName) {
      warning('分类名称不能为空');
      return;
    }

    try {
      const resp = await updateTextCategory({ id: currentCategory.id, ...formData });
      if (resp.code === 0) {
        success('更新成功');
        setShowUpdateForm(false);
        setCurrentCategory(null);
        setFormData({ categoryName: '', description: '' });
        loadCategories();
      } else {
        throw new Error(resp.message || '更新失败');
      }
    } catch (err) {
      console.error('更新文案分类失败:', err);
      showError(err.message || '更新失败');
    }
  };

  const handleDelete = async (id) => {
    const confirmed = await confirm('确定要删除这个文案分类吗？', '确认删除');
    if (!confirmed) return;

    try {
      const resp = await deleteTextCategory(id);
      if (resp.code === 0) {
        success('删除成功');
        loadCategories();
      } else {
        throw new Error(resp.message || '删除失败');
      }
    } catch (err) {
      console.error('删除文案分类失败:', err);
      showError(err.message || '删除失败');
    }
  };

  const handleEdit = (category) => {
    setCurrentCategory(category);
    setFormData({
      categoryName: category.categoryName,
      description: category.description || ''
    });
    setShowUpdateForm(true);
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>文案分类管理</h2>
        <button className="btn btn-primary" onClick={() => setShowCreateForm(!showCreateForm)}>
          {showCreateForm ? '取消' : '创建分类'}
        </button>
      </div>

      {showCreateForm && (
        <div className="form-overlay" onClick={() => setShowCreateForm(false)}>
          <div className="form-modal" onClick={(e) => e.stopPropagation()}>
            <h3>创建文案分类</h3>
            <form onSubmit={handleCreate}>
              <div className="form-group">
                <label>分类名称 *</label>
                <input
                  type="text"
                  value={formData.categoryName}
                  onChange={(e) => setFormData({ ...formData, categoryName: e.target.value })}
                  placeholder="请输入分类名称"
                />
              </div>
              <div className="form-group">
                <label>描述</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="请输入描述"
                  rows="3"
                />
              </div>
              <div className="form-actions">
                <button type="submit" className="btn btn-primary">确定</button>
                <button type="button" className="btn btn-default" onClick={() => setShowCreateForm(false)}>取消</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {showUpdateForm && (
        <div className="form-overlay" onClick={() => setShowUpdateForm(false)}>
          <div className="form-modal" onClick={(e) => e.stopPropagation()}>
            <h3>编辑文案分类</h3>
            <form onSubmit={handleUpdate}>
              <div className="form-group">
                <label>分类名称 *</label>
                <input
                  type="text"
                  value={formData.categoryName}
                  onChange={(e) => setFormData({ ...formData, categoryName: e.target.value })}
                  placeholder="请输入分类名称"
                />
              </div>
              <div className="form-group">
                <label>描述</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="请输入描述"
                  rows="3"
                />
              </div>
              <div className="form-actions">
                <button type="submit" className="btn btn-primary">确定</button>
                <button type="button" className="btn btn-default" onClick={() => setShowUpdateForm(false)}>取消</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {loading ? (
        <div className="loading">加载中...</div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>分类名称</th>
                <th>描述</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {categories.length === 0 ? (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', color: '#999' }}>
                    暂无数据
                  </td>
                </tr>
              ) : (
                categories.map((category) => (
                  <tr key={category.id}>
                    <td>{category.id}</td>
                    <td>{category.categoryName}</td>
                    <td>{category.description || '-'}</td>
                    <td>{new Date(category.createdTime).toLocaleString('zh-CN')}</td>
                    <td>
                      <button className="btn-link" onClick={() => handleEdit(category)}>编辑</button>
                      <button className="btn-link" onClick={() => handleDelete(category.id)}>删除</button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}