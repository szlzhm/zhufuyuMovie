import React, { useState, useEffect } from 'react';
import { getFileRootPath, updateFileRootPath } from '../services/fileConfig';
import { useMessage } from '../components/Dialog';

export default function FileConfigPage() {
  const { success, error: showError, warning } = useMessage();
  const [configValue, setConfigValue] = useState('');
  const [loading, setLoading] = useState(false);
  const [editing, setEditing] = useState(false);
  const [tempValue, setTempValue] = useState('');

  useEffect(() => {
    loadConfig();
  }, []);

  const loadConfig = async () => {
    setLoading(true);
    try {
      const resp = await getFileRootPath();
      if (resp.code === 0 && resp.data) {
        setConfigValue(resp.data.rootPath || '');
        setTempValue(resp.data.rootPath || '');
      } else {
        throw new Error(resp.message || '加载文件配置失败');
      }
    } catch (err) {
      console.error('加载文件配置失败:', err);
      showError(err.message || '加载文件配置失败');
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = () => {
    setTempValue(configValue);
    setEditing(true);
  };

  const handleSave = async () => {
    if (!tempValue.trim()) {
      warning('配置值不能为空');
      return;
    }

    try {
      const resp = await updateFileRootPath({ rootPath: tempValue.trim() });
      if (resp.code === 0) {
        success('更新成功');
        setEditing(false);
        setConfigValue(tempValue.trim());
      } else {
        throw new Error(resp.message || '更新失败');
      }
    } catch (err) {
      console.error('更新配置失败:', err);
      showError(err.message || '更新失败');
    }
  };

  const handleCancel = () => {
    setTempValue(configValue);
    setEditing(false);
  };

  const handleChange = (e) => {
    setTempValue(e.target.value);
  };

  return (
    <div className="page-container">
      <h2>文件存储配置</h2>

      {loading ? (
        <div className="loading">加载中...</div>
      ) : (
        <div className="table-container">
          <table className="data-table">
            <thead>
              <tr>
                <th>配置键</th>
                <th>配置值</th>
                <th>描述</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>文件根路径</td>
                <td>
                  {editing ? (
                    <input
                      type="text"
                      value={tempValue}
                      onChange={handleChange}
                      className="form-control"
                      style={{ width: '100%', padding: '4px 8px', fontSize: '14px' }}
                    />
                  ) : (
                    configValue || '-'
                  )}
                </td>
                <td>系统文件存储根路径</td>
                <td>
                  {editing ? (
                    <>
                      <button className="btn btn-primary" onClick={handleSave}>保存</button>
                      <button className="btn btn-default" onClick={handleCancel}>取消</button>
                    </>
                  ) : (
                    <button className="btn-link" onClick={handleEdit}>编辑</button>
                  )}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}