import React, { useState, useEffect } from 'react';
import {
  createVideoTask,
  getVideoTaskList,
  executeTask,
  executeTasks,
  confirmTaskToLibrary
} from '../services/videoTask';
import { listImageMaterials } from '../services/imageMaterial';
import { listMusicMaterials } from '../services/musicMaterial';
import { useMessage } from '../components/Dialog';

const VideoTaskPage = () => {
  const { success, error, warning, MessageComponent } = useMessage();
  const [taskList, setTaskList] = useState([]);
  const [total, setTotal] = useState(0);
  const [pageNo, setPageNo] = useState(1);
  const [pageSize] = useState(10);
  const [loading, setLoading] = useState(false);

  // 搜索条件
  const [searchTaskName, setSearchTaskName] = useState('');
  const [searchBatchName, setSearchBatchName] = useState('');

  // 创建任务对话窗口
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [formData, setFormData] = useState({
    taskName: '',
    batchName: '',
    videoTitle: '',
    backgroundImageId: null,
    backgroundMusicId: null,
    voiceAudioPath: '',
    textMaterialName: ''
  });

  // 素材选择相关
  const [showImageSelector, setShowImageSelector] = useState(false);
  const [showMusicSelector, setShowMusicSelector] = useState(false);
  const [imageList, setImageList] = useState([]);
  const [musicList, setMusicList] = useState([]);
  const [selectedImage, setSelectedImage] = useState(null);
  const [selectedMusic, setSelectedMusic] = useState(null);

  // 图片选择器状态
  const [imagePageNo, setImagePageNo] = useState(1);
  const [imagePageSize] = useState(10);
  const [imageTotal, setImageTotal] = useState(0);
  const [imageSearchName, setImageSearchName] = useState('');
  const [imageSortField, setImageSortField] = useState('createdTime');
  const [imageSortOrder, setImageSortOrder] = useState('desc');

  // 音乐选择器状态
  const [musicPageNo, setMusicPageNo] = useState(1);
  const [musicPageSize] = useState(10);
  const [musicTotal, setMusicTotal] = useState(0);
  const [musicSearchName, setMusicSearchName] = useState('');
  const [musicSortField, setMusicSortField] = useState('createdTime');
  const [musicSortOrder, setMusicSortOrder] = useState('desc');

  // 选中的任务(批量操作)
  const [selectedTaskIds, setSelectedTaskIds] = useState([]);

  useEffect(() => {
    loadTaskList();
  }, [pageNo, searchTaskName, searchBatchName]);

  const loadTaskList = async () => {
    setLoading(true);
    try {
      const response = await getVideoTaskList({
        pageNo,
        pageSize,
        taskName: searchTaskName || null,
        batchName: searchBatchName || null
      });
      setTaskList(response.list || []);
      setTotal(response.total || 0);
    } catch (err) {
      error('加载任务列表失败: ' + err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    setPageNo(1);
    loadTaskList();
  };

  const handleReset = () => {
    setSearchTaskName('');
    setSearchBatchName('');
    setPageNo(1);
    loadTaskList();
  };

  // 打开创建任务对话窗口
  const handleOpenCreateForm = () => {
    setFormData({
      taskName: '',
      batchName: '',
      videoTitle: '',
      backgroundImageId: null,
      backgroundMusicId: null,
      voiceAudioPath: '',
      textMaterialName: ''
    });
    setSelectedImage(null);
    setSelectedMusic(null);
    setShowCreateForm(true);
  };

  // 打开图片选择器
  const handleOpenImageSelector = async () => {
    setImagePageNo(1);
    setImageSearchName('');
    setImageSortField('createdTime');
    setImageSortOrder('desc');
    await loadImageList(1, '', 'createdTime', 'desc');
    setShowImageSelector(true);
  };

  // 加载图片列表
  const loadImageList = async (pageNo = imagePageNo, searchName = imageSearchName, sortField = imageSortField, sortOrder = imageSortOrder) => {
    try {
      const response = await listImageMaterials({
        pageNo,
        pageSize: imagePageSize,
        title: searchName || null,
        categoryId: null,
        sortField,
        sortOrder
      });
      setImageList(response.list || []);
      setImageTotal(response.total || 0);
    } catch (err) {
      error('加载图片列表失败: ' + err.message);
    }
  };

  // 图片搜索
  const handleImageSearch = () => {
    setImagePageNo(1);
    loadImageList(1, imageSearchName, imageSortField, imageSortOrder);
  };

  // 图片重置
  const handleImageReset = () => {
    setImageSearchName('');
    setImageSortField('createdTime');
    setImageSortOrder('desc');
    setImagePageNo(1);
    loadImageList(1, '', 'createdTime', 'desc');
  };

  // 图片排序
  const handleImageSort = (field) => {
    const newOrder = imageSortField === field && imageSortOrder === 'asc' ? 'desc' : 'asc';
    setImageSortField(field);
    setImageSortOrder(newOrder);
    loadImageList(imagePageNo, imageSearchName, field, newOrder);
  };

  // 图片分页
  const handleImagePageChange = (newPageNo) => {
    setImagePageNo(newPageNo);
    loadImageList(newPageNo, imageSearchName, imageSortField, imageSortOrder);
  };

  // 打开音乐选择器
  const handleOpenMusicSelector = async () => {
    setMusicPageNo(1);
    setMusicSearchName('');
    setMusicSortField('createdTime');
    setMusicSortOrder('desc');
    await loadMusicList(1, '', 'createdTime', 'desc');
    setShowMusicSelector(true);
  };

  // 加载音乐列表
  const loadMusicList = async (pageNo = musicPageNo, searchName = musicSearchName, sortField = musicSortField, sortOrder = musicSortOrder) => {
    try {
      const response = await listMusicMaterials({
        pageNo,
        pageSize: musicPageSize,
        name: searchName || null,
        sortField,
        sortOrder
      });
      setMusicList(response.list || []);
      setMusicTotal(response.total || 0);
    } catch (err) {
      error('加载背景音乐列表失败: ' + err.message);
    }
  };

  // 音乐搜索
  const handleMusicSearch = () => {
    setMusicPageNo(1);
    loadMusicList(1, musicSearchName, musicSortField, musicSortOrder);
  };

  // 音乐重置
  const handleMusicReset = () => {
    setMusicSearchName('');
    setMusicSortField('createdTime');
    setMusicSortOrder('desc');
    setMusicPageNo(1);
    loadMusicList(1, '', 'createdTime', 'desc');
  };

  // 音乐排序
  const handleMusicSort = (field) => {
    const newOrder = musicSortField === field && musicSortOrder === 'asc' ? 'desc' : 'asc';
    setMusicSortField(field);
    setMusicSortOrder(newOrder);
    loadMusicList(musicPageNo, musicSearchName, field, newOrder);
  };

  // 音乐分页
  const handleMusicPageChange = (newPageNo) => {
    setMusicPageNo(newPageNo);
    loadMusicList(newPageNo, musicSearchName, musicSortField, musicSortOrder);
  };

  // 选择图片
  const handleSelectImage = (image) => {
    setSelectedImage(image);
    setFormData({ ...formData, backgroundImageId: image.id });
    setShowImageSelector(false);
  };

  // 选择音乐
  const handleSelectMusic = (music) => {
    setSelectedMusic(music);
    setFormData({ ...formData, backgroundMusicId: music.id });
    setShowMusicSelector(false);
  };

  // 清除音乐选择
  const handleClearMusic = () => {
    setSelectedMusic(null);
    setFormData({ ...formData, backgroundMusicId: null });
  };

  // 创建任务
  const handleCreateTask = async () => {
    if (!formData.taskName) {
      warning('请输入任务名称');
      return;
    }
    if (!formData.batchName) {
      warning('请输入任务批次');
      return;
    }
    if (!formData.videoTitle) {
      warning('请输入视频标题');
      return;
    }
    if (!formData.backgroundImageId) {
      warning('请选择背景图片');
      return;
    }
    if (!formData.voiceAudioPath) {
      warning('请输入祝福语音频路径');
      return;
    }

    try {
      await createVideoTask({
        ...formData,
        taskType: 'AUDIO_TO_VIDEO'
      });
      success('创建任务成功');
      setShowCreateForm(false);
      loadTaskList();
    } catch (err) {
      error('创建任务失败: ' + err.message);
    }
  };

  // 执行单个任务
  const handleExecuteTask = async (taskId) => {
    try {
      await executeTask(taskId);
      success('任务已开始执行');
      // 延迟1.5秒后刷新列表,等待模拟执行完成
      setTimeout(() => {
        loadTaskList();
      }, 1500);
    } catch (err) {
      error('执行任务失败: ' + err.message);
    }
  };

  // 批量执行任务
  const handleBatchExecute = async () => {
    if (selectedTaskIds.length === 0) {
      warning('请选择要执行的任务');
      return;
    }
    try {
      await executeTasks(selectedTaskIds);
      success('批量任务已开始执行');
      setSelectedTaskIds([]);
      setTimeout(() => {
        loadTaskList();
      }, 1500);
    } catch (err) {
      error('批量执行失败: ' + err.message);
    }
  };

  // 确认入库
  const handleConfirmLibrary = async (taskId) => {
    try {
      await confirmTaskToLibrary(taskId);
      success('已确认入库');
      loadTaskList();
    } catch (err) {
      error('确认入库失败: ' + err.message);
    }
  };

  // 切换任务选中状态
  const handleToggleTask = (taskId) => {
    if (selectedTaskIds.includes(taskId)) {
      setSelectedTaskIds(selectedTaskIds.filter(id => id !== taskId));
    } else {
      setSelectedTaskIds([...selectedTaskIds, taskId]);
    }
  };

  // 全选/取消全选
  const handleToggleAll = () => {
    if (selectedTaskIds.length === taskList.length) {
      setSelectedTaskIds([]);
    } else {
      setSelectedTaskIds(taskList.map(task => task.id));
    }
  };

  return (
    <div className="page-container">
      <MessageComponent />
      <h2>祝福语视频创作任务</h2>

      {/* 搜索区域 */}
      <div className="search-bar">
        <input
          type="text"
          placeholder="任务名称"
          value={searchTaskName}
          onChange={(e) => setSearchTaskName(e.target.value)}
        />
        <input
          type="text"
          placeholder="任务批次"
          value={searchBatchName}
          onChange={(e) => setSearchBatchName(e.target.value)}
        />
        <button className="btn btn-primary" onClick={handleSearch}>
          搜索
        </button>
        <button className="btn btn-default" onClick={handleReset}>
          重置
        </button>
        <div style={{ flex: 1 }} />
        <button className="btn btn-success" onClick={handleOpenCreateForm}>
          新建任务
        </button>
        <button
          className="btn btn-primary"
          onClick={handleBatchExecute}
          disabled={selectedTaskIds.length === 0}
        >
          批量执行 ({selectedTaskIds.length})
        </button>
      </div>

      {/* 任务列表 */}
      <div className="table-container">
        <table>
          <thead>
            <tr>
              <th style={{ width: '40px' }}>
                <input
                  type="checkbox"
                  checked={taskList.length > 0 && selectedTaskIds.length === taskList.length}
                  onChange={handleToggleAll}
                />
              </th>
              <th>任务名称</th>
              <th>任务批次</th>
              <th>视频标题</th>
              <th>文案名称</th>
              <th>状态</th>
              <th>创建时间</th>
              <th>生成时间</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan="9" style={{ textAlign: 'center' }}>
                  加载中...
                </td>
              </tr>
            ) : taskList.length === 0 ? (
              <tr>
                <td colSpan="9" style={{ textAlign: 'center' }}>
                  暂无数据
                </td>
              </tr>
            ) : (
              taskList.map((task) => (
                <tr key={task.id}>
                  <td>
                    <input
                      type="checkbox"
                      checked={selectedTaskIds.includes(task.id)}
                      onChange={() => handleToggleTask(task.id)}
                    />
                  </td>
                  <td>{task.taskName}</td>
                  <td>{task.batchName}</td>
                  <td>{task.videoTitle}</td>
                  <td>{task.textMaterialName || '-'}</td>
                  <td>
                    <span className={`status-badge status-${task.taskStatus.toLowerCase()}`}>
                      {task.taskStatusText}
                    </span>
                    {task.taskStatus === 'FAILED' && task.errorMessage && (
                      <div className="error-message">{task.errorMessage}</div>
                    )}
                  </td>
                  <td>{task.createdTime}</td>
                  <td>{task.generatedTime || '-'}</td>
                  <td>
                    <div className="action-buttons">
                      {(task.taskStatus === 'PENDING' || task.taskStatus === 'FAILED') && (
                        <button
                          className="btn btn-sm btn-primary"
                          onClick={() => handleExecuteTask(task.id)}
                        >
                          {task.taskStatus === 'FAILED' ? '重新创作' : '开始创作'}
                        </button>
                      )}
                      {task.taskStatus === 'SUCCESS' && !task.confirmedToLibrary && (
                        <>
                          <button className="btn btn-sm btn-default">试听</button>
                          <button
                            className="btn btn-sm btn-success"
                            onClick={() => handleConfirmLibrary(task.id)}
                          >
                            确认入库
                          </button>
                        </>
                      )}
                      {task.confirmedToLibrary && (
                        <span className="text-success">已入库</span>
                      )}
                    </div>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>

      {/* 分页 */}
      <div className="pagination">
        <button
          className="btn btn-default"
          disabled={pageNo === 1}
          onClick={() => setPageNo(pageNo - 1)}
        >
          上一页
        </button>
        <span>
          第 {pageNo} 页，共 {Math.ceil(total / pageSize)} 页，共 {total} 条
        </span>
        <button
          className="btn btn-default"
          disabled={pageNo >= Math.ceil(total / pageSize)}
          onClick={() => setPageNo(pageNo + 1)}
        >
          下一页
        </button>
      </div>

      {/* 创建任务对话窗口 */}
      {showCreateForm && (
        <div className="modal-overlay">
          <div className="modal-content modal-large">
            <h3>创建视频任务</h3>
            <div className="form-group">
              <label>任务名称 *</label>
              <input
                type="text"
                value={formData.taskName}
                onChange={(e) => setFormData({ ...formData, taskName: e.target.value })}
                placeholder="如: 春节祝福第一批"
              />
            </div>
            <div className="form-group">
              <label>任务批次 *</label>
              <input
                type="text"
                value={formData.batchName}
                onChange={(e) => setFormData({ ...formData, batchName: e.target.value })}
                placeholder="如: 2025春节活动第一批"
              />
            </div>
            <div className="form-group">
              <label>视频标题 *</label>
              <input
                type="text"
                value={formData.videoTitle}
                onChange={(e) => setFormData({ ...formData, videoTitle: e.target.value })}
                placeholder="视频的标题"
              />
            </div>
            <div className="form-group">
              <label>背景图片 *</label>
              <div className="material-selector">
                {selectedImage ? (
                  <div className="selected-material">
                    <span>{selectedImage.name}</span>
                    <button
                      className="btn btn-sm btn-default"
                      onClick={handleOpenImageSelector}
                    >
                      重新选择
                    </button>
                  </div>
                ) : (
                  <button
                    className="btn btn-default"
                    onClick={handleOpenImageSelector}
                  >
                    选择背景图片
                  </button>
                )}
              </div>
            </div>
            <div className="form-group">
              <label>背景音乐(可选)</label>
              <div className="material-selector">
                {selectedMusic ? (
                  <div className="selected-material">
                    <span>{selectedMusic.name}</span>
                    <button
                      className="btn btn-sm btn-default"
                      onClick={handleOpenMusicSelector}
                    >
                      重新选择
                    </button>
                    <button
                      className="btn btn-sm btn-danger"
                      onClick={handleClearMusic}
                    >
                      清除
                    </button>
                  </div>
                ) : (
                  <button
                    className="btn btn-default"
                    onClick={handleOpenMusicSelector}
                  >
                    选择背景音乐
                  </button>
                )}
              </div>
            </div>
            <div className="form-group">
              <label>祝福语音频路径 *</label>
              <input
                type="text"
                value={formData.voiceAudioPath}
                onChange={(e) => setFormData({ ...formData, voiceAudioPath: e.target.value })}
                placeholder="音频文件路径，如: uploads/audios/voice/abc.mp3"
              />
            </div>
            <div className="form-group">
              <label>文案名称(可选)</label>
              <input
                type="text"
                value={formData.textMaterialName}
                onChange={(e) => setFormData({ ...formData, textMaterialName: e.target.value })}
                placeholder="祝福语文案名称"
              />
            </div>
            <div className="modal-actions">
              <button className="btn btn-primary" onClick={handleCreateTask}>
                创建
              </button>
              <button
                className="btn btn-default"
                onClick={() => setShowCreateForm(false)}
              >
                取消
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 图片选择器 */}
      {showImageSelector && (
        <div className="modal-overlay">
          <div className="modal-content modal-large">
            <h3>选择背景图片</h3>
            
            {/* 搜索区域 */}
            <div className="search-bar" style={{ marginBottom: '15px' }}>
              <input
                type="text"
                placeholder="按名称搜索"
                value={imageSearchName}
                onChange={(e) => setImageSearchName(e.target.value)}
                style={{ flex: 1, marginRight: '10px' }}
              />
              <button className="btn btn-primary" onClick={handleImageSearch}>搜索</button>
              <button className="btn btn-default" onClick={handleImageReset} style={{ marginLeft: '10px' }}>重置</button>
            </div>

            {/* 排序按钮 */}
            <div style={{ marginBottom: '15px', display: 'flex', gap: '10px' }}>
              <button 
                className="btn btn-sm btn-default"
                onClick={() => handleImageSort('name')}
              >
                按名称 {imageSortField === 'name' && (imageSortOrder === 'asc' ? '↑' : '↓')}
              </button>
              <button 
                className="btn btn-sm btn-default"
                onClick={() => handleImageSort('createdTime')}
              >
                按创建时间 {imageSortField === 'createdTime' && (imageSortOrder === 'asc' ? '↑' : '↓')}
              </button>
            </div>

            {/* 图片宫格 */}
            <div className="material-grid" style={{ maxHeight: '400px', overflowY: 'auto' }}>
              {imageList.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>暂无图片</div>
              ) : (
                imageList.map((image) => (
                  <div
                    key={image.id}
                    className="material-item"
                    onClick={() => handleSelectImage(image)}
                  >
                    <div className="material-preview">
                      <img src={`http://localhost:8080/files/${image.filePath}`} alt={image.name} />
                    </div>
                    <div className="material-name">{image.name}</div>
                  </div>
                ))
              )}
            </div>

            {/* 分页 */}
            <div className="pagination" style={{ marginTop: '15px' }}>
              <button
                className="btn btn-default"
                disabled={imagePageNo === 1}
                onClick={() => handleImagePageChange(imagePageNo - 1)}
              >
                上一页
              </button>
              <span style={{ margin: '0 15px' }}>
                第 {imagePageNo} 页，共 {Math.ceil(imageTotal / imagePageSize)} 页，共 {imageTotal} 条
              </span>
              <button
                className="btn btn-default"
                disabled={imagePageNo >= Math.ceil(imageTotal / imagePageSize)}
                onClick={() => handleImagePageChange(imagePageNo + 1)}
              >
                下一页
              </button>
            </div>

            <div className="modal-actions">
              <button
                className="btn btn-default"
                onClick={() => setShowImageSelector(false)}
              >
                取消
              </button>
            </div>
          </div>
        </div>
      )}

      {/* 音乐选择器 */}
      {showMusicSelector && (
        <div className="modal-overlay">
          <div className="modal-content modal-large">
            <h3>选择背景音乐</h3>
            
            {/* 搜索区域 */}
            <div className="search-bar" style={{ marginBottom: '15px' }}>
              <input
                type="text"
                placeholder="按名称搜索"
                value={musicSearchName}
                onChange={(e) => setMusicSearchName(e.target.value)}
                style={{ flex: 1, marginRight: '10px' }}
              />
              <button className="btn btn-primary" onClick={handleMusicSearch}>搜索</button>
              <button className="btn btn-default" onClick={handleMusicReset} style={{ marginLeft: '10px' }}>重置</button>
            </div>

            {/* 排序按钮 */}
            <div style={{ marginBottom: '15px', display: 'flex', gap: '10px' }}>
              <button 
                className="btn btn-sm btn-default"
                onClick={() => handleMusicSort('name')}
              >
                按名称 {musicSortField === 'name' && (musicSortOrder === 'asc' ? '↑' : '↓')}
              </button>
              <button 
                className="btn btn-sm btn-default"
                onClick={() => handleMusicSort('createdTime')}
              >
                按创建时间 {musicSortField === 'createdTime' && (musicSortOrder === 'asc' ? '↑' : '↓')}
              </button>
            </div>

            {/* 音乐列表 */}
            <div className="material-list" style={{ maxHeight: '400px', overflowY: 'auto' }}>
              {musicList.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '40px', color: '#999' }}>暂无背景音乐</div>
              ) : (
                musicList.map((music) => (
                  <div
                    key={music.id}
                    className="material-list-item"
                    onClick={() => handleSelectMusic(music)}
                  >
                    <div className="material-name">{music.name}</div>
                    <div className="material-desc">{music.description || '暂无简介'}</div>
                  </div>
                ))
              )}
            </div>

            {/* 分页 */}
            <div className="pagination" style={{ marginTop: '15px' }}>
              <button
                className="btn btn-default"
                disabled={musicPageNo === 1}
                onClick={() => handleMusicPageChange(musicPageNo - 1)}
              >
                上一页
              </button>
              <span style={{ margin: '0 15px' }}>
                第 {musicPageNo} 页，共 {Math.ceil(musicTotal / musicPageSize)} 页，共 {musicTotal} 条
              </span>
              <button
                className="btn btn-default"
                disabled={musicPageNo >= Math.ceil(musicTotal / musicPageSize)}
                onClick={() => handleMusicPageChange(musicPageNo + 1)}
              >
                下一页
              </button>
            </div>

            <div className="modal-actions">
              <button
                className="btn btn-default"
                onClick={() => setShowMusicSelector(false)}
              >
                取消
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default VideoTaskPage;
