import React, { useState, useEffect } from 'react';
import { listEmotions, createEmotion, updateEmotion } from '../services/emotion';
import { useMessage, useConfirm } from '../components/Dialog';

export default function EmotionManagePage() {
  const { success, error: showError, warning } = useMessage();
  const { confirm } = useConfirm();
  const [emotions, setEmotions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [showUpdateForm, setShowUpdateForm] = useState(false);
  const [currentEmotion, setCurrentEmotion] = useState(null);
  const [formData, setFormData] = useState({ emotionName: '', description: '' });

  useEffect(() => {
    loadEmotions();
  }, []);

  const loadEmotions = async () => {
    setLoading(true);
    try {
      const resp = await listEmotions();
      if (resp.code === 0 && resp.data) {
        setEmotions(resp.data.list || []);
      } else {
        throw new Error(resp.message || '加载情绪列表失败');
      }
    } catch (err) {
      console.error('加载情绪列表失败:', err);
      showError(err.message || '加载情绪列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = async (e) => {
    e.preventDefault();
    if (!formData.emotionName) {
      warning('情绪名称不能为空');
      return;
    }

    try {
      const resp = await createEmotion(formData);
      if (resp.code === 0) {
        success('创建成功');
        setShowCreateForm(false);
        setFormData({ emotionName: '', description: '' });
        loadEmotions();
      } else {
        throw new Error(resp.message || '创建失败');
      }
    } catch (err) {
      console.error('创建情绪失败:', err);
      showError(err.message || '创建失败');
    }
  };

  const handleUpdate = async (e) => {
    e.preventDefault();
    if (!formData.emotionName) {
      warning('情绪名称不能为空');
      return;
    }

    try {
      const resp = await updateEmotion({ id: currentEmotion.id, ...formData });
      if (resp.code === 0) {
        success('更新成功');
        setShowUpdateForm(false);
        setCurrentEmotion(null);
        setFormData({ emotionName: '', description: '' });
        loadEmotions();
      } else {
        throw new Error(resp.message || '更新失败');
      }
    } catch (err) {
      console.error('更新情绪失败:', err);
      showError(err.message || '更新失败');
    }
  };

  const handleEdit = (emotion) => {
    setCurrentEmotion(emotion);
    setFormData({
      emotionName: emotion.emotionName,
      description: emotion.description || ''
    });
    setShowUpdateForm(true);
  };

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>情绪管理</h2>
        <button className="btn btn-primary" onClick={() => setShowCreateForm(!showCreateForm)}>
          {showCreateForm ? '取消' : '创建情绪'}
        </button>
      </div>

      {showCreateForm && (
        <div className="form-overlay" onClick={() => setShowCreateForm(false)}>
          <div className="form-modal" onClick={(e) => e.stopPropagation()}>
            <h3>创建情绪</h3>
            <form onSubmit={handleCreate}>
              <div className="form-group">
                <label>情绪名称 *</label>
                <input
                  type="text"
                  value={formData.emotionName}
                  onChange={(e) => setFormData({ ...formData, emotionName: e.target.value })}
                  placeholder="请输入情绪名称"
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
            <h3>编辑情绪</h3>
            <form onSubmit={handleUpdate}>
              <div className="form-group">
                <label>情绪名称 *</label>
                <input
                  type="text"
                  value={formData.emotionName}
                  onChange={(e) => setFormData({ ...formData, emotionName: e.target.value })}
                  placeholder="请输入情绪名称"
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
                <th>情绪名称</th>
                <th>描述</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {emotions.length === 0 ? (
                <tr>
                  <td colSpan="5" style={{ textAlign: 'center', color: '#999' }}>
                    暂无数据
                  </td>
                </tr>
              ) : (
                emotions.map((emotion) => (
                  <tr key={emotion.id}>
                    <td>{emotion.id}</td>
                    <td>{emotion.emotionName}</td>
                    <td>{emotion.description || '-'}</td>
                    <td>{new Date(emotion.createdTime).toLocaleString('zh-CN')}</td>
                    <td>
                      <button className="btn-link" onClick={() => handleEdit(emotion)}>编辑</button>
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