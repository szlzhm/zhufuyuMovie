import React, { useState } from 'react';
import { Modal, message } from 'antd';
import { ExclamationCircleOutlined, CheckCircleOutlined, InfoCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';

const Dialog = () => {
  const [modal, setModal] = useState({
    open: false,
    type: 'info', // 'success', 'error', 'warning', 'info'
    title: '',
    content: '',
    onOk: null,
    onCancel: null,
  });

  const open = (type, title, content, onOk, onCancel) => {
    setModal({
      open: true,
      type,
      title,
      content,
      onOk,
      onCancel,
    });
  };

  const handleOk = async () => {
    if (modal.onOk) {
      try {
        await modal.onOk();
      } catch (error) {
        console.error('Dialog callback error:', error);
      }
    }
    setModal(prev => ({ ...prev, open: false }));
  };

  const handleCancel = () => {
    if (modal.onCancel) {
      try {
        modal.onCancel();
      } catch (error) {
        console.error('Dialog onCancel error:', error);
      }
    }
    setModal(prev => ({ ...prev, open: false }));
  };

  const getIcon = (type) => {
    switch (type) {
      case 'success':
        return <CheckCircleOutlined style={{ color: '#52c41a', fontSize: '24px' }} />;
      case 'error':
        return <CloseCircleOutlined style={{ color: '#ff4d4f', fontSize: '24px' }} />;
      case 'warning':
        return <ExclamationCircleOutlined style={{ color: '#faad14', fontSize: '24px' }} />;
      default:
        return <InfoCircleOutlined style={{ color: '#1890ff', fontSize: '24px' }} />;
    }
  };

  const getOkButtonProps = (type) => {
    switch (type) {
      case 'error':
        return { danger: true };
      case 'warning':
        return { type: 'primary', style: { backgroundColor: '#faad14', borderColor: '#faad14' } };
      default:
        return { type: 'primary' };
    }
  };

  return (
    <>
      <Modal
        open={modal.open}
        title={modal.title}
        onCancel={handleCancel}
        footer={[
          <button key="cancel" onClick={handleCancel} className="btn btn-default">
            取消
          </button>,
          <button key="ok" {...getOkButtonProps(modal.type)} onClick={handleOk} className="btn btn-primary">
            确定
          </button>,
        ]}
      >
        <div style={{ display: 'flex', alignItems: 'flex-start' }}>
          <div style={{ marginRight: '12px', marginTop: '2px' }}>
            {getIcon(modal.type)}
          </div>
          <div>{modal.content}</div>
        </div>
      </Modal>
    </>
  );
};

// Hook for using the dialog - 使用Ant Design的message和Modal组件
export const useMessage = () => {
  const success = (content, title = '成功') => {
    return new Promise((resolve) => {
      Modal.success({
        title,
        content,
        onOk: () => resolve(true),
      });
    });
  };

  const error = (content, title = '错误') => {
    return new Promise((resolve) => {
      Modal.error({
        title,
        content,
        onOk: () => resolve(true),
      });
    });
  };

  const warning = (content, title = '警告') => {
    return new Promise((resolve) => {
      Modal.warning({
        title,
        content,
        onOk: () => resolve(true),
      });
    });
  };

  const info = (content, title = '提示') => {
    return new Promise((resolve) => {
      Modal.info({
        title,
        content,
        onOk: () => resolve(true),
      });
    });
  };

  const confirm = (config) => {
    return new Promise((resolve) => {
      Modal.confirm({
        title: config.title,
        content: config.content,
        okText: config.okText || '确定',
        cancelText: config.cancelText || '取消',
        onOk: () => resolve(true),
        onCancel: () => resolve(false),
      });
    });
  };

  // 不需要MessageComponent，因为使用Ant Design的Modal组件
  return {
    success,
    error,
    warning,
    info,
    confirm,
  };
};

// 添加 useConfirm 导出以兼容现有代码
export const useConfirm = () => {
  const confirm = (config) => {
    return new Promise((resolve) => {
      Modal.confirm({
        title: config.title,
        content: config.content,
        okText: config.okText || '确定',
        cancelText: config.cancelText || '取消',
        onOk: () => resolve(true),
        onCancel: () => resolve(false),
      });
    });
  };

  return { confirm };
};

export default Dialog;