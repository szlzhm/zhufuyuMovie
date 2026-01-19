import React, { useState, useEffect } from 'react';
import { listMusicMaterials, createMusicMaterial, updateMusicMaterial, deleteMusicMaterial, getMusicMaterialDetail } from '../services/musicMaterial';
import { useMessage, useConfirm } from '../components/Dialog';

export default function MusicMaterialPage() {
  const { success, error: showError, warning } = useMessage();
  const { confirm } = useConfirm();
  const [materials, setMaterials] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pageNo, setPageNo] = useState(1);
  const [pageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [showUpdateForm, setShowUpdateForm] = useState(false);
  const [currentMaterial, setCurrentMaterial] = useState(null);
  const [formData, setFormData] = useState({
    title: '',
    author: '',
    description: '',
    duration: '',
    filePath: ''
  });

  useEffect(() => {
    loadMaterials();
  }, [pageNo]);

  const loadMaterials = async () => {
    setLoading(true);
    try {
      const resp = await listMusicMaterials({ pageNo, pageSize });
      if (resp.code === 0 && resp.data) {
        setMaterials(resp.data.list || []);
        setTotal(resp.data.total || 0);
      } else {
        throw new Error(resp.message || '加载音乐素材失败');
      }
    } catch (err) {
      console.error('加载音乐素材失败:', err);
      showError(err.message || '加载音乐素材失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!formData.title || !formData.filePath) {
      warning('标题和文件路径不能为空');
      return;
    }

    try {
      const resp = await createMusicMaterial(formData);
      if (resp.code === 0) {
        success('创建成功');
        setShowCreateForm(false);
        setFormData({ title: '', author: '', description: '', duration: '', filePath: '' });
        loadMaterials();
      } else {
        throw new Error(resp.message || '创建失败');
      }
    } catch (err) {
      console.error('创建音乐素材失败:', err);
      showError(err.message || '创建失败');
    }
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    if (!formData.title || !formData.filePath) {
      warning('标题和文件路径不能为空');
      return;
    }

    try {
      const resp = await updateMusicMaterial({ id: currentMaterial.id, ...formData });
      if (resp.code === 0) {
        success('更新成功');
        setShowUpdateForm(false);
        setCurrentMaterial(null);
        setFormData({ title: '', author: '', description: '', duration: '', filePath: '' });
        loadMaterials();
      } else {
        throw new Error(resp.message || '更新失败');
      }
    } catch (err) {
      console.error('更新音乐素材失败:', err);
      showError(err.message || '更新失败');
    }
  };

  const handleDelete = async (id) => {
    const confirmed = await confirm('确定要删除这个音乐素材吗？', '确认删除');
    if (!confirmed) return;

    try {
      const resp = await deleteMusicMaterial(id);
      if (resp.code === 0) {
        success('删除成功');
        loadMaterials();
      } else {
        throw new Error(resp.message || '删除失败');
      }
    } catch (err) {
      console.error('删除音乐素材失败:', err);
      showError(err.message || '删除失败');
    }
  };

  const handleEdit = (material) => {
    setCurrentMaterial(material);
    setFormData({
      title: material.title,
      author: material.author || '',
      description: material.description || '',
      duration: material.duration || '',
      filePath: material.filePath
    });
    setShowUpdateForm(true);
  };

  const handleViewDetail = async (id) => {
    try {
      const resp = await getMusicMaterialDetail(id);
      if (resp.code === 0 && resp.data) {
        alert(`音乐素材详情:
ID: ${resp.data.id}
标题: ${resp.data.title}
作者: ${resp.data.author || '无'}
描述: ${resp.data.description || '无'}
时长: ${resp.data.duration || '无'}
文件路径: ${resp.data.filePath}
创建时间: ${resp.data.createdTime}`);
      } else {
        throw new Error(resp.message || '获取音乐素材详情失败');
      }
    } catch (err) {
      console.error('获取音乐素材详情失败:', err);
      showError(err.message || '获取音乐素材详情失败');
    }
  };

  const playMusic = (filePath, title) => {
    const audio = new Audio(`http://localhost:8080/files/${filePath}`);
    audio.play().catch(err => {
      console.error('播放音乐失败:', err);
      showError('播放失败: ' + err.message);
    });
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>背景音乐管理</h2>
        <button className="btn btn-primary" onClick={() => setShowCreateForm(!showCreateForm)}>
          {showCreateForm ? '取消' : '创建音乐'}
        </button>
      </div>

      {showCreateForm && (
        <div className="form-overlay" onClick={() => setShowCreateForm(false)}>
          <div className="form-modal" onClick={(e) => e.stopPropagation()}>
            <h3>创建背景音乐</h3>
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
                <label>作者</label>
                <input
                  type="text"
                  value={formData.author}
                  onChange={(e) => setFormData({ ...formData, author: e.target.value })}
                  placeholder="请输入作者"
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
              <div className="form-group">
                <label>时长</label>
                <input
                  type="text"
                  value={formData.duration}
                  onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
                  placeholder="请输入时长，如：3:45"
                />
              </div>
              <div className="form-group">
                <label>文件路径 *</label>
                <input
                  type="text"
                  value={formData.filePath}
                  onChange={(e) => setFormData({ ...formData, filePath: e.target.value })}
                  placeholder="请输入文件路径"
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
            <h3>编辑背景音乐</h3>
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
                <label>作者</label>
                <input
                  type="text"
                  value={formData.author}
                  onChange={(e) => setFormData({ ...formData, author: e.target.value })}
                  placeholder="请输入作者"
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
              <div className="form-group">
                <label>时长</label>
                <input
                  type="text"
                  value={formData.duration}
                  onChange={(e) => setFormData({ ...formData, duration: e.target.value })}
                  placeholder="请输入时长，如：3:45"
                />
              </div>
              <div className="form-group">
                <label>文件路径 *</label>
                <input
                  type="text"
                  value={formData.filePath}
                  onChange={(e) => setFormData({ ...formData, filePath: e.target.value })}
                  placeholder="请输入文件路径"
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
                <th>作者</th>
                <th>描述</th>
                <th>时长</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {materials.length === 0 ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', color: '#999' }}>
                    暂无数据
                  </td>
                </tr>
              ) : (
                materials.map((material) => (
                  <tr key={material.id}>
                    <td>{material.id}</td>
                    <td>{material.title}</td>
                    <td>{material.author || '-'}</td>
                    <td className="text-preview" title={material.description}>
                      {material.description || '-'}
                    </td>
                    <td>{material.duration || '-'}</td>
                    <td>
                      <button className="btn-link" onClick={() => playMusic(material.filePath, material.title)}>播放</button>
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