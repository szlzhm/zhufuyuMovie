import React, { useState, useRef, useEffect } from 'react';
import '../styles/FloatingPlayer.css';

/**
 * 悬浮播放器组件
 * 支持音频和视频播放,可拖拽
 * @param {boolean} show - 是否显示
 * @param {string} src - 媒体源地址
 * @param {string} type - 类型: audio | video
 * @param {string} title - 标题
 * @param {function} onClose - 关闭回调
 */
export default function FloatingPlayer({ show, src, type = 'audio', title = '', onClose }) {
  const [position, setPosition] = useState({ x: 100, y: 100 });
  const [isDragging, setIsDragging] = useState(false);
  const [dragStart, setDragStart] = useState({ x: 0, y: 0 });
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  
  const playerRef = useRef(null);
  const mediaRef = useRef(null);

  // 重置播放器状态
  useEffect(() => {
    if (show && mediaRef.current) {
      setIsPlaying(false);
      setCurrentTime(0);
      setDuration(0);
      mediaRef.current.currentTime = 0;
    }
  }, [show, src]);

  // 处理鼠标按下(开始拖拽)
  const handleMouseDown = (e) => {
    // 只在标题栏区域拖拽
    if (e.target.classList.contains('player-header') || 
        e.target.classList.contains('player-title')) {
      setIsDragging(true);
      setDragStart({
        x: e.clientX - position.x,
        y: e.clientY - position.y
      });
    }
  };

  // 处理鼠标移动(拖拽中)
  const handleMouseMove = (e) => {
    if (isDragging) {
      const newX = e.clientX - dragStart.x;
      const newY = e.clientY - dragStart.y;
      
      // 限制在视口内
      const maxX = window.innerWidth - (playerRef.current?.offsetWidth || 400);
      const maxY = window.innerHeight - (playerRef.current?.offsetHeight || 300);
      
      setPosition({
        x: Math.max(0, Math.min(newX, maxX)),
        y: Math.max(0, Math.min(newY, maxY))
      });
    }
  };

  // 处理鼠标释放(结束拖拽)
  const handleMouseUp = () => {
    setIsDragging(false);
  };

  // 监听全局鼠标事件
  useEffect(() => {
    if (isDragging) {
      document.addEventListener('mousemove', handleMouseMove);
      document.addEventListener('mouseup', handleMouseUp);
      return () => {
        document.removeEventListener('mousemove', handleMouseMove);
        document.removeEventListener('mouseup', handleMouseUp);
      };
    }
  }, [isDragging, dragStart, position]);

  // 播放/暂停
  const togglePlay = () => {
    if (mediaRef.current) {
      if (isPlaying) {
        mediaRef.current.pause();
      } else {
        mediaRef.current.play();
      }
      setIsPlaying(!isPlaying);
    }
  };

  // 更新播放进度
  const handleTimeUpdate = () => {
    if (mediaRef.current) {
      setCurrentTime(mediaRef.current.currentTime);
    }
  };

  // 加载元数据
  const handleLoadedMetadata = () => {
    if (mediaRef.current) {
      setDuration(mediaRef.current.duration);
    }
  };

  // 播放结束
  const handleEnded = () => {
    setIsPlaying(false);
    setCurrentTime(0);
  };

  // 拖动进度条
  const handleProgressChange = (e) => {
    const newTime = parseFloat(e.target.value);
    if (mediaRef.current) {
      mediaRef.current.currentTime = newTime;
      setCurrentTime(newTime);
    }
  };

  // 格式化时间
  const formatTime = (seconds) => {
    if (isNaN(seconds)) return '0:00';
    const mins = Math.floor(seconds / 60);
    const secs = Math.floor(seconds % 60);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  if (!show) return null;

  return (
    <div
      ref={playerRef}
      className={`floating-player ${isDragging ? 'dragging' : ''}`}
      style={{
        left: `${position.x}px`,
        top: `${position.y}px`
      }}
    >
      <div className="player-header" onMouseDown={handleMouseDown}>
        <span className="player-title">{title || '播放器'}</span>
        <button className="player-close" onClick={onClose}>×</button>
      </div>

      <div className="player-body">
        {type === 'audio' ? (
          <audio
            ref={mediaRef}
            src={src}
            onTimeUpdate={handleTimeUpdate}
            onLoadedMetadata={handleLoadedMetadata}
            onEnded={handleEnded}
          />
        ) : (
          <video
            ref={mediaRef}
            src={src}
            onTimeUpdate={handleTimeUpdate}
            onLoadedMetadata={handleLoadedMetadata}
            onEnded={handleEnded}
            className="player-video"
          />
        )}

        {type === 'audio' && (
          <div className="audio-visualizer">
            <div className="visualizer-icon">🎵</div>
            <div className="visualizer-text">正在播放音频</div>
          </div>
        )}

        <div className="player-controls">
          <button className="control-btn play-btn" onClick={togglePlay}>
            {isPlaying ? '⏸' : '▶'}
          </button>

          <div className="progress-container">
            <span className="time-label">{formatTime(currentTime)}</span>
            <input
              type="range"
              className="progress-bar"
              min="0"
              max={duration || 0}
              value={currentTime}
              onChange={handleProgressChange}
            />
            <span className="time-label">{formatTime(duration)}</span>
          </div>
        </div>
      </div>

      <div className="player-hint">可拖拽标题栏移动</div>
    </div>
  );
}
