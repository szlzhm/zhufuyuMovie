import React from 'react';
import PromptSubmitForm from '../../components/PromptSubmitForm.jsx';

/**
 * 提示语提交页面
 */
export default function ImagePromptSubmitPage() {
  return (
    <div className="page-container">
      <div className="page-header">
        <h2>图片创作 - 提示语提交</h2>
      </div>
      <PromptSubmitForm />
    </div>
  );
}
