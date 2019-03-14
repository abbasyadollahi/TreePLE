const path = require('path');
const webpack = require('webpack');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const CleanWebpackPlugin = require('clean-webpack-plugin');

const rootDir = path.join(__dirname, '../');
const buildDir = path.join(rootDir, 'dist');

module.exports = {
  entry: [
    '@babel/polyfill',
    path.join(rootDir, 'src/index.jsx')
  ],
  output: {
    path: buildDir,
    filename: '[name].bundle.js'
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)?$/,
        exclude: /node_modules/,
        loader: 'babel-loader'
      }, {
        test: /\.less$/,
        loaders: ['style-loader', 'css-loader', 'less-loader']
      }, {
        test: /\.css$/,
        loaders: ['style-loader', 'css-loader']
      }, {
        test: /\.s[a|c]ss$/,
        use: [
          {loader: 'style-loader'},
          {loader: 'css-loader', options: {sourceMap: true}},
          {loader: 'sass-loader', options: {sourceMap: true}}
        ]
      }, {
        test: /\.(html)$/,
        loader: 'html-loader'
      }, {
        test: /\.(png|jpg|jpeg|gif|svg|ico|eot|ttf|woff|woff2)$/,
        include: /images/,
        loader: 'url-loader',
        options: {
          name: '[hash:8].[ext]',
          limit: 10240
        }
      }, {
        test: /\.(pdf)$/,
        include: /static/,
        loader: 'file-loader',
        options: {
          name: '[name].[ext]'
        }
      }
    ]
  },
  optimization: {
    splitChunks: {
      cacheGroups: {
        vendors: {
          test: /[\\/]node_modules[\\/]/,
          name: 'vendors',
          chunks: 'all'
        }
      }
    }
  },
  resolve: {
    extensions: ['.js', '.jsx', '.css', '.scss']
  },
  plugins: [
    new webpack.ProvidePlugin({
      _: 'lodash',
      C: 'constants',
      U: 'Utils'
    }),
    new CopyWebpackPlugin([{
      from: path.join(rootDir, 'src/assets/static'),
      to: buildDir
    }]),
    new CleanWebpackPlugin(buildDir, {
      root: rootDir
    })
  ]
};
