import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      redirect: '/push'
    },
    {
      path: '/push',
      name: 'Push',
      component: () => import('../push/index.vue')
    }
  ]
});

export default router; 