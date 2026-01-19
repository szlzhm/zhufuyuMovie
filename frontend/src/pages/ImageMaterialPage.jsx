import React, { useState, useEffect } from 'react';
import { uploadImageMaterial, listImageMaterials, getImageMaterialDetail, updateImageMaterial } from '../services/imageMaterial';
import { listImageCategories } from '../services/imageCategory';
import { useMessage } from '../components/Dialog';

export default function ImageMaterialPage() {
  const { success, error, warning } = useMessage();
  const [materials, setMaterials] = useState([]);
  const [categories, setCategories] = useState([]);
  const [pageNo, setPageNo] = useState(1);
  const [pageSize] = useState(12);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  
  // 搜索条件
  const [searchTitle, setSearchTitle] = useState('');
  const [searchCategoryId, setSearchCategoryId] = useState('');
  
  // 上传弹窗
  const [showUploadModal, setShowUploadModal] = useState(false);
  const [uploadForm, setUploadForm] = useState({
    title: '',
    categoryId: '',
    description: '',
    file: null
  });
  
  // 详情弹窗
  const [showDetailModal, setShowDetailModal] = useState(false);
  const [detailData, setDetailData] = useState(null);
  
  // 编辑弹窗
  const [showEditModal, setShowEditModal] = useState(false);
  const [editForm, setEditForm] = useState({
    id: '',
    title: '',
    categoryId: '',
    description: ''
  });

  useEffect(() => {
    loadCategories();
    loadMaterials();
  }, [pageNo, searchTitle, searchCategoryId]);

  const loadCategories = async () => {
    try {
      const resp = await listImageCategories({ pageNo: 1, pageSize: 100 });
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
      const resp = await listImageMaterials({
        pageNo,
        pageSize,
        title: searchTitle || undefined,
        categoryId: searchCategoryId || undefined
      });
      if (resp.code === 0 && resp.data) {
        setMaterials(resp.data.list || []);
        setTotal(resp.data.total || 0);
      }
    } catch (err) {
      error('加载失败');
    } finally {
      setLoading(false);
    }
  };

  const handleUpload = async () => {
    if (!uploadForm.title || !uploadForm.categoryId || !uploadForm.file) {
      warning('请填写完整信息并选择图片');
      return;
    }

    const formData = new FormData();
    formData.append('title', uploadForm.title);
    formData.append('categoryId', uploadForm.categoryId);
    if (uploadForm.description) {
      formData.append('description', uploadForm.description);
    }
    formData.append('file', uploadForm.file);

    try {
      const resp = await uploadImageMaterial(formData);
      if (resp.code === 0) {
        success('上传成功');
        setShowUploadModal(false);
        setUploadForm({ title: '', categoryId: '', description: '', file: null });
        loadMaterials();
      } else {
        error(resp.message || '上传失败');
      }
    } catch (err) {
      error('上传失败');
    }
  };

  const handleViewDetail = async (id) => {
    try {
      const resp = await getImageMaterialDetail(id);
      if (resp.code === 0 && resp.data) {
        setDetailData(resp.data);
        setShowDetailModal(true);
      }
    } catch (err) {
      error('获取详情失败');
    }
  };

  const handleEdit = (material) => {
    setEditForm({
      id: material.id,
      title: material.title,
      categoryId: material.categoryId,
      description: material.description || ''
    });
    setShowEditModal(true);
  };

  const handleUpdate = async () => {
    if (!editForm.title || !editForm.categoryId) {
      warning('标题和分类不能为空');
      return;
    }

    try {
      const resp = await updateImageMaterial(editForm);
      if (resp.code === 0) {
        success('更新成功');
        setShowEditModal(false);
        loadMaterials();
      } else {
        error(resp.message || '更新失败');
      }
    } catch (err) {
      error('更新失败');
    }
  };

  const getCategoryName = (categoryId) => {
    const category = categories.find(c => c.id === categoryId);
    return category ? category.categoryName : '';
  };

  return (
    <div className="page-container">
      <h2>图片素材管理</h2>

      {/* 搜索和操作区 */}
      <div className="toolbar">
        <input
          type="text"
          placeholder="搜索标题"
          value={searchTitle}
          onChange={(e) => setSearchTitle(e.target.value)}
          className="search-input"
        />
        <select
          value={searchCategoryId}
          onChange={(e) => setSearchCategoryId(e.target.value)}
          className="select-input"
        >
          <option value="">全部分类</option>
          {categories.map(cat => (
            <option key={cat.id} value={cat.id}>{cat.categoryName}</option>
          ))}
        </select>
        <button onClick={() => setShowUploadModal(true)} className="btn btn-primary">
          上传图片
        </button>
      </div>

      {/* 宫格展示 */}
      {loading ? (
        <div className="loading">加载中...</div>
      ) : (
        <>
          <div className="image-grid">
            {materials.map(material => (
              <div key={material.id} className="image-card">
                <img
                  src={`http://localhost:8080/files/${material.imagePath}`}
                  alt={material.title}
                  onClick={() => handleViewDetail(material.id)}
                  className="image-thumbnail"
                />
                <div className="card-info">
                  <div className="card-title">{material.title}</div>
                  <div className="card-category">{material.categoryName}</div>
                  <div className="card-actions">
                    <button onClick={() => handleViewDetail(material.id)} className="btn-link">
                      详情
                    </button>
                    <button onClick={() => handleEdit(material)} className="btn-link">
                      编辑
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>

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
                onClick={() => setPageNo(p => p + 1)}
                disabled={pageNo >= Math.ceil(total / pageSize)}
                className="btn"
              >
                下一页
              </button>
            </div>
          )}
        </>
      )}

      {/* 上传弹窗 */}
      {showUploadModal && (
        <div className="modal-overlay" onClick={() => setShowUploadModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>上传图片素材</h3>
            <div className="form-group">
              <label>标题 *</label>
              <input
                type="text"
                value={uploadForm.title}
                onChange={(e) => setUploadForm({...uploadForm, title: e.target.value})}
              />
            </div>
            <div className="form-group">
              <label>分类 *</label>
              <select
                value={uploadForm.categoryId}
                onChange={(e) => setUploadForm({...uploadForm, categoryId: e.target.value})}
              >
                <option value="">请选择分类</option>
                {categories.map(cat => (
                  <option key={cat.id} value={cat.id}>{cat.categoryName}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>简介</label>
              <textarea
                value={uploadForm.description}
                onChange={(e) => setUploadForm({...uploadForm, description: e.target.value})}
                rows="3"
              />
            </div>
            <div className="form-group">
              <label>图片文件 *</label>
              <input
                type="file"
                accept="image/*"
                onChange={(e) => setUploadForm({...uploadForm, file: e.target.files[0]})}
              />
            </div>
            <div className="modal-actions">
              <button onClick={handleUpload} className="btn btn-primary">上传</button>
              <button onClick={() => setShowUploadModal(false)} className="btn">取消</button>
            </div>
          </div>
        </div>
      )}

      {/* 详情弹窗 */}
      {showDetailModal && detailData && (
        <div className="modal-overlay" onClick={() => setShowDetailModal(false)}>
          <div className="modal-content modal-large" onClick={(e) => e.stopPropagation()}>
            <h3>图片详情</h3>
            <div className="detail-content">
              <img
                src={`http://localhost:8080/files/${detailData.imagePath}`}
                alt={detailData.title}
                className="detail-image"
              />
              <div className="detail-info">
                <p><strong>标题:</strong> {detailData.title}</p>
                <p><strong>分类:</strong> {detailData.categoryName}</p>
                <p><strong>简介:</strong> {detailData.description || '无'}</p>
                <p><strong>创建时间:</strong> {detailData.createdTime}</p>
              </div>
            </div>
            <div className="modal-actions">
              <button onClick={() => setShowDetailModal(false)} className="btn">关闭</button>
            </div>
          </div>
        </div>
      )}

      {/* 编辑弹窗 */}
      {showEditModal && (
        <div className="modal-overlay" onClick={() => setShowEditModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <h3>编辑图片素材</h3>
            <div className="form-group">
              <label>标题 *</label>
              <input
                type="text"
                value={editForm.title}
                onChange={(e) => setEditForm({...editForm, title: e.target.value})}
              />
            </div>
            <div className="form-group">
              <label>分类 *</label>
              <select
                value={editForm.categoryId}
                onChange={(e) => setEditForm({...editForm, categoryId: e.target.value})}
              >
                {categories.map(cat => (
                  <option key={cat.id} value={cat.id}>{cat.categoryName}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>简介</label>
              <textarea
                value={editForm.description}
                onChange={(e) => setEditForm({...editForm, description: e.target.value})}
                rows="3"
              />
            </div>
            <div className="modal-actions">
              <button onClick={handleUpdate} className="btn btn-primary">保存</button>
              <button onClick={() => setShowEditModal(false)} className="btn">取消</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}