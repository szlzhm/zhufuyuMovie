import React, { useState, useEffect } from 'react';
import { listBlessVideos, getBlessVideoDetail, deleteBlessVideo } from '../services/blessVideo';
import { useMessage } from '../components/Dialog';

export default function BlessVideoPage() {
  const { success, error: showError, warning } = useMessage();
  const [videos, setVideos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [pageNo, setPageNo] = useState(1);
  const [pageSize] = useState(10);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    loadVideos();
  }, [pageNo]);

  const loadVideos = async () => {
    setLoading(true);
    try {
      const resp = await listBlessVideos({ pageNo, pageSize });
      if (resp.code === 0 && resp.data) {
        setVideos(resp.data.list || []);
        setTotal(resp.data.total || 0);
      } else {
        throw new Error(resp.message || '加载视频列表失败');
      }
    } catch (err) {
      console.error('加载视频列表失败:', err);
      showError(err.message || '加载视频列表失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    const confirmed = await warning('确定要删除这个祝福视频吗？', '确认删除');
    if (!confirmed) return;

    try {
      const resp = await deleteBlessVideo(id);
      if (resp.code === 0) {
        success('删除成功');
        loadVideos(); // 重新加载列表
      } else {
        throw new Error(resp.message || '删除失败');
      }
    } catch (err) {
      console.error('删除视频失败:', err);
      showError(err.message || '删除失败');
    }
  };

  const handleViewDetail = async (id) => {
    try {
      const resp = await getBlessVideoDetail(id);
      if (resp.code === 0 && resp.data) {
        // 显示视频详情，可以弹窗或跳转到详情页
        alert(`视频详情:
ID: ${resp.data.id}
标题: ${resp.data.title}
描述: ${resp.data.description}
视频路径: ${resp.data.videoPath}`);
      } else {
        throw new Error(resp.message || '获取视频详情失败');
      }
    } catch (err) {
      console.error('获取视频详情失败:', err);
      showError(err.message || '获取视频详情失败');
    }
  };

  const downloadVideo = (videoPath, title) => {
    const link = document.createElement('a');
    link.href = `http://localhost:8080/files/${videoPath}`;
    link.download = `${title}.mp4`;
    link.click();
  };

  const formatDate = (dateString) => {
    if (!dateString) return '';
    return new Date(dateString).toLocaleString('zh-CN');
  };

  return (
    <div className="page-container">
      <h2>祝福语视频管理</h2>

      {loading ? (
        <div className="loading">加载中...</div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>标题</th>
                <th>描述</th>
                <th>视频路径</th>
                <th>创建时间</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              {videos.length === 0 ? (
                <tr>
                  <td colSpan="6" style={{ textAlign: 'center', color: '#999' }}>
                    暂无数据
                  </td>
                </tr>
              ) : (
                videos.map((video) => (
                  <tr key={video.id}>
                    <td>{video.id}</td>
                    <td>{video.title}</td>
                    <td className="text-preview" title={video.description}>
                      {video.description || '-'}
                    </td>
                    <td className="text-preview" title={video.videoPath}>
                      {video.videoPath}
                    </td>
                    <td>{formatDate(video.createdTime)}</td>
                    <td>
                      <button 
                        className="btn-link" 
                        onClick={() => downloadVideo(video.videoPath, video.title)}
                      >
                        下载
                      </button>
                      <button 
                        className="btn-link" 
                        onClick={() => handleViewDetail(video.id)}
                      >
                        详情
                      </button>
                      <button 
                        className="btn-link" 
                        onClick={() => handleDelete(video.id)}
                      >
                        删除
                      </button>
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
