var webpack = require('webpack');
var ExtractTextPlugin = require("extract-text-webpack-plugin");

module.exports = {
  entry: './ui/entry.js',
  output: { path: __dirname + '/public/compiled', filename: 'bundle.js' },
  module: {
    loaders: [
      { test: /\.jsx?$/, loader: 'babel-loader', include: /ui/, query: { presets: ['es2015', 'react'] } },
      { test: /\.scss$/, loader: ExtractTextPlugin.extract( "style", "css!sass") }
    ]
  },
  plugins: [
    new webpack.ProvidePlugin({
      'fetch': 'imports?this=>global!exports?global.fetch!whatwg-fetch'
    }),
    new ExtractTextPlugin("styles.css")
  ]
}
