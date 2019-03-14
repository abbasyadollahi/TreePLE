const path = require('path');
const webpack = require('webpack');
const wpMerge = require('webpack-merge');
const common = require('./webpack.config.common.js');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CompressionPlugin = require('compression-webpack-plugin');

const rootDir = path.join(__dirname, '../');

module.exports = wpMerge(common, {
  mode: 'production',
  plugins: [
    new webpack.DefinePlugin({
      webHost: JSON.stringify('treeple-web.herokuapp.com'),
      webPort: JSON.stringify(5000),
      apiHost: JSON.stringify('treeple-api.herokuapp.com'),
      apiPort: JSON.stringify(8000)
    }),
    new webpack.optimize.AggressiveMergingPlugin(),
    new webpack.optimize.OccurrenceOrderPlugin(),
    new HtmlWebpackPlugin({
      inject: false,
      template: './index.html',
      favicon: './favicon.ico'
    }),
    new CompressionPlugin({
      asset: '[path].gz[query]',
      algorithm: 'gzip',
      test: /\.js$|\.jsx$|\.css$|\.html$/,
      threshold: 10240,
      minRatio: 0.8
    })
  ],
  performance: {
    maxAssetSize: 1024000,
    maxEntrypointSize: 1024000
  },
  devServer: {
    port: process.env.PORT || 5000,
    compress: true,
    contentBase: rootDir,
    historyApiFallback: true,
    // disableHostCheck: true
  }
});
