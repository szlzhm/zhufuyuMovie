import React, { useState, useRef, useEffect } from 'react';
import { useMessage, useConfirm } from '../components/Dialog';
import { generateImage } from '../services/textToImage';
import { createConversation, addConversationDetail, getConversationList, getRecentConversationDetails } from '../services/chatbot';
import { Select, Button, Dropdown, Space } from 'antd';
import { HistoryOutlined, CaretRightOutlined } from '@ant-design/icons';

const ChatBotPage = () => {
  const { success, error, warning } = useMessage();
  const { confirm } = useConfirm();
  const [messages, setMessages] = useState([]);
  const [inputText, setInputText] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [showAdvancedParams, setShowAdvancedParams] = useState(false);
  const [imageGenerationParams, setImageGenerationParams] = useState({
    model: 'Qwen-Image-Max',
    size: '1024*1024',
    n: 1,
    seed: '',
    smartExpansion: true,
    customParams: {}
  });
  
  // 新增状态用于处理动画和计时
  const [requestStartTime, setRequestStartTime] = useState(null);
  
  // 新增状态用于对话历史
  const [conversations, setConversations] = useState([]);
  const [currentConversationId, setCurrentConversationId] = useState(null);
  const [currentConversationName, setCurrentConversationName] = useState('');
  const [showHistoryDropdown, setShowHistoryDropdown] = useState(false);
  
  // 新增状态用于分页加载
  const [currentPage, setCurrentPage] = useState(1);
  const [hasMore, setHasMore] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  
  const messagesEndRef = useRef(null);
  const chatContainerRef = useRef(null);

  // 滚动到底部
  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  // 计算耗时
  const getElapsedTime = () => {
    if (!requestStartTime) return '';
    const elapsed = Math.floor((Date.now() - requestStartTime) / 1000);
    const minutes = Math.floor(elapsed / 60);
    const seconds = elapsed % 60;
    
    if (minutes > 0) {
      return `${minutes}分${seconds}秒`;
    }
    return `${seconds}秒`;
  };

  // 加载对话历史
  const loadConversations = async () => {
    try {
      const response = await getConversationList({
        pageNo: 1,
        pageSize: 20
      });
      if (response.code === 0 && response.data) {
        setConversations(response.data.list || []);
        setCurrentPage(1);
        
        // 如果返回的数据少于pageSize，说明没有更多数据了
        if (response.data.list && response.data.list.length < 20) {
          setHasMore(false);
        } else {
          setHasMore(true);
        }
      }
    } catch (err) {
      console.error('加载对话历史失败:', err);
    }
  };

  // 创建新对话
  const createNewConversation = async (firstMessage) => {
    try {
      const firstMessageText = firstMessage.trim().substring(0, 10) || '新对话';
      const response = await createConversation({ conversationName: firstMessageText });
      if (response.code === 0 && response.data) {
        const newConversationId = response.data;
        setCurrentConversationId(newConversationId);
        setCurrentConversationName(firstMessageText);
        return newConversationId;
      }
    } catch (err) {
      console.error('创建对话失败:', err);
      error('创建对话失败: ' + err.message);
    }
    return null;
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // 初始化对话历史
  useEffect(() => {
    loadConversations();
  }, []);

  // 自动保存功能（每30秒保存一次）
  useEffect(() => {
    const autoSaveInterval = setInterval(() => {
      if (currentConversationId && messages.length > 0) {
        // 实现自动保存逻辑，保存当前对话状态
        console.log('自动保存对话...');
        // 可以在这里实现将当前对话状态保存到后端的逻辑
        // 暂时留空，可根据需要实现具体逻辑
      }
    }, 30000); // 30秒

    return () => clearInterval(autoSaveInterval);
  }, [currentConversationId, messages]);

  // 发送消息
  const handleSendMessage = async () => {
    if (!inputText.trim()) {
      warning('请输入消息内容');
      return;
    }

    // 检查是否为重试命令
    if (inputText.trim() === '/retry' || inputText.trim() === '/重试') {
      // 查找最近的用户消息作为重试内容
      const lastUserMessage = [...messages].reverse().find(msg => msg.type === 'user');
      if (lastUserMessage) {
        // 重新发送最后的用户消息
        setInputText(lastUserMessage.content);
        setTimeout(() => {
          handleSendMessage();
        }, 100);
      } else {
        error('没有找到可重试的消息');
      }
      return;
    }

    // 如果当前没有对话，则创建新对话
    let conversationId = currentConversationId;
    if (!conversationId) {
      conversationId = await createNewConversation(inputText);
      if (!conversationId) {
        return; // 创建对话失败
      }
    }

    const userMessage = {
      id: Date.now(),
      type: 'user',
      content: inputText,
      timestamp: new Date()
    };

    // 添加用户消息到对话
    const newMessages = [...messages, userMessage];
    setMessages(newMessages);
    setInputText('');
    setIsLoading(true);
    setRequestStartTime(Date.now()); // 开始计时

    try {
      // 添加用户消息到数据库
      await addConversationDetail({
        conversationId: conversationId,
        role: 0, // 0-用户提问
        contentType: 'text',
        content: inputText
      });

      // 创建文生图请求参数
      const params = {
        prompt: inputText,
        model: imageGenerationParams.model,
        size: imageGenerationParams.size,
        n: parseInt(imageGenerationParams.n),
        seed: imageGenerationParams.seed ? parseInt(imageGenerationParams.seed) : undefined,
        negativePrompt: imageGenerationParams.smartExpansion ? 'low quality, blurry, worst quality' : undefined
      };

      // 调用同步API生成图像
      const response = await generateImage(params);
      
      if (response.code === 0 && response.data) {
        // 添加机器人消息（包含生成的图像）
        const botMessage = {
          id: Date.now() + 1,
          type: 'bot',
          content: '图像生成成功',
          contentType: 'image',
          images: response.data.imageUrls,
          timestamp: new Date()
        };
      
        setMessages(prev => [...prev, botMessage]);
        success('图像生成成功');
      
        // 添加机器人消息到数据库
        await addConversationDetail({
          conversationId: conversationId,
          role: 1, // 1-ChatBot回答
          contentType: 'image',
          content: response.data.imageUrls ? response.data.imageUrls[0] : '图像生成成功', // 存储实际的图片URL
          images: response.data.imageUrls
        });
      } else {
        error(response.message || '图像生成失败');
      }
    } catch (err) {
      error('请求失败: ' + err.message);
    } finally {
      setIsLoading(false);
      setRequestStartTime(null); // 停止计时
    }
  };

  // 处理参数变化
  const handleParamChange = (paramName, value) => {
    setImageGenerationParams(prev => ({
      ...prev,
      [paramName]: value
    }));
  };

  // 处理自定义参数变化
  const handleCustomParamChange = (key, value) => {
    setImageGenerationParams(prev => ({
      ...prev,
      customParams: {
        ...prev.customParams,
        [key]: value
      }
    }));
  };

  // 处理图片下载
  const handleDownload = (imageUrl, filename) => {
    try {
      const link = document.createElement('a');
      link.href = imageUrl;
      link.download = filename;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      success('图片下载完成');
    } catch (err) {
      error('下载失败: ' + err.message);
    }
  };

  // 清空当前对话
  const clearCurrentConversation = () => {
    if (window.confirm('确定要清空当前对话吗？')) {
      setMessages([]);
    }
  };

  // 切换对话
  const switchToConversation = async (conversation) => {
    try {
      const confirmed = await confirm({
        title: '切换对话',
        content: '您确认切换对话吗？',
        okText: '确认',
        cancelText: '取消',
      });
      
      if (confirmed) {
        // 保存当前未完成的对话（如果需要）
        if (currentConversationId && messages.length > 0) {
          // 可以添加保存当前对话的逻辑
        }
        
        // 加载选中的对话
        setCurrentConversationId(conversation.conversationId);
        setCurrentConversationName(conversation.conversationName);
        
        // 获取该对话的最近详情
        const response = await getRecentConversationDetails(conversation.conversationId, 100);
        if (response.code === 0 && response.data) {
          // 转换数据格式为页面使用的格式
          const convertedMessages = response.data.map((detail, index) => ({
            id: detail.id,
            type: detail.role === 0 ? 'user' : 'bot',
            content: detail.content,
            contentType: detail.contentType,
            timestamp: new Date(detail.occurredTime),
            // 根据内容类型设置相应的属性
            ...(detail.contentType === 'image' && { images: [detail.content] }),
            ...(detail.contentType === 'audio' && { 
              audioUrl: detail.content,
              fileName: detail.originalFilename || detail.relativePath || '音频文件'
            }),
            ...(detail.contentType === 'video' && { 
              videoUrl: detail.content,
              fileName: detail.originalFilename || detail.relativePath || '视频文件'
            }),
            ...(detail.contentType === 'file' && { 
              fileUrl: detail.content,
              fileName: detail.originalFilename || detail.relativePath || '文件'
            })
          }));
          setMessages(convertedMessages);
        }
      }
    } catch (err) {
      error('切换对话失败: ' + err.message);
    }
  };

  // 限制对话历史数量（保留最近50条）
  useEffect(() => {
    if (messages.length > 50) {
      setMessages(prev => prev.slice(-50)); // 保留最后50条消息
    }
  }, [messages]);

  // 加载更多对话历史
  const loadMoreConversations = async () => {
    if (loadingMore || !hasMore) return;
    
    setLoadingMore(true);
    try {
      const response = await getConversationList({
        pageNo: currentPage + 1,
        pageSize: 20
      });
      
      if (response.code === 0 && response.data) {
        const newConversations = response.data.list || [];
        if (newConversations.length > 0) {
          setConversations(prev => [...prev, ...newConversations]);
          setCurrentPage(prev => prev + 1);
          
          // 如果返回的数据少于pageSize，说明没有更多数据了
          if (newConversations.length < 20) {
            setHasMore(false);
          }
        } else {
          setHasMore(false);
        }
      }
    } catch (err) {
      console.error('加载更多对话历史失败:', err);
      error('加载更多对话历史失败: ' + err.message);
    } finally {
      setLoadingMore(false);
    }
  };

  return (
    <div className="chatbot-page">
      
      <div className="chatbot-header">
        <h2>ChatBot - 文生图助手</h2>
        <div className="header-actions">
          {/* 历史对话下拉菜单 */}
          <Dropdown
            menu={{
              items: [
                ...conversations.map((conv, index) => ({
                  key: conv.conversationId,
                  label: (
                    <div onClick={() => switchToConversation(conv)}>
                      <div>{conv.conversationName}</div>
                      <small style={{ color: '#999' }}>
                        {new Date(conv.createdTime).toLocaleString('zh-CN')}
                      </small>
                    </div>
                  ),
                })),
                ...(hasMore ? [{
                  key: 'load-more',
                  label: loadingMore ? '加载中...' : '加载更多',
                  onClick: loadMoreConversations,
                  disabled: loadingMore
                }] : [])
              ],
              onClick: ({ key }) => {
                if (key === 'load-more') {
                  loadMoreConversations();
                } else {
                  const conv = conversations.find(c => c.conversationId === key);
                  if (conv) switchToConversation(conv);
                }
              }
            }}
            trigger={['click']}
            onOpenChange={(open) => {
              if (open) {
                setShowHistoryDropdown(true);
              } else {
                setShowHistoryDropdown(false);
              }
            }}
          >
            <Button type="default" icon={<HistoryOutlined />}>历史对话</Button>
          </Dropdown>
          
          <Button className="btn btn-default" onClick={clearCurrentConversation}>
            清空当前
          </Button>
        </div>
      </div>
      
      <div className="chatbot-container">
        <div className="chatbot-sidebar">
          <div className="param-section">
            <h3>生成参数</h3>
            
            <div className="form-group">
              <label>模型名称</label>
              <select 
                value={imageGenerationParams.model} 
                onChange={(e) => handleParamChange('model', e.target.value)}
              >
                <option value="Qwen-Image-Max">Qwen-Image-Max</option>
                <option value="flux-merged">Flux Merged</option>
              </select>
            </div>
            
            <div className="form-group">
              <label>图像尺寸</label>
              <select 
                value={imageGenerationParams.size} 
                onChange={(e) => handleParamChange('size', e.target.value)}
              >
                <option value="1024*1024">1024x1024</option>
                <option value="768*768">768x768</option>
                <option value="512*512">512x512</option>
                <option value="1440*768">1440x768</option>
                <option value="768*1440">768x1440</option>
              </select>
            </div>
            
            <div className="form-group">
              <label>生成数量</label>
              <select 
                value={imageGenerationParams.n} 
                onChange={(e) => handleParamChange('n', e.target.value)}
              >
                {[1, 2, 3, 4].map(num => (
                  <option key={num} value={num}>{num}</option>
                ))}
              </select>
            </div>
            
            <div className="form-group">
              <label>随机种子</label>
              <input
                type="number"
                value={imageGenerationParams.seed}
                onChange={(e) => handleParamChange('seed', e.target.value)}
                placeholder="可选，用于生成确定性结果"
              />
            </div>
            
            <div className="form-group">
              <label>
                <input
                  type="checkbox"
                  checked={imageGenerationParams.smartExpansion}
                  onChange={(e) => handleParamChange('smartExpansion', e.target.checked)}
                />
                智能扩展（优化提示词）
              </label>
            </div>
            
            <div className="form-group">
              <label>
                <input
                  type="checkbox"
                  checked={showAdvancedParams}
                  onChange={(e) => setShowAdvancedParams(e.target.checked)}
                />
                自定义参数
              </label>
            </div>
            
            {showAdvancedParams && (
              <div className="advanced-params">
                <p>自定义参数（JSON格式）:</p>
                <textarea
                  value={JSON.stringify(imageGenerationParams.customParams, null, 2)}
                  onChange={(e) => {
                    try {
                      const obj = JSON.parse(e.target.value);
                      handleCustomParamChange(obj);
                    } catch (err) {
                      // 解析失败，暂时不更新
                    }
                  }}
                  rows={4}
                  placeholder="例如：{&quot;style&quot;: &quot;realistic&quot;, &quot;quality&quot;: &quot;high&quot;}"
                />
              </div>
            )}
          </div>
        </div>
        
        <div className="chatbot-main">
          <div className="chat-messages" ref={chatContainerRef}>
            {messages.length === 0 ? (
              <div className="empty-chat">
                <p>欢迎使用ChatBot！请输入您的图像生成需求，例如："画一只可爱的小猫"。</p>
              </div>
            ) : (
              messages.map((message) => (
                <div 
                  key={message.id} 
                  className={`message ${message.type === 'user' ? 'user-message' : 'bot-message'}`}
                >
                  <div className="message-content">
                    {message.content}
                  </div>
                  
                  {/* 根据内容类型展示不同类型的内容 */}
                  {message.contentType && (
                    <div className="message-content-type">
                      {message.contentType === 'text' && (
                        <div className="text-content">{message.content}</div>
                      )}
                      
                      {message.contentType === 'image' && message.images && message.images.length > 0 && (
                        <div className="generated-images">
                          {message.images.map((imgUrl, idx) => (
                            <div key={idx} className="image-item">
                              <img 
                                src={imgUrl} 
                                alt={`Generated ${idx + 1}`} 
                                className="generated-image"
                              />
                              <button 
                                className="btn btn-sm btn-primary download-btn"
                                onClick={() => handleDownload(imgUrl, `generated_image_${Date.now()}_${idx + 1}.png`)}
                              >
                                下载
                              </button>
                            </div>
                          ))}
                        </div>
                      )}
                      
                      {message.contentType === 'audio' && message.audioUrl && (
                        <div className="audio-content">
                          <audio controls src={message.audioUrl} className="audio-player">
                            您的浏览器不支持音频播放
                          </audio>
                          <div className="file-info">
                            {message.fileName || '音频文件'}
                            <button 
                              className="btn btn-sm btn-primary download-btn"
                              onClick={() => handleDownload(message.audioUrl, message.fileName || `audio_${Date.now()}.mp3`)}
                            >
                              下载
                            </button>
                          </div>
                        </div>
                      )}
                      
                      {message.contentType === 'video' && message.videoUrl && (
                        <div className="video-content">
                          <video controls src={message.videoUrl} className="video-player">
                            您的浏览器不支持视频播放
                          </video>
                          <div className="file-info">
                            {message.fileName || '视频文件'}
                            <button 
                              className="btn btn-sm btn-primary download-btn"
                              onClick={() => handleDownload(message.videoUrl, message.fileName || `video_${Date.now()}.mp4`)}
                            >
                              下载
                            </button>
                          </div>
                        </div>
                      )}
                      
                      {message.contentType === 'file' && message.fileUrl && (
                        <div className="file-content">
                          <div className="file-icon">📄</div>
                          <div className="file-info">
                            <a 
                              href={message.fileUrl}
                              onClick={(e) => {
                                e.preventDefault();
                                handleDownload(message.fileUrl, message.fileName || `file_${Date.now()}`);
                              }}
                            >
                              {message.fileName || '文件'}
                            </a>
                          </div>
                        </div>
                      )}
                    </div>
                  )}
                  
                  <div className="message-timestamp">
                    {message.timestamp.toLocaleTimeString()}
                  </div>
                </div>
              ))
            )}
            <div ref={messagesEndRef} />
          </div>
          
          <div className="chat-input-area">
            <div className="input-container">
              <textarea
                value={inputText}
                onChange={(e) => setInputText(e.target.value)}
                placeholder="输入您的图像生成需求..."
                rows={3}
                onKeyPress={(e) => {
                  if (e.key === 'Enter' && !e.shiftKey) {
                    e.preventDefault();
                    handleSendMessage();
                  }
                }}
                disabled={isLoading}
              />
              <div className="send-section">
                <button 
                  className="send-button btn btn-primary"
                  onClick={handleSendMessage}
                  disabled={isLoading || !inputText.trim()}
                >
                  {isLoading ? (
                    <span>
                      处理中... {getElapsedTime() && `(${getElapsedTime()})`}
                    </span>
                  ) : '发送'}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ChatBotPage;