module.exports = {
  publicPath: process.env.NODE_ENV === 'production'
    ? '/'
    : '/',
  devServer: {
    proxy: {
      '/api/*': {
        target: 'http://localhost:9091/',
        ws: true,
        changeOrigin: true,
        pathRewrite: {
          '^/api': '',
        },
        // '/socket': {
        //   target: 'http://localhost:9091',
        //   ws: true,
        //   changeOrigin: true,
        //   pathRewrite: {
        //     '': ''
        //   }
        // }
      },
    },
  },
  // chainWebpack: (config) => {
  //   config.module
  //     .rule('vue')
  //     .tap(options => options);
  // },
};
