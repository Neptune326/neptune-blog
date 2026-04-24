// 路由：异步页面包，减少首包体积
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth.js'

var routes = [
  { path: '/', component: function() { return import('@/views/frontend/home/LandingView.vue') } },
  { path: '/articles', component: function() { return import('@/views/frontend/home/HomeView.vue') } },
  { path: '/article/:id', component: function() { return import('@/views/frontend/article/ArticleDetailView.vue') } },
  { path: '/category/:id', component: function() { return import('@/views/frontend/browse/CategoryView.vue') } },
  { path: '/tag/:id', component: function() { return import('@/views/frontend/browse/TagView.vue') } },
  { path: '/archive', component: function() { return import('@/views/frontend/browse/ArchiveView.vue') } },
  { path: '/about', component: function() { return import('@/views/frontend/other/AboutView.vue') } },
  { path: '/message', component: function() { return import('@/views/frontend/other/MessageView.vue') } },
  { path: '/search', component: function() { return import('@/views/frontend/browse/SearchView.vue') } },
  { path: '/:pathMatch(.*)*', component: function() { return import('@/views/frontend/other/NotFoundView.vue') } },
  { path: '/admin/login', component: function() { return import('@/views/admin/auth/LoginView.vue') } },
  {
    path: '/admin',
    component: function() { return import('@/components/admin/AdminLayout.vue') },
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true },
    children: [
      { path: 'dashboard',          component: function() { return import('@/views/admin/dashboard/DashboardView.vue') },         meta: { requiresAuth: true } },
      { path: 'articles',           component: function() { return import('@/views/admin/content/ArticleListView.vue') },       meta: { requiresAuth: true } },
      { path: 'articles/edit',      component: function() { return import('@/views/admin/content/ArticleEditView.vue') },         meta: { requiresAuth: true } },
      { path: 'articles/edit/:id',  component: function() { return import('@/views/admin/content/ArticleEditView.vue') },     meta: { requiresAuth: true } },
      { path: 'categories',         component: function() { return import('@/views/admin/content/CategoryView.vue') },     meta: { requiresAuth: true } },
      { path: 'tags',               component: function() { return import('@/views/admin/content/TagView.vue') },          meta: { requiresAuth: true } },
      { path: 'series',             component: function() { return import('@/views/admin/content/SeriesView.vue') },    meta: { requiresAuth: true } },
      { path: 'comments',           component: function() { return import('@/views/admin/interaction/CommentView.vue') },  meta: { requiresAuth: true } },
      { path: 'messages',           component: function() { return import('@/views/admin/interaction/MessageAdminView.vue') }, meta: { requiresAuth: true } },
      { path: 'comment-blacklist',  component: function() { return import('@/views/admin/interaction/CommentBlacklistView.vue') }, meta: { requiresAuth: true } },
      { path: 'sys-config',         component: function() { return import('@/views/admin/system/SysConfigView.vue') },  meta: { requiresAuth: true } },
      { path: 'about',              component: function() { return import('@/views/admin/system/AboutEditView.vue') },    meta: { requiresAuth: true } },
      { path: 'admin-users',        component: function() { return import('@/views/admin/system/AdminUserView.vue') },    meta: { requiresAuth: true } },
      { path: 'friend-links',       component: function() { return import('@/views/admin/system/FriendLinkView.vue') },  meta: { requiresAuth: true } },
      { path: 'data-backup',        component: function() { return import('@/views/admin/system/DataBackupView.vue') },    meta: { requiresAuth: true } },
      { path: 'feature-guide',      component: function() { return import('@/views/admin/system/FeatureGuideView.vue') },  meta: { requiresAuth: true } }
    ]
  }
]

var router = createRouter({
  history: createWebHistory(),
  routes: routes
})

router.beforeEach(function(to, from, next) {
  if (to.meta.requiresAuth) {
    var authStore = useAuthStore()
    if (!authStore.isLoggedIn) {
      next('/admin/login')
      return
    }
  }
  next()
})

export default router
