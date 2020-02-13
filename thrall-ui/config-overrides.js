const {
  override,
  fixBabelImports,
  addWebpackPlugin
} = require('customize-cra');
const path = require("path");
const paths = require('react-scripts/config/paths');
paths.appBuild = path.join(path.dirname(paths.appBuild), '../thrall-server/src/main/resources/dist'); 
const AntdDayjsWebpackPlugin = require('antd-dayjs-webpack-plugin');
module.exports = override(
  fixBabelImports('import', {
    libraryName: 'antd',
    libraryDirectory: 'es',
    style: 'css',
  }),
  addWebpackPlugin(new AntdDayjsWebpackPlugin())
);