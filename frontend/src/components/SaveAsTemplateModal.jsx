import React, { useEffect, useState } from 'react';
import { Modal, Form, Input, InputNumber, Slider, Row, Col, Select, Switch, Space, Button } from 'antd';
import { PlusOutlined, MinusCircleOutlined } from '@ant-design/icons';

const { TextArea } = Input;
const { Option } = Select;

/**
 * 保存为模板模态窗口组件
 */
export default function SaveAsTemplateModal({ visible, onCancel, onSave, initialData }) {
  const [form] = Form.useForm();

  // 分辨率选项
  const resolutionOptions = [
    { label: '1:1 (1024x1024)', value: '1024*1024' },
    { label: '9:16 (832x1536)', value: '832*1536' },
    { label: '16:9 (1536x832)', value: '1536*832' },
    { label: '4:3 (1216x912)', value: '1216*912' },
    { label: '3:4 (912x1216)', value: '912*1216' },
    { label: '9:16 (684x1216)', value: '684*1216' },
    { label: '21:9 (1920x832)', value: '1920*832' },
    { label: '1:1 (512x512)', value: '512*512' },
    { label: '9:16 (416x768)', value: '416*768' },
    { label: '16:9 (768x416)', value: '768*416' },
    { label: '4:3 (608x456)', value: '608*456' },
    { label: '3:4 (456x608)', value: '456*608' },
    { label: '9:16 (342x608)', value: '342*608' },
    { label: '21:9 (960x416)', value: '960*416' },
  ];

  useEffect(() => {
    if (visible && initialData) {
      // 添加一个小延迟，确保 Form 完全挂载后再设置值
      const timer = setTimeout(() => {
        // 确保 form 已连接
        if (form) {
          // 先重置表单以清除任何残留状态
          form.resetFields();
          
          // 处理自定义参数
          let customParamsList = [{ name: '', value: '' }];
          if (initialData.parameters?.customParams) {
            customParamsList = Object.entries(initialData.parameters.customParams).map(([name, value]) => ({ name, value }));
          }

          const formValues = {
            templateContent: initialData.templateContent || '',
            placeholderKeywords: initialData.placeholderKeywords !== undefined && initialData.placeholderKeywords !== null ? initialData.placeholderKeywords : '',
            templateStatus: 1,
            parameters: {
              ...{
                resolution: '912*1216',
                numImages: 1,
                seed: -1,
                smartOptimization: false,
                inferenceSteps: 4,
                cfgScale: 1.0,
                enableCustomParams: false
              },
              ...(initialData.parameters || {})
            },
            customParamsList: customParamsList
          };
          
          form.setFieldsValue(formValues);
        }
      }, 0);

      return () => clearTimeout(timer);
    } else if (!visible) {
      // Modal 关闭时重置表单
      form.resetFields();
    }
  }, [visible, initialData, form]);

  const handleOk = () => {
    form.validateFields().then(() => {
      // 使用 getFieldsValue 获取所有字段值，包括未验证的字段
      const values = form.getFieldsValue(true);
      
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

      onSave(values);
      form.resetFields();
    }).catch(err => {
      console.error('Form validation failed:', err);
    });
  };

  const handleCancel = () => {
    form.resetFields();
    onCancel();
  };

  return (
    <Modal
      title="保存为模板"
      open={visible}
      onOk={handleOk}
      onCancel={handleCancel}
      width={900}
      okText="保存"
      cancelText="取消"
    >
      <Form
        form={form}
        layout="vertical"
      >
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
          <TextArea rows={6} placeholder="提示语内容（可手动添加 {'{{'}关键词{'}}'}，例如：{'{{'}八方来财{'}}'} ）" />
        </Form.Item>

        <Form.Item
          name="placeholderKeywords"
          label="占位符关键词"
          preserve={true}
          extra="输入模板中使用的占位符（不带大括号），多个以逗号分隔。例如: 风格,颜色,主题"
        >
          <Input 
            placeholder="例如: 风格,颜色,主题"
            onChange={(e) => {
              // 手动设置表单字段值
              form.setFieldsValue({ placeholderKeywords: e.target.value });
            }}
          />
        </Form.Item>

        <Form.Item
          name={['parameters', 'resolution']}
          label="图像分辨率"
        >
          <Select placeholder="请选择分辨率">
            {resolutionOptions.map(opt => (
              <Option key={opt.value} value={opt.value}>{opt.label}</Option>
            ))}
          </Select>
        </Form.Item>

        <Form.Item
          name={['parameters', 'numImages']}
          label="生成图片数量"
        >
          <InputNumber min={1} max={4} style={{ width: '100%' }} />
        </Form.Item>

        <Form.Item label="随机种子 (-1 为随机)">
          <Row gutter={16}>
            <Col span={16}>
              <Form.Item name={['parameters', 'seed']} noStyle>
                <Slider 
                  min={-1} 
                  max={10000}
                  tooltip={{ formatter: (val) => val === -1 ? '随机' : val }}
                />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name={['parameters', 'seed']} noStyle>
                <InputNumber min={-1} max={99999999} style={{ width: '100%' }} />
              </Form.Item>
            </Col>
          </Row>
        </Form.Item>

        <Form.Item
          name={['parameters', 'smartOptimization']}
          valuePropName="checked"
        >
          <Switch /> <span style={{ marginLeft: 8 }}>开启提示语智能优化</span>
        </Form.Item>

        <Row gutter={16}>
          <Col span={12}>
            <Form.Item
              name={['parameters', 'inferenceSteps']}
              label="推理步骤"
            >
              <InputNumber min={1} max={100} style={{ width: '100%' }} />
            </Form.Item>
          </Col>
          <Col span={12}>
            <Form.Item
              name={['parameters', 'cfgScale']}
              label="CFG (提示词相关性)"
            >
              <InputNumber min={0.1} max={20.0} step={0.1} style={{ width: '100%' }} />
            </Form.Item>
          </Col>
        </Row>

        <Form.Item
          name={['parameters', 'enableCustomParams']}
          label="启用自定义参数"
          valuePropName="checked"
        >
          <Switch />
        </Form.Item>

        <Form.Item noStyle shouldUpdate={(prevValues, curValues) => 
          prevValues.parameters?.enableCustomParams !== curValues.parameters?.enableCustomParams
        }>
          {({ getFieldValue }) => 
            getFieldValue(['parameters', 'enableCustomParams']) && (
              <div style={{ border: '1px solid #d9d9d9', padding: 16, borderRadius: 4, marginBottom: 16 }}>
                <div style={{ marginBottom: 12, fontWeight: 500 }}>自定义参数</div>
                <Form.List name="customParamsList">
                  {(fields, { add, remove }) => (
                    <>
                      {fields.map(({ key, name, ...restField }) => (
                        <Space key={key} style={{ display: 'flex', marginBottom: 8 }} align="baseline">
                          <Form.Item
                            {...restField}
                            name={[name, 'name']}
                            rules={[{ required: true, message: '参数名不能为空' }]}
                          >
                            <Input placeholder="参数名" style={{ width: 200 }} />
                          </Form.Item>
                          <Form.Item
                            {...restField}
                            name={[name, 'value']}
                          >
                            <Input placeholder="参数值" style={{ width: 200 }} />
                          </Form.Item>
                          <MinusCircleOutlined onClick={() => remove(name)} />
                        </Space>
                      ))}
                      <Form.Item>
                        <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                          添加参数
                        </Button>
                      </Form.Item>
                    </>
                  )}
                </Form.List>
              </div>
            )
          }
        </Form.Item>
      </Form>
    </Modal>
  );
}
