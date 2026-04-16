import { createRouter, createWebHistory } from 'vue-router';

/**
 * 路由只做页面级入口映射，业务逻辑仍在页面/hooks 中。
 */
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'game',
      component: () => import('../pages/GamePage.vue'),
      meta: { title: '游戏' }
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/'
    }
  ]
});

router.afterEach((to) => {
  const title = to.meta.title as string | undefined;
  if (title) {
    document.title = `${title} · npcagent-ui`;
  }
});

export default router;
