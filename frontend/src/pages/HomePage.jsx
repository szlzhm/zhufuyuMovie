import React from 'react';
import { useOutletContext } from 'react-router-dom';

export default function HomePage() {
  const { currentUser } = useOutletContext();

  return (
    <div className="homepage-container">
      <section className="content-section">
        <div className="card">
          <h3>欢迎使用祝福语视频系统第一阶段（MVP）后台</h3>
          <p style={{ marginTop: '8px', color: '#666' }}>
            当前登录用户：{currentUser?.username} | 角色：{currentUser?.role}
          </p>
        </div>
      </section>
      <section className="content-section" style={{ marginTop: '20px' }}>
        <div className="card">
          <h4>功能模块</h4>
          <ul style={{ marginTop: '12px', paddingLeft: '20px', lineHeight: '1.8' }}>
            <li>素材管理：图片、文案、音色、背景音乐</li>
            <li>祝福语视频管理：视频列表、下载、发布信息</li>
            <li>创作祝福语视频：任务创建、执行、确认入库</li>
            {currentUser?.role === 'ADMIN' && <li>系统管理：用户管理</li>}
            <li>系统配置：文件根目录、情绪枚举、分类管理</li>
          </ul>
        </div>
      </section>
    </div>
  );
}