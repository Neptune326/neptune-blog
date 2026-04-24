// 路由配置
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/store/auth.js'

// ===== 前台视图 =====
import LandingView from '@/views/frontend/home/LandingView.vue'
import HomeView from '@/views/frontend/home/HomeView.vue'
import ArticleDetailView from '@/views/frontend/article/ArticleDetailView.vue'
import FrontCategoryView from '@/views/frontend/browse/CategoryView.vue'
import FrontTagView from '@/views/frontend/browse/TagView.vue'
import ArchiveView from '@/views/frontend/browse/ArchiveView.vue'
import SearchView from '@/views/frontend/browse/SearchView.vue'
import AboutView from '@/views/frontend/other/AboutView.vue'
import MessageView from '@/views/frontend/other/MessageView.vue'
import NotFoundView from '@/views/frontend/other/NotFoundView.vue'

// ===== 后台布局 =====
import AdminLayout from '@/components/admin/AdminLayout.vue'

// ===== 后台视图 =====
import LoginView from '@/views/admin/auth/LoginView.vue'
import DashboardView from '@/views/admin/dashboard/DashboardView.vue'
// content
import ArticleListView from '@/views/admin/content/ArticleListView.vue'
import ArticleEditView from '@/views/admin/content/ArticleEditView.vue'
import AdminCategoryView from '@/views/admin/content/CategoryView.vue'
import AdminTagView from '@/views/admin/content/TagView.vue'
import SeriesView from '@/views/admin/content/SeriesView.vue'
// interaction
import CommentView from '@/views/admin/interaction/CommentView.vue'
import MessageAdminView from '@/views/admin/interaction/MessageAdminView.vue'
import CommentBlacklistView from '@/views/admin/interaction/CommentBlacklistView.vue'
// system
import SysConfigView from '@/views/admin/system/SysConfigView.vue'
import AboutEditView from '@/views/admin/system/AboutEditView.vue'
import AdminUserView from '@/views/admin/system/AdminUserView.vue'
import FriendLinkView from '@/views/admin/system/FriendLinkView.vue'
import DataBackupView from '@/views/admin/system/DataBackupView.vue'
import FeatureGuideView from '@/views/admin/system/FeatureGuideView.vue'

var routes = [
  // ===== 前台路由（无需登录）=====
  { path: '/', component: LandingView },
  { path: '/articles', component: HomeView },
  { path: '/article/:id', component: ArticleDetailView },
  { path: '/category/:id', component: FrontCategoryView },
  { path: '/tag/:id', component: FrontTagView },
  { path: '/archive', component: ArchiveView },
  { path: '/about', component: AboutView },
  { path: '/message', component: MessageView },
  { path: '/search', component: SearchView },
  { path: '/:pathMatch(.*)*', component: NotFoundView },

  // ===== 后台登录页 =====
  { path: '/admin/login', component: LoginView },

  // ===== 后台路由（需要登录）=====
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true },
    children: [
      // dashboard
      { path: 'dashboard',          component: DashboardView,        meta: { requiresAuth: true } },
      // content
      { path: 'articles',           component: ArticleListView,      meta: { requiresAuth: true } },
      { path: 'articles/edit',      component: ArticleEditView,      meta: { requiresAuth: true } },
      { path: 'articles/edit/:id',  component: ArticleEditView,      meta: { requiresAuth: true } },
      { path: 'categories',         component: AdminCategoryView,    meta: { requiresAuth: true } },
      { path: 'tags',               component: AdminTagView,         meta: { requiresAuth: true } },
      { path: 'series',             component: SeriesView,           meta: { requiresAuth: true } },
      // interaction
      { path: 'comments',           component: CommentView,          meta: { requiresAuth: true } },
      { path: 'messages',           component: MessageAdminView,     meta: { requiresAuth: true } },
      { path: 'comment-blacklist',  component: CommentBlacklistView, meta: { requiresAuth: true } },
      // system
      { path: 'sys-config',         component: SysConfigView,        meta: { requiresAuth: true } },
      { path: 'about',              component: AboutEditView,        meta: { requiresAuth: true } },
      { path: 'admin-users',        component: AdminUserView,        meta: { requiresAuth: true } },
      { path: 'friend-links',       component: FriendLinkView,       meta: { requiresAuth: true } },
      { path: 'data-backup',        component: DataBackupView,       meta: { requiresAuth: true } },
      { path: 'feature-guide',      component: FeatureGuideView,     meta: { requiresAuth: true } },
      // logs
      // 日志页面文件当前缺失，暂时下线路由以避免 Vite 启动时扫描报错
    ]
  }
]

var router = createRouter({
  history: createWebHistory(),
  routes: routes
})

// 路由守卫：需要登录的路由检查认证状态
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

