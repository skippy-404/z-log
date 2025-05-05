# Vue 3 + Vite

This template should help get you started developing with Vue 3 in Vite. The template uses Vue 3 `<script setup>` SFCs, check out the [script setup docs](https://v3.vuejs.org/api/sfc-script-setup.html#sfc-script-setup) to learn more.

Learn more about IDE Support for Vue in the [Vue Docs Scaling up Guide](https://vuejs.org/guide/scaling-up/tooling.html#ide-support).

vue
├── dist  //项目构建后的输出目录
├── node_modules
│   └── ...  // 各类依赖
├── public
│   └── index.html  // 项目主入口文件
│   └── ...  // 其他公共资源
├── src
│   └── assets  //静态资源
│       └── logo.png
│   └── components  //公共组件
│       └── HelloWorld.vue
│   └── plugins  //插件资源
│       └── axios.js
│   └── router  //路由配置
│       └── index.js
│   └── store  //vuex文件
│       └── index.js
│   └── views  //视图组件
│       └── About.vue
│       └── Home.vue
│   └── App.vue  //根组件
│   └── main.js  //入口文件
├── tests  //选装：测试模块
├── .gitignore  //Git上传时需要忽略的文件列表
├── package-lock.json  //版本管理使用的文件
├── package.json  //项目的基本配置信息文件
├── README.md  //项目的描述文件
├── ...  //其他依赖的独立配置信息文件
