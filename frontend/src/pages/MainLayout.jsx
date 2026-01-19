import React, { useEffect, useState } from 'react';
import { useNavigate, Outlet, useLocation } from 'react-router-dom';
import { getCurrentUser } from '../services/user.js';

export default function MainLayout() {
  const navigate = useNavigate();
  const location = useLocation();
  const [currentUser, setCurrentUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [activeMenu, setActiveMenu] = useState('home');
  const [showSystemConfig, setShowSystemConfig] = useState(false);
  const [showMaterialManage, setShowMaterialManage] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem('authToken');
    if (!token) {
      navigate('/login');
      return;
    }

    getCurrentUser()
      .then((resp) => {
        if (resp.code === 0 && resp.data) {
          setCurrentUser(resp.data);
        } else {
          localStorage.removeItem('authToken');
          navigate('/login');
        }
      })
      .catch(() => {
        localStorage.removeItem('authToken');
        navigate('/login');
      })
      .finally(() => {
        setLoading(false);
      });
  }, [navigate]);

  useEffect(() => {
    if (location.pathname === '/' || location.pathname === '') {
      setActiveMenu('home');
    } else if (location.pathname.includes('user-manage')) {
      setActiveMenu('user-manage');
    } else if (location.pathname.includes('emotion-manage')) {
      setActiveMenu('emotion-manage');
      setShowSystemConfig(true);
    } else if (location.pathname.includes('file-config')) {
      setActiveMenu('file-config');
      setShowSystemConfig(true);
    } else if (location.pathname.includes('image-category')) {
      setActiveMenu('image-category');
      setShowSystemConfig(true);
    } else if (location.pathname.includes('text-category')) {
      setActiveMenu('text-category');
      setShowSystemConfig(true);
    } else if (location.pathname.includes('image-material')) {
      setActiveMenu('image-material');
      setShowMaterialManage(true);
    } else if (location.pathname.includes('text-material')) {
      setActiveMenu('text-material');
      setShowMaterialManage(true);
    } else if (location.pathname.includes('voice-material')) {
      setActiveMenu('voice-material');
      setShowMaterialManage(true);
    } else if (location.pathname.includes('music-material')) {
      setActiveMenu('music-material');
      setShowMaterialManage(true);
    } else if (location.pathname.includes('bless-video')) {
      setActiveMenu('bless-video');
    } else if (location.pathname.includes('text-to-image')) {
      setActiveMenu('text-to-image');
      setShowMaterialManage(true);
    } else if (location.pathname.includes('chatbot')) {
      setActiveMenu('chatbot');
    }
  }, [location]);

  const handleMenuClick = (menu, path) => {
    setActiveMenu(menu);
    navigate(path);
  };

  const handleLogout = () => {
    localStorage.removeItem('authToken');
    navigate('/login');
  };

  if (loading) {
    return <div className="loading">加载中...</div>;
  }

  return (
    <div className="layout-root">
      <aside className="layout-sider">
        <div className="logo">祝福语后台</div>
        <nav className="menu">
          <div 
            className={`menu-item ${activeMenu === 'home' ? 'active' : ''}`}
            onClick={() => handleMenuClick('home', '/')}
          >
            首页
          </div>
          <div className="menu-item" onClick={() => setShowMaterialManage(!showMaterialManage)}>
            素材管理 {showMaterialManage ? '▼' : '▶'}
          </div>
          {showMaterialManage && (
            <div className="submenu">
              <div 
                className={`menu-item ${activeMenu === 'image-material' ? 'active' : ''}`}
                onClick={() => handleMenuClick('image-material', '/image-material')}
              >
                图片素材管理
              </div>
              <div 
                className={`menu-item ${activeMenu === 'text-material' ? 'active' : ''}`}
                onClick={() => handleMenuClick('text-material', '/text-material')}
              >
                文案素材管理
              </div>
              <div 
                className={`menu-item ${activeMenu === 'voice-material' ? 'active' : ''}`}
                onClick={() => handleMenuClick('voice-material', '/voice-material')}
              >
                音色素材管理
              </div>
              <div 
                className={`menu-item ${activeMenu === 'music-material' ? 'active' : ''}`}
                onClick={() => handleMenuClick('music-material', '/music-material')}
              >
                背景音乐管理
              </div>
              <div 
                className={`menu-item ${activeMenu === 'text-to-image' ? 'active' : ''}`}
                onClick={() => handleMenuClick('text-to-image', '/text-to-image')}
              >
                文生图功能
              </div>
            </div>
          )}
          <div 
            className={`menu-item ${activeMenu === 'chatbot' ? 'active' : ''}`}
            onClick={() => handleMenuClick('chatbot', '/chatbot')}
          >
            ChatBot
          </div>
          <div 
            className={`menu-item ${activeMenu === 'bless-video' ? 'active' : ''}`}
            onClick={() => handleMenuClick('bless-video', '/bless-video')}
          >
            祝福语视频管理
          </div>
          <div 
            className={`menu-item ${activeMenu === 'video-task' ? 'active' : ''}`}
            onClick={() => handleMenuClick('video-task', '/video-task')}
          >
            创作祝福语视频
          </div>
          {currentUser?.role === 'ADMIN' && (
            <div 
              className={`menu-item ${activeMenu === 'user-manage' ? 'active' : ''}`}
              onClick={() => handleMenuClick('user-manage', '/user-manage')}
            >
              系统管理
            </div>
          )}
          {currentUser?.role === 'ADMIN' && (
            <>
              <div 
                className="menu-item"
                onClick={() => setShowSystemConfig(!showSystemConfig)}
              >
                系统配置 {showSystemConfig ? '▼' : '▶'}
              </div>
              {showSystemConfig && (
                <div className="submenu">
                  <div 
                    className={`menu-item ${activeMenu === 'emotion-manage' ? 'active' : ''}`}
                    onClick={() => handleMenuClick('emotion-manage', '/emotion-manage')}
                  >
                    情绪管理
                  </div>
                  <div 
                    className={`menu-item ${activeMenu === 'file-config' ? 'active' : ''}`}
                    onClick={() => handleMenuClick('file-config', '/file-config')}
                  >
                    文件存储配置
                  </div>
                  <div 
                    className={`menu-item ${activeMenu === 'image-category' ? 'active' : ''}`}
                    onClick={() => handleMenuClick('image-category', '/image-category')}
                  >
                    图片分类管理
                  </div>
                  <div 
                    className={`menu-item ${activeMenu === 'text-category' ? 'active' : ''}`}
                    onClick={() => handleMenuClick('text-category', '/text-category')}
                  >
                    文案分类管理
                  </div>
                </div>
              )}
            </>
          )}
        </nav>
      </aside>
      <main className="layout-main">
        <header className="layout-header">
          <div className="header-title">
            {activeMenu === 'home' && '首页'}
            {activeMenu === 'user-manage' && '系统管理 - 用户管理'}
            {activeMenu === 'emotion-manage' && '系统配置 - 情绪管理'}
            {activeMenu === 'file-config' && '系统配置 - 文件存储配置'}
            {activeMenu === 'image-category' && '系统配置 - 图片分类管理'}
            {activeMenu === 'text-category' && '系统配置 - 文案分类管理'}
            {activeMenu === 'image-material' && '素材管理 - 图片素材管理'}
            {activeMenu === 'text-material' && '素材管理 - 文案素材管理'}
            {activeMenu === 'voice-material' && '素材管理 - 音色素材管理'}
            {activeMenu === 'music-material' && '素材管理 - 背景音乐管理'}
            {activeMenu === 'text-to-image' && '素材管理 - 文生图功能'}
            {activeMenu === 'chatbot' && '素材管理 - ChatBot'}
            {activeMenu === 'bless-video' && '祝福语视频管理'}
            {activeMenu === 'video-task' && '创作祝福语视频'}
          </div>
          <div className="header-right">
            <span className="user-name">{currentUser?.username || '当前用户'}</span>
            <span className="user-role">({currentUser?.role === 'ADMIN' ? '管理员' : '普通用户'})</span>
            <button className="link-button" onClick={handleLogout}>
              退出登录
            </button>
          </div>
        </header>
        <div className="layout-content">
          <Outlet context={{ currentUser }} />
        </div>
      </main>
    </div>
  );
}