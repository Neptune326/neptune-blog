const routes = [
  {
    path: '/',
    component: () => import('layouts/MainLayout.vue'),
    children: [
      {
        path: '',
        component: () => import('pages/article/ArticleListPlus.vue')
      },
      {
        path: 'article-list',
        name: 'article-list',
        component: () => import('pages/article/ArticleList.vue')
      },
      {
        path: 'article/:id',
        name: 'article-view',
        component: () => import('pages/article/ArticleView.vue')
      },
    ]
  },

  // Always leave this as last one,
  // but you can also remove it
  {
    path: '/:catchAll(.*)*',
    component: () => import('pages/ErrorNotFound.vue')
  }
]

export default routes
