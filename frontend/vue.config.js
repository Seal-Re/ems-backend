const { defineConfig } = require('@vue/cli-service')

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8080,
    proxy: {
      '/subscribe/consume': {
        target: 'http://192.168.1.97:11451',
        changeOrigin: true
      },
      '/subscribe/product': {
        target: 'http://192.168.1.97:11452',
        changeOrigin: true
      }
    }
  }
})
