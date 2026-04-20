module.exports = {
  devServer: {
    port: 8081,
    proxy: {
      '/foundation': {
        target: 'http://localhost:8088',
        changeOrigin: true
      }
    }
  }
}
