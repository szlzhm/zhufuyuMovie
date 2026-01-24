import React, { useState, useEffect, useRef } from 'react';
import { Table, Button, Input, Modal, Form, message, Space, Tag, Popconfirm, Select, Row, Col, InputNumber, Slider, Checkbox, Switch, Divider, Image } from 'antd';
import { PlusOutlined, MinusCircleOutlined, FileImageOutlined, SendOutlined } from '@ant-design/icons';
import { queryTemplates, saveTemplate, deleteTemplate } from '../../services/imageCreation.js';
import PromptSubmitForm from '../../components/PromptSubmitForm.jsx';

const { TextArea } = Input;
const { Option } = Select;

/**
 * 提示语模板管理页面
 */
export default function ImagePromptTemplatePage() {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [total, setTotal] = useState(0);
  const [queryParams, setQueryParams] = useState({
    page: 1,
    size: 10,
    templateContent: ''
  });

  const [isModalVisible, setIsModalVisible] = useState(false);
  const [isSubmitModalVisible, setIsSubmitModalVisible] = useState(false);
  const [submitTemplateId, setSubmitTemplateId] = useState(null);
  const [form] = Form.useForm();
  const [editingId, setEditingId] = useState(null);


  useEffect(() => {
    fetchData();
  }, [queryParams]);


  const [prevModalVisible, setPrevModalVisible] = useState(false);

  const fetchData = async () => {
    setLoading(true);
    try {
      const resp = await queryTemplates(queryParams);
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

  const handleSearch = (value) => {
    setQueryParams({ ...queryParams, page: 1, templateContent: value });
  };

  const showModal = (record = null) => {
    // 先关闭模态框并等待DOM更新，以确保完全重置
    setIsModalVisible(false);
    
    // 使用setTimeout来确保模态框完全关闭后再打开新的
    setTimeout(() => {
      if (record) {
        setEditingId(record.id);
        // 处理参数展示
        const values = { ...record };
        
        if (record.parameters) {
          // 将 parameters 中的字段提升或展开到表单结构
          if (record.parameters.customParams) {
            values.customParamsList = Object.entries(record.parameters.customParams).map(([name, value]) => ({ name, value }));
          }
        }
        
        // 先重置表单以清除任何残留状态
        form.resetFields();
        
        form.setFieldsValue(values);
        
        // 确保 placeholderKeywords 被正确设置，给一点时间让表单更新
        setTimeout(() => {
          // 即使是空字符串也要设置，所以使用 !== undefined && !== null 来判断
          if (record.hasOwnProperty('placeholderKeywords')) {
            form.setFieldsValue({ placeholderKeywords: record.placeholderKeywords || '' });
          }
        }, 10);
      } else {
        setEditingId(null);
        form.resetFields();
        form.setFieldsValue({ 
          templateStatus: 1,
          parameters: {
            resolution: '912*1216',
            numImages: 1,
            seed: -1,
            smartOptimization: 0,
            inferenceSteps: 4,
            cfgScale: 1.0,
            enableCustomParams: false
          },
          customParamsList: [{ name: '', value: '' }]
        });
      }
      setIsModalVisible(true);
    }, 10);
  };

  const handleOk = () => {
    form.validateFields().then(async (formValues) => {
      try {
        const values = { ...formValues };
        // 转换自定义参数
        if (values.parameters?.enableCustomParams && values.customParamsList) {
          const customParams = {};
          values.customParamsList.forEach(item => {
            if (item.name && item.name.trim()) {
              customParams[item.name.trim()] = item.value || '';
            }
          });
          values.parameters.customParams = customParams;
        }
        delete values.customParamsList;

        const resp = await saveTemplate({ ...values, id: editingId });
        if (resp.code === 0) {
          message.success('保存成功');
          setIsModalVisible(false);
          fetchData();
        } else {
          message.error(resp.message || '保存失败');
        }
      } catch (error) {
        message.error('操作失败');
      }
    });
  };

  const handleDelete = async (id) => {
    try {
      const resp = await deleteTemplate(id);
      if (resp.code === 0) {
        message.success('删除成功');
        fetchData();
      } else {
        message.error(resp.message || '删除失败');
      }
    } catch (error) {
      message.error('操作失败');
    }
  };

  const showSubmitModal = (templateId) => {
    setSubmitTemplateId(templateId);
    setIsSubmitModalVisible(true);
  };

  const columns = [
    {
      title: '模板图片',
      dataIndex: 'templateImageUrl',
      key: 'templateImageUrl',
      width: 120,
      render: (imageUrl) => (
        imageUrl ? (
          <Image
            src={imageUrl}
            alt="template"
            width={100}
            height={100}
            style={{ objectFit: 'cover', borderRadius: '4px' }}
            preview
          />
        ) : (
          <div style={{ 
            width: 100, 
            height: 100, 
            display: 'flex', 
            flexDirection: 'column',
            alignItems: 'center', 
            justifyContent: 'center',
            background: '#fafafa',
            border: '1px solid #f0f0f0',
            borderRadius: '4px',
            color: '#bfbfbf'
          }}>
            <FileImageOutlined style={{ fontSize: 24, marginBottom: 4 }} />
            <span style={{ fontSize: 12 }}>无预览图</span>
          </div>
        )
      )
    },
    {
      title: 'ID',
      dataIndex: 'id',
      key: 'id',
      width: 80,
    },
    {
      title: '模板内容',
      dataIndex: 'templateContent',
      key: 'templateContent',
      ellipsis: true,
    },
    {
      title: '占位符关键字',
      dataIndex: 'placeholderKeywords',
      key: 'placeholderKeywords',
      width: 200,
      render: (text) => text || '-',
    },
    {
      title: '状态',
      dataIndex: 'templateStatus',
      key: 'templateStatus',
      width: 100,
      render: (status) => (
        <Tag color={status === 1 ? 'success' : 'default'}>
          {status === 1 ? '启用' : '禁用'}
        </Tag>
      ),
    },
    {
      title: '创建时间',
      dataIndex: 'createdTime',
      key: 'createdTime',
      width: 180,
    },
    {
      title: '操作',
      key: 'action',
      width: 250,
      render: (_, record) => (
        <Space size="middle">
          <Button 
            type="link" 
            icon={<SendOutlined />} 
            onClick={() => showSubmitModal(record.id)}
            disabled={record.templateStatus !== 1}
          >
            提交提示语
          </Button>
          <Button type="link" onClick={() => showModal(record)}>编辑</Button>
          <Popconfirm title="确定删除吗？" onConfirm={() => handleDelete(record.id)} okText="确定" cancelText="取消">
            <Button type="link" danger>删除</Button>
          </Popconfirm>
        </Space>
      ),
    },
  ];

  return (
    <div className="page-container">
      <div className="page-header">
        <h2>提示语模板管理</h2>
        <Button type="primary" onClick={() => showModal()}>新增模板</Button>
      </div>

      <div className="toolbar" style={{ marginBottom: 16 }}>
        <Input.Search
          placeholder="搜索模板内容"
          onSearch={handleSearch}
          style={{ width: 300 }}
          allowClear
          enterButton
        />
      </div>

      <Table
        columns={columns}
        dataSource={data}
        rowKey="id"
        loading={loading}
        pagination={{
          current: queryParams.page,
          pageSize: queryParams.size,
          total: total,
          showSizeChanger: true,
          showTotal: (total) => `共 ${total} 条`,
          onChange: (page, size) => setQueryParams({ ...queryParams, page, size }),
        }}
      />

      <Modal
        title={editingId ? '编辑模板' : '新增模板'}
        open={isModalVisible}
        onOk={handleOk}
        onCancel={() => setIsModalVisible(false)}
        width={600}
        okText="确定"
        cancelText="取消"
      >
        <Form form={form} layout="vertical" key={`template-form-${editingId || 'new'}`}>
          <Form.Item
            name="templateContent"
            label={
              <span>
                模板内容
                <span style={{ marginLeft: 8, fontSize: '12px', color: '#1890ff', fontWeight: 'normal' }}>
                  （使用 {'{{'}关键词{'}}'} 标注占位符，例如：{'{{'}八方来财{'}}'} ）
                </span>
              </span>
            }
            rules={[{ required: true, message: '请输入模板内容' }]}
          >
            <TextArea rows={4} placeholder="支持使用 {'{{'}关键词{'}}'} 作为占位符，例如：{'{{'}八方来财{'}}'}" />
          </Form.Item>
          <Form.Item
            name="placeholderKeywords"
            label="占位符关键字"
            preserve={true}
            extra="输入模板中使用的占位符（不带大括号），多个以逗号分隔。例如: 场景,主体,风格"
          >
            <Input 
              placeholder="例如: 场景,主体,风格"
              onChange={(e) => {
                // 手动设置表单字段值
                form.setFieldsValue({ placeholderKeywords: e.target.value });
              }}
            />
          </Form.Item>

          <Divider titlePlacement="left">预设参数</Divider>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name={['parameters', 'resolution']} label="分辨率">
                <Select placeholder="请选择分辨率">
                  <Option value="1024*1024">1:1 (1024x1024)</Option>
                  <Option value="832*1536">9:16 (832x1536)</Option>
                  <Option value="1536*832">16:9 (1536x832)</Option>
                  <Option value="1216*912">4:3 (1216x912)</Option>
                  <Option value="912*1216">3:4 (912x1216)</Option>
                  <Option value="684*1216">9:16 (684x1216)</Option>
                  <Option value="1920*832">21:9 (1920x832)</Option>
                  <Option value="512*512">1:1 (512x512)</Option>
                  <Option value="416*768">9:16 (416x768)</Option>
                  <Option value="768*416">16:9 (768x416)</Option>
                  <Option value="608*456">4:3 (608x456)</Option>
                  <Option value="456*608">3:4 (456x608)</Option>
                  <Option value="342*608">9:16 (342x608)</Option>
                  <Option value="960*416">21:9 (960x416)</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name={['parameters', 'numImages']} label="图片数量">
                <InputNumber min={1} max={4} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name={['parameters', 'inferenceSteps']} label="推理步数">
                <InputNumber min={1} max={100} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name={['parameters', 'cfgScale']} label="CFG (提示词相关性)">
                <InputNumber min={0.1} max={20.0} step={0.1} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item name={['parameters', 'seed']} label="种子 (-1 为随机)">
            <InputNumber style={{ width: '100%' }} />
          </Form.Item>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item name={['parameters', 'smartOptimization']} label="智能优化" valuePropName="checked">
                <Checkbox>开启智能优化</Checkbox>
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name={['parameters', 'enableCustomParams']} label="启用自定义参数" valuePropName="checked">
                <Switch size="small" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item noStyle shouldUpdate={(prevValues, curValues) => prevValues.parameters?.enableCustomParams !== curValues.parameters?.enableCustomParams}>
            {({ getFieldValue }) => 
              getFieldValue(['parameters', 'enableCustomParams']) && (
                <div style={{ padding: '8px', border: '1px dashed #d9d9d9', borderRadius: '4px', marginBottom: '16px' }}>
                  <Form.List name="customParamsList">
                    {(fields, { add, remove }) => (
                      <>
                        {fields.map(({ key, name, ...restField }) => (
                          <Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                            <Form.Item
                              {...restField}
                              name={[name, 'name']}
                              rules={[{ required: true, message: '参数名' }]}
                              style={{ marginBottom: 0 }}
                            >
                              <Input placeholder="名" style={{ width: 100 }} />
                            </Form.Item>
                            <Form.Item
                              {...restField}
                              name={[name, 'value']}
                              style={{ marginBottom: 0 }}
                            >
                              <Input placeholder="值" style={{ width: 120 }} />
                            </Form.Item>
                            <MinusCircleOutlined onClick={() => remove(name)} />
                          </Space>
                        ))}
                        <Form.Item style={{ marginBottom: 0 }}>
                          <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                            添加
                          </Button>
                        </Form.Item>
                      </>
                    )}
                  </Form.List>
                </div>
              )
            }
          </Form.Item>

          <Form.Item
            name="templateStatus"
            label="状态"
            rules={[{ required: true }]}
          >
            <Select placeholder="请选择状态">
              <Option value={1}>启用</Option>
              <Option value={0}>禁用</Option>
            </Select>
          </Form.Item>
        </Form>
      </Modal>
      <Modal
        title="提交提示语任务"
        open={isSubmitModalVisible}
        onCancel={() => setIsSubmitModalVisible(false)}
        footer={null}
        width={1200}
        destroyOnHidden
      >
        <PromptSubmitForm 
          initialTemplateId={submitTemplateId} 
          onSuccess={() => setIsSubmitModalVisible(false)} 
        />
      </Modal>
    </div>
  );
}
