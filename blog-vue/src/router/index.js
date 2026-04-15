// 路由配置
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '../store/auth.js'

// 前台视图
import HomeView from '../views/frontend/HomeView.vue'
import ArticleDetailView from '../views/frontend/ArticleDetailView.vue'
import FrontCategoryView from '../views/frontend/CategoryView.vue'
import FrontTagView from '../views/frontend/TagView.vue'
import ArchiveView from '../views/frontend/ArchiveView.vue'
import SearchView from '../views/frontend/SearchView.vue'
import NotFoundView from '../views/frontend/NotFoundView.vue'

// 后台布局
import AdminLayout from '../components/admin/AdminLayout.vue'

// 后台视图
import LoginView from '../views/admin/LoginView.vue'
import DashboardView from '../views/admin/DashboardView.vue'
import ArticleListView from '../views/admin/ArticleListView.vue'
import ArticleEditView from '../views/admin/ArticleEditView.vue'
import AdminCategoryView from '../views/admin/CategoryView.vue'
import AdminTagView from '../views/admin/TagView.vue'
import CommentView from '../views/admin/CommentView.vue'

var routes = [
  // ===== 前台路由（无需登录）=====
  {
    path: '/',
    component: HomeView
  },
  {
    path: '/article/:id',
    component: ArticleDetailView
  },
  {
    path: '/category/:id',
    component: FrontCategoryView
  },
  {
    path: '/tag/:id',
    component: FrontTagView
  },
  {
    path: '/archive',
    component: ArchiveView
  },
  {
    path: '/search',
    component: SearchView
  },

  // 404 页面
  {
    path: '/:pathMatch(.*)*',
    component: NotFoundView
  },

  // ===== 后台登录页（独立，不使用 AdminLayout）=====
  {
    path: '/admin/login',
    component: LoginView
  },

  // ===== 后台路由（嵌套在 AdminLayout 下）=====
  {
    path: '/admin',
    component: AdminLayout,
    redirect: '/admin/dashboard',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboard',
        component: DashboardView,
        meta: { requiresAuth: true }
      },
      {
        path: 'articles',
        component: ArticleListView,
        meta: { requiresAuth: true }
      },
      {
        // 新建文章
        path: 'articles/edit',
        component: ArticleEditView,
        meta: { requiresAuth: true }
      },
      {
        // 编辑文章
        path: 'articles/edit/:id',
        component: ArticleEditView,
        meta: { requiresAuth: true }
      },
      {
        path: 'categories',
        component: AdminCategoryView,
        meta: { requiresAuth: true }
      },
      {
        path: 'tags',
        component: AdminTagView,
        meta: { requiresAuth: true }
      },
      {
        path: 'comments',
        component: CommentView,
        meta: { requiresAuth: true }
      }
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
      // 未登录，跳转到登录页
      next('/admin/login')
      return
    }
  }
  next()
})

export default router
