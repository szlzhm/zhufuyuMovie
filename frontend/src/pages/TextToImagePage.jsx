import React, { useState } from 'react';
import { useMessage } from '../components/Dialog';
import { generateImage, submitAsyncTask, getTaskStatus } from '../services/textToImage';

const TextToImagePage = () => {
  const { success, error, warning } = useMessage();
  
  // 支持的模型列表
  const supportedModels = [
    { name: 'Qwen-Image-Max', label: 'Qwen-Image-Max', description: '通义千问文生图模型' },
    { name: 'flux-merged', label: 'Flux Merged', description: 'Flux融合模型' },
    // 可以添加更多模型
  ];
  
  const [selectedModel, setSelectedModel] = useState('Qwen-Image-Max');
  const [prompt, setPrompt] = useState('');
  const [negativePrompt, setNegativePrompt] = useState('');
  const [size, setSize] = useState('1024*1024');
  const [imageCount, setImageCount] = useState(1);
  const [seed, setSeed] = useState('');
  const [loading, setLoading] = useState(false);
  const [generatedImages, setGeneratedImages] = useState([]);
  
  // 异步任务相关状态
  const [asyncTaskId, setAsyncTaskId] = useState(null);
  const [taskStatus, setTaskStatus] = useState('');
  const [taskProgress, setTaskProgress] = useState(0);
  const [isPolling, setIsPolling] = useState(false);
  
  // 图片预览相关状态
  const [showPreview, setShowPreview] = useState(false);
  const [previewImageUrl, setPreviewImageUrl] = useState('');
  
  // 下载进度相关状态
  const [showDownloadProgress, setShowDownloadProgress] = useState(false);
  const [downloadProgress, setDownloadProgress] = useState(0);

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!prompt.trim()) {
      warning('请输入提示词');
      return;
    }

    setLoading(true);
    try {
      // 使用异步API
      const params = {
        prompt: prompt.trim(),
        model: selectedModel,
        negativePrompt: negativePrompt.trim() || undefined,
        size,
        n: parseInt(imageCount),
        seed: seed ? parseInt(seed) : undefined
      };

      const response = await submitAsyncTask(params);
      
      if (response.code === 0 && response.data) {
        const taskId = response.data.taskId;
        setAsyncTaskId(taskId);
        setTaskStatus(response.data.status);
        setTaskProgress(response.data.progress || 0);
        
        // 开始轮询任务状态
        startTaskPolling(taskId);
        success('任务已提交，正在生成图像...');
      } else {
        error(response.message || '任务提交失败');
      }
    } catch (err) {
      error('请求失败: ' + err.message);
    } finally {
      setLoading(false);
    }
  };
  
  // 轮询任务状态
  const startTaskPolling = async (taskId) => {
    setIsPolling(true);
    
    const poll = async () => {
      try {
        const response = await getTaskStatus(taskId);
        
        if (response.code === 0 && response.data) {
          const taskData = response.data;
          setTaskStatus(taskData.status);
          setTaskProgress(taskData.progress || 0);
          
          if (taskData.status === 'SUCCESS') {
            setGeneratedImages(taskData.imageUrls || []);
            success('图像生成成功');
            setIsPolling(false);
            setAsyncTaskId(null);
          } else if (taskData.status === 'FAILED') {
            error(taskData.errorMessage || '图像生成失败');
            setIsPolling(false);
            setAsyncTaskId(null);
          } else {
            // 继续轮询
            setTimeout(poll, 2000); // 每2秒轮询一次
          }
        } else {
          error('获取任务状态失败');
          setIsPolling(false);
          setAsyncTaskId(null);
        }
      } catch (err) {
        error('获取任务状态失败: ' + err.message);
        setIsPolling(false);
        setAsyncTaskId(null);
      }
    };
    
    poll();
  };

  // 打开图片预览
  const openImagePreview = (imageUrl) => {
    setPreviewImageUrl(imageUrl);
    setShowPreview(true);
  };
  
  // 关闭图片预览
  const closeImagePreview = () => {
    setShowPreview(false);
    setPreviewImageUrl('');
  };
  
  // 处理图片下载
  const handleDownload = async (imageUrl, filename) => {
    try {
      setShowDownloadProgress(true);
      setDownloadProgress(0);
      
      // 创建一个隐藏的下载链接
      const link = document.createElement('a');
      link.href = imageUrl;
      link.download = filename;
      
      // 模拟下载进度
      const interval = setInterval(() => {
        setDownloadProgress(prev => {
          if (prev >= 95) {
            clearInterval(interval);
            return prev;
          }
          return prev + 5;
        });
      }, 200);
      
      // 点击链接开始下载
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      // 模拟下载完成
      setTimeout(() => {
        clearInterval(interval);
        setDownloadProgress(100);
        setTimeout(() => {
          setShowDownloadProgress(false);
          setDownloadProgress(0);
          success('图片下载完成');
        }, 500);
      }, 1000);
      
    } catch (err) {
      error('下载失败: ' + err.message);
      setShowDownloadProgress(false);
      setDownloadProgress(0);
    }
  };

  return (
    <div className="text-to-image-page">
      <div className="page-header">
        <h2>文生图功能</h2>
      </div>

      <div className="form-container">
        <form onSubmit={handleSubmit} className="text-to-image-form">
          <div className="form-row">
            <div className="form-group form-group-full">
              <label>选择模型</label>
              <select 
                value={selectedModel} 
                onChange={(e) => setSelectedModel(e.target.value)}
                className="select-input"
              >
                {supportedModels.map((model) => (
                  <option key={model.name} value={model.name}>
                    {model.label} - {model.description}
                  </option>
                ))}
              </select>
            </div>
          </div>
          
          <div className="form-group">
            <label>提示词 *</label>
            <textarea
              value={prompt}
              onChange={(e) => setPrompt(e.target.value)}
              placeholder="描述您想要生成的图像内容，例如：一只可爱的金毛犬在花园里玩耍"
              rows={4}
              required
            />
          </div>

          <div className="form-group">
            <label>反向提示词</label>
            <textarea
              value={negativePrompt}
              onChange={(e) => setNegativePrompt(e.target.value)}
              placeholder="描述您不希望在图像中出现的内容"
              rows={2}
            />
          </div>

          <div className="form-row">
            <div className="form-group">
              <label>图像尺寸</label>
              <select value={size} onChange={(e) => setSize(e.target.value)}>
                <option value="1024*1024">1024x1024</option>
                <option value="768*768">768x768</option>
                <option value="512*512">512x512</option>
                <option value="1440*768">1440x768</option>
                <option value="768*1440">768x1440</option>
              </select>
            </div>

            <div className="form-group">
              <label>生成数量</label>
              <select value={imageCount} onChange={(e) => setImageCount(e.target.value)}>
                {[1, 2, 3, 4].map(num => (
                  <option key={num} value={num}>{num}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label>随机种子</label>
              <input
                type="number"
                value={seed}
                onChange={(e) => setSeed(e.target.value)}
                placeholder="可选，用于生成确定性结果"
              />
            </div>
          </div>

          <div className="form-actions">
            <button type="submit" className="btn btn-primary" disabled={loading || isPolling}>
              {(loading || isPolling) ? '处理中...' : '生成图像'}
            </button>
            
            {/* 任务进度显示 */}
            {(asyncTaskId || isPolling) && (
              <div className="task-progress">
                <div className="progress-bar">
                  <div 
                    className="progress-fill" 
                    style={{ width: `${taskProgress}%` }}
                  >
                    {taskProgress}%
                  </div>
                </div>
                <div className="task-status">
                  任务状态: <span className={`status-${taskStatus.toLowerCase()}`}>{taskStatus}</span>
                </div>
              </div>
            )}
          </div>
        </form>
      </div>

      {generatedImages.length > 0 && (
        <div className="generated-images-section">
          <h3>生成的图像</h3>
          <div className="image-grid">
            {generatedImages.map((url, index) => (
              <div key={index} className="image-item">
                <img 
                  src={url} 
                  alt={`Generated ${index + 1}`} 
                  onClick={() => openImagePreview(url)}
                  style={{ cursor: 'pointer' }}
                />
                <div className="image-actions">
                  <button 
                    className="btn btn-sm btn-primary"
                    onClick={() => handleDownload(url, `generated_image_${index + 1}.png`)}
                  >
                    下载
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}
      
      {/* 图片预览弹窗 */}
      {showPreview && (
        <div className="image-preview-overlay" onClick={closeImagePreview}>
          <div className="image-preview-content" onClick={(e) => e.stopPropagation()}>
            <button className="preview-close-btn" onClick={closeImagePreview}>×</button>
            <img src={previewImageUrl} alt="Preview" className="preview-image" />
            <div className="preview-actions">
              <button 
                className="btn btn-primary"
                onClick={() => handleDownload(previewImageUrl, 'preview_image.png')}
              >
                下载图片
              </button>
            </div>
          </div>
        </div>
      )}
      
      {/* 下载进度弹窗 */}
      {showDownloadProgress && (
        <div className="download-progress-overlay">
          <div className="download-progress-content">
            <h3>下载进度</h3>
            <div className="progress-bar">
              <div 
                className="progress-fill" 
                style={{ width: `${downloadProgress}%` }}
              >
                {downloadProgress}%
              </div>
            </div>
            <p>正在下载图片...</p>
          </div>
        </div>
      )}
    </div>
  );
};

export default TextToImagePage;