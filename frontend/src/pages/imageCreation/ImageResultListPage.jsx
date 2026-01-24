import React, { useState, useEffect } from 'react';
import { Card, Input, DatePicker, Button, Space, List, Image, message, Checkbox, Modal, Progress } from 'antd';
import { DownloadOutlined, SaveOutlined } from '@ant-design/icons';
import { queryResults, saveTemplateFromResult } from '../../services/imageCreation.js';
import DelayedHoverPreview from '../../components/DelayedHoverPreview.jsx';
import SaveAsTemplateModal from '../../components/SaveAsTemplateModal.jsx';

const { RangePicker } = DatePicker;

/**
 * 创作结果列表页面
 */
export default function ImageResultListPage() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [queryParams, setQueryParams] = useState({
    pageNo: 1,
    pageSize: 20,
    prompt: '',
    promptStartTime: null,
    promptEndTime: null,
    completedStartTime: null,
    completedEndTime: null
  });

  const [selectedIds, setSelectedIds] = useState([]);
  const [downloading, setDownloading] = useState(false);
  const [downloadProgress, setDownloadProgress] = useState(0);
  
  // 新增：每个任务选中的图片索引
  const [selectedImageIndices, setSelectedImageIndices] = useState({});
  
  // 保存为模板相关状态
  const [saveModalVisible, setSaveModalVisible] = useState(false);
  const [currentSavingItem, setCurrentSavingItem] = useState(null);

  useEffect(() => {
    fetchData();
  }, [queryParams.pageNo, queryParams.pageSize]);

  const fetchData = async () => {
    setLoading(true);
    try {
      const params = { ...queryParams };
      if (params.promptStartTime) params.promptStartTime = params.promptStartTime.format('YYYY-MM-DD HH:mm:ss');
      if (params.promptEndTime) params.promptEndTime = params.promptEndTime.format('YYYY-MM-DD HH:mm:ss');
      if (params.completedStartTime) params.completedStartTime = params.completedStartTime.format('YYYY-MM-DD HH:mm:ss');
      if (params.completedEndTime) params.completedEndTime = params.completedEndTime.format('YYYY-MM-DD HH:mm:ss');

      const resp = await queryResults(params);
      if (resp.code === 0) {
        setData(resp.data.list);
        setTotal(resp.data.total);
      } else {
        message.error(resp.message || '获取数据失败');
      }
    } catch (error) {
      message.error('网络请求失败');
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = () => {
    if (queryParams.pageNo === 1) {
      fetchData();
    } else {
      setQueryParams({ ...queryParams, pageNo: 1 });
    }
  };

  const handleSelect = (id) => {
    const isCurrentlySelected = selectedIds.includes(id);
    
    if (isCurrentlySelected) {
      // 取消选中：移除任务ID，并清空该任务的图片选择
      setSelectedIds(prev => prev.filter(i => i !== id));
      setSelectedImageIndices(prev => {
        const newIndices = { ...prev };
        delete newIndices[id];
        return newIndices;
      });
    } else {
      // 选中：添加任务ID，并选中该任务的所有图片
      setSelectedIds(prev => [...prev, id]);
      
      // 找到该任务并选中所有图片
      const task = data.find(item => item.id === id);
      if (task && task.imageUrls && task.imageUrls.length > 0) {
        const allIndices = task.imageUrls.map((_, idx) => idx);
        setSelectedImageIndices(prev => ({
          ...prev,
          [id]: allIndices
        }));
      }
    }
  };

  const handleSelectAll = (e) => {
    if (e.target.checked) {
      // 选中所有任务
      const allIds = data.map(item => item.id);
      setSelectedIds(allIds);
      
      // 选中所有任务的所有图片
      const allImageIndices = {};
      data.forEach(item => {
        if (item.imageUrls && item.imageUrls.length > 0) {
          allImageIndices[item.id] = item.imageUrls.map((_, idx) => idx);
        }
      });
      setSelectedImageIndices(allImageIndices);
    } else {
      // 取消选中所有任务和所有图片
      setSelectedIds([]);
      setSelectedImageIndices({});
    }
  };
  
  // 切换某个任务的某张图片选中状态
  const toggleImageSelection = (taskId, imageIndex) => {
    setSelectedImageIndices(prev => {
      const taskSelections = prev[taskId] || [];
      const newSelections = taskSelections.includes(imageIndex)
        ? taskSelections.filter(idx => idx !== imageIndex)
        : [...taskSelections, imageIndex];
      
      return {
        ...prev,
        [taskId]: newSelections
      };
    });
  };
  
  // 从URL中提取文件名
  const getFileNameFromUrl = (url) => {
    try {
      // 从URL中提取路径部分
      const urlPath = url.split('?')[0]; // 去掉查询参数
      const segments = urlPath.split('/');
      return segments[segments.length - 1]; // 获取最后一个片段（文件名）
    } catch (err) {
      return 'download.png'; // 默认文件名
    }
  };
  
  // 下载单张图片
  const downloadSingleImage = async (imageUrl, taskId, imageIndex) => {
    try {
      const response = await fetch(imageUrl);
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = getFileNameFromUrl(imageUrl);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      message.success('下载成功');
    } catch (err) {
      console.error('Download failed', err);
      message.error('下载失败');
    }
  };
  
  // 下载任务的选中图片
  const downloadTaskSelectedImages = async (item) => {
    const selectedIndices = selectedImageIndices[item.id] || [];
    
    if (selectedIndices.length === 0) {
      message.warning('请先选择要下载的图片');
      return;
    }
    
    setDownloading(true);
    setDownloadProgress(0);
    
    for (let i = 0; i < selectedIndices.length; i++) {
      const idx = selectedIndices[i];
      const imageUrl = item.imageUrls[idx];
      
      try {
        const response = await fetch(imageUrl);
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = getFileNameFromUrl(imageUrl);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
      } catch (err) {
        console.error('Download failed', err);
      }
      
      setDownloadProgress(Math.round(((i + 1) / selectedIndices.length) * 100));
      await new Promise(resolve => setTimeout(resolve, 300));
    }
    
    setTimeout(() => {
      setDownloading(false);
      message.success('下载完成');
    }, 500);
  };

  const batchDownload = async () => {
    if (selectedIds.length === 0) {
      message.warning('请选择要下载的图片');
      return;
    }

    setDownloading(true);
    setDownloadProgress(0);

    const selectedItems = data.filter(item => selectedIds.includes(item.id));
    
    // 计算总图片数用于进度显示
    const totalImages = selectedItems.reduce((acc, item) => acc + (item.imageUrls?.length || 0), 0);
    let downloadedCount = 0;

    for (let i = 0; i < selectedItems.length; i++) {
      const item = selectedItems[i];
      const urls = item.imageUrls || [];
      
      for (let j = 0; j < urls.length; j++) {
        const imageUrl = urls[j];
        try {
          const response = await fetch(imageUrl);
          const blob = await response.blob();
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = getFileNameFromUrl(imageUrl);
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
        } catch (err) {
          console.error('Download failed', err);
        }
        
        downloadedCount++;
        setDownloadProgress(Math.round((downloadedCount / totalImages) * 100));
        await new Promise(resolve => setTimeout(resolve, 300));
      }
    }

    setTimeout(() => {
      setDownloading(false);
      message.success('下载任务提交完成');
    }, 500);
  };

  // 打开保存为模板的模态窗口
  const handleSaveAsTemplate = (item) => {
    setCurrentSavingItem(item);
    setSaveModalVisible(true);
  };

  // 保存模板
  const handleSaveTemplate = async (formValues) => {
    try {
      const saveData = {
        resultId: currentSavingItem.id,
        templateContent: formValues.templateContent,
        placeholderKeywords: formValues.placeholderKeywords,
        templateStatus: formValues.templateStatus || 1,
        parameters: formValues.parameters,
        firstImageUrl: currentSavingItem.imageUrls[0] // 第一张图片URL
      };
      
      const resp = await saveTemplateFromResult(saveData);
      if (resp.code === 0) {
        message.success('保存为模板成功');
        setSaveModalVisible(false);
        setCurrentSavingItem(null);
      } else {
        message.error(resp.message || '保存失败');
      }
    } catch (error) {
      console.error('Save template error:', error);
      message.error('保存失败');
    }
  };

  return (
    <div className="page-container" style={{ maxWidth: '1400px', margin: '0 auto' }}>
      <div className="page-header">
        <h2>创作结果列表</h2>
        <Space>
          <Checkbox 
            checked={selectedIds.length === data.length && data.length > 0} 
            onChange={handleSelectAll}
          >
            全选当前页
          </Checkbox>
          <Button type="primary" onClick={batchDownload} disabled={selectedIds.length === 0}>
            批量下载 ({selectedIds.length})
          </Button>
        </Space>
      </div>

      <Card bordered={false} style={{ marginBottom: 16 }}>
        <Space wrap>
          <Input 
            placeholder="提示语" 
            style={{ width: 180 }} 
            value={queryParams.prompt}
            onChange={(e) => setQueryParams({ ...queryParams, prompt: e.target.value })}
          />
          <div>
            <span style={{ marginRight: 8 }}>提交时间:</span>
            <RangePicker 
              showTime 
              onChange={(dates) => setQueryParams({ 
                ...queryParams, 
                promptStartTime: dates ? dates[0] : null, 
                promptEndTime: dates ? dates[1] : null 
              })} 
            />
          </div>
          <div>
            <span style={{ marginRight: 8 }}>完成时间:</span>
            <RangePicker 
              showTime 
              onChange={(dates) => setQueryParams({ 
                ...queryParams, 
                completedStartTime: dates ? dates[0] : null, 
                completedEndTime: dates ? dates[1] : null 
              })} 
            />
          </div>
          <Button type="primary" onClick={handleSearch}>查询</Button>
          <Button onClick={() => setQueryParams({ 
            pageNo: 1, 
            pageSize: 20, 
            prompt: '', 
            promptStartTime: null, 
            promptEndTime: null, 
            completedStartTime: null, 
            completedEndTime: null 
          })}>重置</Button>
        </Space>
      </Card>

      <List
        dataSource={data}
        loading={loading}
        pagination={{
          current: queryParams.pageNo,
          pageSize: queryParams.pageSize,
          total: total,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, size) => setQueryParams({ ...queryParams, pageNo: page, pageSize: size }),
        }}
        renderItem={(item) => (
          <List.Item style={{ marginBottom: 24 }}>
            <Card
              style={{ width: '100%' }}
              bodyStyle={{ padding: '16px' }}
            >
              {/* 任务信息头部 */}
              <div style={{ marginBottom: 16, borderBottom: '1px solid #f0f0f0', paddingBottom: 12 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <div style={{ flex: 1 }}>
                    <DelayedHoverPreview content={item.promptContent}>
                      <div style={{ fontSize: '14px', fontWeight: 500, marginBottom: 8, color: '#333' }}>
                        {item.promptContent}
                      </div>
                    </DelayedHoverPreview>
                    <Space size={16} style={{ fontSize: '12px', color: '#888' }}>
                      <span>任务ID: {item.taskId}</span>
                      <span>耗时: {item.generationTime ? `${item.generationTime}s` : '-'}</span>
                      <span>{item.completedTime}</span>
                    </Space>
                  </div>
                </div>
              </div>

              {/* 图片网格 */}
              <div style={{ 
                display: 'grid', 
                gridTemplateColumns: 'repeat(auto-fill, minmax(200px, 1fr))',
                gap: '16px'
              }}>
                {(item.imageUrls || []).map((url, idx) => (
                  <div key={idx} style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                    <Image.PreviewGroup>
                      <Image
                        src={url}
                        alt={`result-${idx}`}
                        style={{ 
                          width: '100%', 
                          height: 200, 
                          objectFit: 'cover',
                          borderRadius: '4px'
                        }}
                      />
                    </Image.PreviewGroup>
                    
                    {/* 操作按钮区域 - 放在图片下方 */}
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      {/* 多图任务才显示复选框 */}
                      {item.imageUrls.length > 1 ? (
                        <Checkbox
                          checked={(selectedImageIndices[item.id] || []).includes(idx)}
                          onChange={() => toggleImageSelection(item.id, idx)}
                        >
                          选择
                        </Checkbox>
                      ) : (
                        <div></div>
                      )}
                      
                      {/* 下载按钮 */}
                      <Button
                        type="primary"
                        size="small"
                        icon={<DownloadOutlined />}
                        onClick={() => downloadSingleImage(url, item.taskId, idx)}
                      >
                        下载
                      </Button>
                    </div>
                  </div>
                ))}
              </div>

              {/* 任务操作区域 - 放在图片下方 */}
              <div style={{ marginTop: 16, display: 'flex', justifyContent: 'center', gap: '12px', alignItems: 'center' }}>
                {item.imageUrls && item.imageUrls.length > 1 && (
                  <>
                    <Button
                      type="primary"
                      icon={<DownloadOutlined />}
                      onClick={() => downloadTaskSelectedImages(item)}
                      disabled={(selectedImageIndices[item.id] || []).length === 0}
                    >
                      下载选中图片 ({(selectedImageIndices[item.id] || []).length})
                    </Button>
                    <Checkbox 
                      checked={selectedIds.includes(item.id)}
                      onChange={() => handleSelect(item.id)}
                    >
                      选择任务的全部图片
                    </Checkbox>
                  </>
                )}
                <Button
                  icon={<SaveOutlined />}
                  onClick={() => handleSaveAsTemplate(item)}
                >
                  存为模板
                </Button>
              </div>
            </Card>
          </List.Item>
        )}
      />

      <Modal
        title="正在处理下载"
        open={downloading}
        footer={null}
        closable={false}
        maskClosable={false}
      >
        <div style={{ textAlign: 'center', padding: '20px 0' }}>
          <Progress type="circle" percent={downloadProgress} />
          <div style={{ marginTop: 16 }}>正在处理下载队列，请稍候...</div>
        </div>
      </Modal>

      {/* 保存为模板模态窗口 */}
      <SaveAsTemplateModal
        visible={saveModalVisible}
        onCancel={() => {
          setSaveModalVisible(false);
          setCurrentSavingItem(null);
        }}
        onSave={handleSaveTemplate}
        initialData={currentSavingItem ? {
          templateContent: currentSavingItem.promptContent || '',
          placeholderKeywords: currentSavingItem.placeholderKeywords || '',
          parameters: {
            resolution: currentSavingItem.resolution || '912*1216',
            numImages: currentSavingItem.numImages || 1,
            seed: currentSavingItem.seed || -1,
            smartOptimization: currentSavingItem.smartOptimization === 1 || currentSavingItem.smartOptimization === true,
            inferenceSteps: currentSavingItem.inferenceSteps || 4,
            cfgScale: currentSavingItem.cfgScale || 1.0,
            negativePrompt: currentSavingItem.negativePrompt || '',
            enableCustomParams: currentSavingItem.enableCustomParams === 1 || currentSavingItem.enableCustomParams === true,
            customParams: currentSavingItem.customParams || {}
          }
        } : null}
      />
    </div>
  );
}
