import React, { useState, useEffect } from 'react';
import { useMessage, useConfirm } from '../components/Dialog';
import { listVoiceMaterials, createVoiceMaterial, updateVoiceMaterial, toggleVoiceMaterialStatus } from '../services/voiceMaterial.js';

export default function VoiceMaterialPage() {
  const { success, error: showError, warning } = useMessage();
  const { confirm } = useConfirm();
  const [materials, setMaterials] = useState([]);
  const [loading, setLoading] = useState(false);
  const [validationError, setValidationError] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingMaterial, setEditingMaterial] = useState(null);
  const [pageNo, setPageNo] = useState(1);
  const [pageSize] = useState(10);
  const [total, setTotal] = useState(0);
  
  // 搜索条件
  const [searchName, setSearchName] = useState('');
  const [searchGender, setSearchGender] = useState('');
  const [searchLanguage, setSearchLanguage] = useState('');
  
  const [formData, setFormData] = useState({
    name: '',
    gender: '',
    language: '',
    ageGroup: '',
    type: ''
  });

  // 枚举选项
  const genderOptions = ['男', '女'];
  const languageOptions = ['普通话', '英文', '粤语', '客家话', '四川话', '上海话', '宁波话'];
  const ageGroupOptions = ['幼童', '儿童', '少年', '成年'];
  const typeOptions = ['系统音色', '自定义音色'];

  useEffect(() => {
    loadMaterials();
  }, [pageNo, searchName, searchGender, searchLanguage]);

  const loadMaterials = async () => {
    setLoading(true);
    setValidationError('');
    try {
      const resp = await listVoiceMaterials({ 
        pageNo, 
        pageSize,
        name: searchName || undefined,
        gender: searchGender || undefined,
        language: searchLanguage || undefined
      });
      if (resp.code === 0 && resp.data) {
        setMaterials(resp.data.list || []);
        setTotal(resp.data.total || 0);
      } else {
        setValidationError(resp.message || '加载音色素材列表失败');
      }
    } catch (err) {
      setValidationError(err.message || '加载音色素材列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleCreate = () => {
    setEditingMaterial(null);
    setFormData({
      name: '',
      gender: '',
      language: '普通话',
      ageGroup: '',
      type: '系统音色'
    });
    setShowForm(true);
  };

  const handleEdit = (material) => {
    setEditingMaterial(material);
    setFormData({
      name: material.name,
      gender: material.gender,
      language: material.language,
      ageGroup: material.ageGroup || '',
      type: material.type
    });
    setShowForm(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.name.trim()) {
      warning('请输入音色名称');
      return;
    }
    if (!formData.gender) {
      warning('请选择音色性别');
      return;
    }
    if (!formData.language) {
      warning('请选择音色语言');
      return;
    }
    if (!formData.type) {
      warning('请选择音色类型');
      return;
    }

    try {
      if (editingMaterial) {
        // 更新
        const resp = await updateVoiceMaterial({
          id: editingMaterial.id,
          name: formData.name,
          gender: formData.gender,
          language: formData.language,
          ageGroup: formData.ageGroup || null,
          type: formData.type
        });
        if (resp.code === 0) {
          success('更新成功');
          setShowForm(false);
          loadMaterials();
        } else {
          showError(resp.message || '更新失败');
        }
      } else {
        // 创建
        const resp = await createVoiceMaterial(formData);
        if (resp.code === 0) {
          success('创建成功');
          setShowForm(false);
          setPageNo(1);
          loadMaterials();
        } else {
          showError(resp.message || '创建失败');
        }
      }
    } catch (err) {
      showError(err.message || '操作失败');
    }
  };

  const handleToggleStatus = async (material) => {
    const newStatus = material.status === 1 ? 0 : 1;
    const action = newStatus === 1 ? '启用' : '禁用';
    
    const confirmed = await confirm(`确定要${action}该音色吗？`, `${action}确认`);
    if (!confirmed) return;
    
    try {
      const resp = await toggleVoiceMaterialStatus({
        id: material.id,
        status: newStatus
      });
      if (resp.code === 0) {
        success(`${action}成功`);
        loadMaterials();
      } else {
        showError(resp.message || `${action}失败`);
      }
    } catch (err) {
      showError(err.message || `${action}失败`);
    }
  };

  const handleSearch = () => {
    setPageNo(1);
    loadMaterials();
  };

  const handleReset = () => {
    setSearchName('');
    setSearchGender('');
    setSearchLanguage('');
    setPageNo(1);
  };

  return (
    <div className="voice-material-page">
      <div className="page-header">
        <h2>音色素材管理</h2>
        <button className="btn btn-primary" onClick={handleCreate}>
          创建音色
        </button>
      </div>

      {validationError && <div className="error-message">{validationError}</div>}

      {/* 搜索区域 */}
      <div className="search-bar">
        <input
          type="text"
          placeholder="音色名称"
          value={searchName}
          onChange={(e) => setSearchName(e.target.value)}
        />
        <select
          value={searchGender}
          onChange={(e) => setSearchGender(e.target.value)}
        >
          <option value="">全部性别</option>
          {genderOptions.map(option => (
            <option key={option} value={option}>{option}</option>
          ))}
        </select>
        <select
          value={searchLanguage}
          onChange={(e) => setSearchLanguage(e.target.value)}
        >
          <option value="">全部语言</option>
          {languageOptions.map(option => (
            <option key={option} value={option}>{option}</option>
          ))}
        </select>
        <button className="btn btn-primary" onClick={handleSearch}>查询</button>
        <button className="btn btn-default" onClick={handleReset}>重置</button>
      </div>

      {/* 表单对话窗口 */}
      {showForm && (
        <div className="form-overlay">
          <div className="form-modal">
            <h3>{editingMaterial ? '编辑音色' : '创建音色'}</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>音色名称 *</label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  placeholder="如: 温柔女声"
                  required
                />
              </div>
              <div className="form-group">
                <label>音色性别 *</label>
                <select
                  value={formData.gender}
                  onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
                  required
                >
                  <option value="">请选择</option>
                  {genderOptions.map(option => (
                    <option key={option} value={option}>{option}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>音色语言 *</label>
                <select
                  value={formData.language}
                  onChange={(e) => setFormData({ ...formData, language: e.target.value })}
                  required
                >
                  <option value="">请选择</option>
                  {languageOptions.map(option => (
                    <option key={option} value={option}>{option}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>音色年龄</label>
                <select
                  value={formData.ageGroup}
                  onChange={(e) => setFormData({ ...formData, ageGroup: e.target.value })}
                >
                  <option value="">请选择(可选)</option>
                  {ageGroupOptions.map(option => (
                    <option key={option} value={option}>{option}</option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label>音色类型 *</label>
                <select
                  value={formData.type}
                  onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                  required
                >
                  <option value="">请选择</option>
                  {typeOptions.map(option => (
                    <option key={option} value={option}>{option}</option>
                  ))}
                </select>
              </div>
              <div className="form-actions">
                <button type="submit" className="btn btn-primary">
                  确定
                </button>
                <button
                  type="button"
                  className="btn btn-default"
                  onClick={() => setShowForm(false)}
                >
                  取消
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {loading ? (
        <div className="loading">加载中...</div>
      ) : (
        <>
          <table className="data-table">
            <thead>
              <tr>
                <th>名称</th>
                <th>性别</th>
                <th>语言</th>
                <th>年龄段</th>
                <th>类型</th>
                <th>状态</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {materials.map(material => (
                <tr key={material.id}>
                  <td>{material.name}</td>
                  <td>{material.gender}</td>
                  <td>{material.language}</td>
                  <td>{material.ageGroup || '-'}</td>
                  <td>{material.type}</td>
                  <td>
                    <span className={material.status === 1 ? 'status-active' : 'status-disabled'}>
                      {material.status === 1 ? '启用' : '禁用'}
                    </span>
                  </td>
                  <td>{material.createdTime}</td>
                  <td>
                    <button onClick={() => handleEdit(material)} className="btn-link">
                      编辑
                    </button>
                    <button onClick={() => handleToggleStatus(material)} className="btn-link">
                      {material.status === 1 ? '禁用' : '启用'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>

          {/* 分页 */}
          {total > pageSize && (
            <div className="pagination">
              <button
                className="btn"
                disabled={pageNo === 1}
                onClick={() => setPageNo(pageNo - 1)}
              >
                上一页
              </button>
              <span className="page-info">
                第 {pageNo} 页 / 共 {Math.ceil(total / pageSize)} 页 (总计 {total} 条)
              </span>
              <button
                className="btn"
                disabled={pageNo >= Math.ceil(total / pageSize)}
                onClick={() => setPageNo(pageNo + 1)}
              >
                下一页
              </button>
            </div>
          )}
        </>
      )}
    </div>
  );
}