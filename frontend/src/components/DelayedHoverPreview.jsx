import React, { useState, useRef } from 'react';
import { Popover, Image } from 'antd';

/**
 * 延迟悬停预览组件
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.children 触发悬停的子元素
 * @param {string} props.type 预览类型：'text' | 'image'
 * @param {string|Object} props.content 预览内容 (文本或图片对象)
 * @param {number} props.delay 延迟时间 (毫秒)，默认 5000
 */
const DelayedHoverPreview = ({ children, type = 'text', content, delay = 5000 }) => {
  const [visible, setVisible] = useState(false);
  const timerRef = useRef(null);

  const handleMouseEnter = () => {
    // 开启计时器
    timerRef.current = setTimeout(() => {
      setVisible(true);
    }, delay);
  };

  const handleMouseLeave = () => {
    // 清除计时器并隐藏
    if (timerRef.current) {
      clearTimeout(timerRef.current);
      timerRef.current = null;
    }
    setVisible(false);
  };

  const renderContent = () => {
    if (type === 'image') {
      return (
        <div style={{ maxWidth: 500 }}>
          <Image
            src={content}
            preview={false}
            style={{ width: '100%', borderRadius: 8 }}
          />
        </div>
      );
    }
    return (
      <div style={{ maxWidth: 400, whiteSpace: 'pre-wrap', wordBreak: 'break-all' }}>
        {content}
      </div>
    );
  };

  return (
    <Popover
      content={renderContent()}
      open={visible}
      trigger="hover"
      // 这里的 trigger 虽然是 hover，但我们通过 open 受控实现 5 秒延迟
      // 实际上我们不让 antd 自动控制显示
      mouseEnterDelay={delay / 1000} // Ant Design 也有内置延迟，可以直接用这个更简单
      placement="rightTop"
    >
      <div 
        onMouseEnter={handleMouseEnter} 
        onMouseLeave={handleMouseLeave}
        style={{ display: 'inline-block', width: '100%' }}
      >
        {children}
      </div>
    </Popover>
  );
};

export default DelayedHoverPreview;
