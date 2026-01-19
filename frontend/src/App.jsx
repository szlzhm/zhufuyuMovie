import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import LoginPage from './pages/LoginPage.jsx';
import MainLayout from './pages/MainLayout.jsx';
import UserManagePage from './pages/UserManagePage.jsx';
import HomePage from './pages/HomePage.jsx';
import EmotionManagePage from './pages/EmotionManagePage.jsx';
import FileConfigPage from './pages/FileConfigPage.jsx';
import ImageCategoryPage from './pages/ImageCategoryPage.jsx';
import TextCategoryPage from './pages/TextCategoryPage.jsx';
import ImageMaterialPage from './pages/ImageMaterialPage.jsx';
import TextMaterialPage from './pages/TextMaterialPage.jsx';
import VoiceMaterialPage from './pages/VoiceMaterialPage.jsx';
import MusicMaterialPage from './pages/MusicMaterialPage.jsx';
import BlessVideoPage from './pages/BlessVideoPage.jsx';
import VideoTaskPage from './pages/VideoTaskPage.jsx';
import TextToImagePage from './pages/TextToImagePage.jsx';
import ChatBotPage from './pages/ChatBotPage.jsx';

export default function App() {
  return (
    <ConfigProvider locale={zhCN} componentSize="middle">
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<MainLayout />}>
          <Route index element={<HomePage />} />
          <Route path="user-manage" element={<UserManagePage />} />
          <Route path="emotion-manage" element={<EmotionManagePage />} />
          <Route path="file-config" element={<FileConfigPage />} />
          <Route path="/image-category" element={<ImageCategoryPage />} />
          <Route path="/text-category" element={<TextCategoryPage />} />
          <Route path="/image-material" element={<ImageMaterialPage />} />
          <Route path="/text-material" element={<TextMaterialPage />} />
          <Route path="/voice-material" element={<VoiceMaterialPage />} />
          <Route path="/music-material" element={<MusicMaterialPage />} />
          <Route path="/bless-video" element={<BlessVideoPage />} />
          <Route path="/video-task" element={<VideoTaskPage />} />
          <Route path="/text-to-image" element={<TextToImagePage />} />
          <Route path="/chatbot" element={<ChatBotPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </ConfigProvider>
  );
}