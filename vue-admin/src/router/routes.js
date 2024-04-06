const routes = [
  {
    path: "/login",
    name: "login",
    component: () => import("layouts/Login.vue"),
  },
  {
    path: "/",
    name: "home",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      { path: "",name:"IndexPage", component: () => import("pages/IndexPage.vue") },
      { path: "403",name:"403", component: () => import("pages/tips/403.vue") },
      {
        path: "test",
        name: "test",
        component: () => import("pages/self/SelfComponent.vue"),
      }
/*
      {
        path: "menu",
        name: "Menu",
        component: () => import("pages/permission/Menu.vue"),
      },
      {
        path:'resource',
        name:'Resource',
        component:()=>import('pages/permission/Resource.vue')
      },
      {
        path:'role',
        name:'Role',
        component:()=>import('pages/permission/Role.vue')
      },
      {
        path:'user',
        name:'User',
        component:()=>import('pages/permission/User.vue')
      },
      {
        path:'whatsapp',
        name:'whatsapp',
        component:()=>import('pages/gallery/whatsapp.vue')
      }, */
    ],
    meta: { title:'首页',requiresAuth: true },
  },
/*

  {
    path: "/permission",
    name: "permission",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "menu",
        name: "Menu",
        component: () => import("pages/permission/Menu.vue"),
      },
      {
        path:'resource',
        name:'Resource',
        component:()=>import('pages/permission/Resource.vue')
      },
      {
        path:'role',
        name:'Role',
        component:()=>import('pages/permission/Role.vue')
      },
    ],
  },
*/
/*
  {
    path: "/articles",
    name: "articles",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "edit",
        name: "ArticleEdit",
        component: () => import("pages/article/ArticleEdit.vue"),
      },
      {
        path: "list",
        name: "ArticleList",
        component: () => import("pages/article/ArticleList.vue"),
      },
    ],
  },

  {
    path: "/logs",
    name: "logs",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "operation-log",
        name: "OperationLogList",
        component: () => import("pages/logs/OperationLogList.vue"),
      },
      {
        path:'exception-log',
        name:'ExceptionLogList',
        component:()=>import('pages/logs/ExceptionLogList.vue')
      }
    ],
  },
  {
    path: "/self-component",
    name: "self-component",
    component: () => import("layouts/MainLayout.vue"),
    children: [
      {
        path: "",
        name: "self-component1",
        component: () => import("pages/self/SelfComponent.vue"),
      },
    ],
  },
 */
  {
    path: "/:catchAll(.*)*",
    name: "ErrorNotFound",
    component: () => import("pages/ErrorNotFound.vue"),
  },
];

export default routes;
