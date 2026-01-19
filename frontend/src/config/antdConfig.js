// Ant Design 全局配置
import { ConfigProvider } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import { theme } from 'antd';

const antdConfig = {
  locale: zhCN, // 中文语言包
  theme: {
    token: {
      // 主题配置
      colorPrimary: '#1890ff', // 主色调
    },
  },
};

export default antdConfig;
export { ConfigProvider };