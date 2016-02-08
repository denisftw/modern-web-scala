var webpack = require('webpack');

module.exports = {
  entry: './ui/entry.js',
  output: { path: __dirname + '/public/compiled', filename: 'bundle.js' },
  module: {
    loaders: [
      { test: /\.jsx?$/, loader: 'babel-loader', include: /ui/, query: { presets: ['es2015', 'react'] } }
    ]
  },
  plugins: [
    new webpack.ProvidePlugin({
      'fetch': 'imports?this=>global!exports?global.fetch!whatwg-fetch'
    })
  ]
}
