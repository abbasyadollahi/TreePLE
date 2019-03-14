const fs = require('fs');
const path = require('path');
const webpack = require('webpack');
const wpMerge = require('webpack-merge');
const common = require('./webpack.config.common.js');

const rootDir = path.join(__dirname, '../');

module.exports = wpMerge(common, {
  plugins: [
    new webpack.DefinePlugin({
      webHost: JSON.stringify('localhost'),
      webPort: JSON.stringify(5000),
      apiHost: JSON.stringify('localhost'),
      apiPort: JSON.stringify(8000)
    })
  ],
  mode: 'development',
  devtool: 'eval-source-map',
  devServer: {
    https: {
      key: fs.readFileSync(path.join(rootDir + 'ssl/treeple.key')),
      cert: fs.readFileSync(path.join(rootDir + 'ssl/treeple.crt'))
    },
    port: 5000,
    host: 'localhost',
    contentBase: rootDir,
    historyApiFallback: true
  }
});
