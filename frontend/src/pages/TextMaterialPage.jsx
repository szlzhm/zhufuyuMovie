import React, { useState, useEffect } from 'react';
import { listTextMaterials, createTextMaterial, updateTextMaterial, deleteTextMaterial, getTextMaterialDetail } from '../services/textMaterial';
import { listTextCategories } from '../services/textCategory';
import { useMessage, useConfirm } from '../components/Dialog';

export default function TextMaterialPage() {
  const { success, error, warning } = useMessage();
  const { confirm } = useConfirm();
  const [materials, setMaterials] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pageNo, setPageNo] = useState(1);
  const [pageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [showUpdateForm, setShowUpdateForm] = useState(false);
  const [currentMaterial, setCurrentMaterial] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    content: '',
    categoryId: '',
    description: ''
  });

  useEffect(() => {
    loadCategories();
    loadMaterials();
  }, [pageNo]);

  const loadCategories = async () => {
    try {
      const resp = await listTextCategories({ pageNo: 1, pageSize: 100 });
      if (resp.code === 0 && resp.data) {
        setCategories(resp.data.list || []);
      }
    } catch (err) {
      error('加载分类失败');
    }
  };

  const loadMaterials = async () => {
    setLoading(true);
    try {
      const resp = await listTextMaterials({ pageNo, pageSize });
      if (resp.code === 0 && resp.data) {
        setMaterials(resp.data.list || []);
        setTotal(resp.data.total || 0);
      } else {
        throw new Error(resp.message || '加载文案素材失败');
      }
    } catch (err) {
      console.error('加载文案素材失败:', err);
      error(err.message || '加载文案素材失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!formData.title || !formData.content || !formData.categoryId) {
      warning('标题、内容和分类不能为空');
      return;
    }

    try {
      const resp = await createTextMaterial(formData);
      if (resp.code === 0) {
        success('创建成功');
        setShowCreateForm(false);
        setFormData({ title: '', content: '', categoryId: '', description: '' });
        loadMaterials();
      } else {
        throw new Error(resp.message || '创建失败');
      }
    } catch (err) {
      console.error('创建文案素材失败:', err);
      error(err.message || '创建失败');
    }
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    if (!formData.title || !formData.content || !formData.categoryId) {
      warning('标题、内容和分类不能为空');
      return;
    }

    try {
      const resp = await updateTextMaterial({ id: currentMaterial.id, ...formData });
      if (resp.code === 0) {
        success('更新成功');
        setShowUpdateForm(false);
        setCurrentMaterial(null);
        setFormData({ title: '', content: '', categoryId: '', description: '' });
        loadMaterials();
      } else {
        throw new Error(resp.message || '更新失败');
      }
    } catch (err) {
      console.error('更新文案素材失败:', err);
      error(err.message || '更新失败');
    }
  };

  const handleDelete = async (id) => {
    const confirmed = await confirm('确定要删除这个文案素材吗？', '确认删除');
    if (!confirmed) return;

    try {
      const resp = await deleteTextMaterial(id);
      if (resp.code === 0) {
        success('删除成功');
        loadMaterials();
      } else {
        throw new Error(resp.message || '删除失败');
      }
    } catch (err) {
      console.error('删除文案素材失败:', err);
      error(err.message || '删除失败');
    }
  };

  const handleEdit = (material) => {
    setCurrentMaterial(material);
    setFormData({
      title: material.title,
      content: material.content,
      categoryId: material.categoryId,
      description: material.description || ''
    });
    setShowUpdateForm(true);
  };

  const handleViewDetail = async (id) => {
    try {
      const resp = await getTextMaterialDetail(id);
      if (resp.code === 0 && resp.data) {
        alert(`文案素材详情:
ID: ${resp.data.id}
标题: ${resp.data.title}
内容: ${resp.data.content}
分类: ${resp.data.categoryName}
描述: ${resp.data.description || '无'}
创建时间: ${resp.data.createdTime}`);
      } else {
        throw new Error(resp.message || '获取文案素材详情失败');
      }
    } catch (err) {
      console.error('获取文案素材详情失败:', err);
      error(err.message || '获取文案素材详情失败');
    }
  };

  const getCategoryName = (categoryId) => {
    const category = categories.find(c => c.id === categoryId);
    return category ? category.categoryName : '';
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>文案素材管理</h2>
        <button className="btn btn-primary" onClick={() => setShowCreateForm(!showCreateForm)}>
          {showCreateForm ? '取消' : '创建文案'}
        </button>
      </div>

      {showCreateForm && (
        <div className="form-overlay" onClick={() => setShowCreateForm(false)}>
          <div className="form-modal" onClick={(e) => e.stopPropagation()}>
            <h3>创建文案素材</h3>
            <form onSubmit={handleCreate}>
              <div className="form-group">
                <label>标题 *</label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="请输入标题"
                />
              </div>
              <div className="form-group">
                <label>内容 *</label>
                <textarea
                  value={formData.content}
                  onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                  placeholder="请输入文案内容"
                  rows="4"
                />
              </div>
              <div className="form-group">
                <label>分类 *</label>
                <select
                  value={formData.categoryId}
                  onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                >
                  <option value="">请选择分类</option>
                  {categories.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.categoryName}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>描述</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="请输入描述"
                  rows="2"
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
            <h3>编辑文案素材</h3>
            <form onSubmit={handleUpdate}>
              <div className="form-group">
                <label>标题 *</label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => setFormData({ ...formData, title: e.target.value })}
                  placeholder="请输入标题"
                />
              </div>
              <div className="form-group">
                <label>内容 *</label>
                <textarea
                  value={formData.content}
                  onChange={(e) => setFormData({ ...formData, content: e.target.value })}
                  placeholder="请输入文案内容"
                  rows="4"
                />
              </div>
              <div className="form-group">
                <label>分类 *</label>
                <select
                  value={formData.categoryId}
                  onChange={(e) => setFormData({ ...formData, categoryId: e.target.value })}
                >
                  <option value="">请选择分类</option>
                  {categories.map(cat => (
                    <option key={cat.id} value={cat.id}>{cat.categoryName}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>描述</label>
                <textarea
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                  placeholder="请输入描述"
                  rows="2"
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
                <th>标题</th>
                <th>内容</th>
                <th>分类</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {materials.length === 0 ? (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', color: '#999' }}>
                    暂无数据
                  </td>
                </tr>
              ) : (
                materials.map((material) => (
                  <tr key={material.id}>
                    <td>{material.id}</td>
                    <td>{material.title}</td>
                    <td className="text-preview" title={material.content}>
                      {material.content.length > 50 ? material.content.substring(0, 50) + '...' : material.content}
                    </td>
                    <td>{getCategoryName(material.categoryId)}</td>
                    <td>
                      <button className="btn-link" onClick={() => handleViewDetail(material.id)}>详情</button>
                      <button className="btn-link" onClick={() => handleEdit(material)}>编辑</button>
                      <button className="btn-link" onClick={() => handleDelete(material.id)}>删除</button>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>

          {/* 分页 */}
          {total > pageSize && (
            <div className="pagination">
              <button
                onClick={() => setPageNo(p => Math.max(1, p - 1))}
                disabled={pageNo === 1}
                className="btn"
              >
                上一页
              </button>
              <span className="page-info">
                第 {pageNo} 页 / 共 {Math.ceil(total / pageSize)} 页 (总计 {total} 条)
              </span>
              <button
                onClick={() => setPageNo(p => Math.max(p + 1, Math.ceil(total / pageSize)))}
                disabled={pageNo >= Math.ceil(total / pageSize)}
                className="btn"
              >
                下一页
              </button>
            </div>
          )}
        </div>
      )}
    </div>
  );
}