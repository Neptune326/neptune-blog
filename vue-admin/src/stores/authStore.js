import { defineStore } from "pinia";
import { ref, onMounted, nextTick } from "vue";
import { Notify, Dialog, LocalStorage } from "quasar";
import { api } from "boot/axios";

export const useAuthStore = defineStore("auth", {
  state: () => ({
    userInfo: {
      userId: "",
      userName: "",
      nickName: "",
      avatar: "",
    },
    token: "",
    isReady: false,
    menus: [
      /*   {
          "id": "109898878109241344",
          "createTime": "2024-01-30 19:19:54",
          "name": "一级菜单",
          "code": "yjcd",
          "route": "",
          "componentName": "",
          "componentPath": null,
          "icon": "check_circle",
          "sort": "0",
          "parentId": null,
          "parentName": null,
          "level": "1",
          "isHidden": "0",
          "children": [
              {
                  "id": "109899516473921536",
                  "createTime": "2024-01-30 19:22:27",
                  "name": "二级菜单",
                  "code": "ejcd",
                  "route": "",
                  "componentName": "",
                  "componentPath": null,
                  "icon": "warning",
                  "sort": "1",
                  "parentId": "109898878109241344",
                  "parentName": null,
                  "level": "2",
                  "isHidden": "0",
                  "children": [
                      {
                          "id": "109899604587859968",
                          "createTime": "2024-01-30 19:22:48",
                          "name": "三级菜单",
                          "code": "sjcd",
                          "route": "",
                          "componentName": "",
                          "componentPath": null,
                          "icon": "info",
                          "sort": "3",
                          "parentId": "109899516473921536",
                          "parentName": null,
                          "level": "3",
                          "isHidden": "0",
                          "children": null
                      }
                  ]
              }
          ]
      },
      {
          "id": "108777337884155904",
          "createTime": "2024-01-27 17:03:18",
          "name": "菜单",
          "code": "menu",
          "route": "menu",
          "componentName": "menu",
          "componentPath": "permission/Menu",
          "icon": "format_list_bulleted",
          "sort": "0",
          "parentId": null,
          "parentName": null,
          "level": "1",
          "isHidden": "0",
          "children": null
      },
      {
          "id": "81205496546238464",
          "createTime": "2023-11-12 15:02:39",
          "name": "whatsapp",
          "code": "home",
          "route": "whatsapp",
          "componentName": "whatsapp",
          "componentPath": "IndexPage",
          "icon": "format_list_bulleted",
          "sort": "0",
          "parentId": null,
          "parentName": null,
          "level": "1",
          "isHidden": "0",
          "children": null
      },
      {
          "id": "108778122399358976",
          "createTime": "2024-01-27 17:06:25",
          "name": "权限管理",
          "code": "permissionManage",
          "route": "",
          "componentName": "",
          "componentPath": null,
          "icon": "lens",
          "sort": "1",
          "parentId": null,
          "parentName": null,
          "level": "1",
          "isHidden": "0",
          "children": [
              {
                  "id": "70662780058050560",
                  "createTime": "2023-10-14 12:49:40",
                  "name": "资源",
                  "code": "resource",
                  "route": "resource",
                  "componentName": "resource",
                  "componentPath": "permission/Resource",
                  "icon": "print",
                  "sort": "1",
                  "parentId": "108778122399358976",
                  "parentName": null,
                  "level": "2",
                  "isHidden": "0",
                  "children": null
              },
              {
                  "id": "70662976322117632",
                  "createTime": "2023-10-14 12:50:26",
                  "name": "角色",
                  "code": "role",
                  "route": "role",
                  "componentName": "role",
                  "componentPath": "permission/Role",
                  "icon": "error",
                  "sort": "2",
                  "parentId": "108778122399358976",
                  "parentName": null,
                  "level": "2",
                  "isHidden": "0",
                  "children": null
              },
              {
                  "id": "70663092378509312",
                  "createTime": "2023-10-14 12:50:54",
                  "name": "用户",
                  "code": "user",
                  "route": "user",
                  "componentName": "user",
                  "componentPath": "permission/User",
                  "icon": "grade",
                  "sort": "3",
                  "parentId": "108778122399358976",
                  "parentName": null,
                  "level": "2",
                  "isHidden": "0",
                  "children": null
              }
          ]
      } */
    ],
  }),

  getters: {
    getMenus: (state) => state.menus,
    getUserInfo: (state) => {
      let userInfoJson = LocalStorage.getItem("userInfo");
      if (userInfoJson) {
        state.userInfo = JSON.parse(userInfoJson);
      }
      return state.userInfo;
    },
    getToken: (state) => {
      if (state.token) {
        return state.token;
      }
      let token = LocalStorage.getItem("token");
      if (token) {
        state.token = token;
        return token;
      }
      return "";
    },
  },

  actions: {
    async login({ userName, password }, redirectPath) {
      try {
        const response = await api.post("/admin/account/login", {
          userName,
          password,
        });

        if (response.status) {
          const { userId,userName, nickName, avatar, token } = response.data;
          const userInfo = {
            userId,
            userName,
            nickName,
            avatar,
          };
          this.userInfo = userInfo;
          this.token = token;
          LocalStorage.set("userInfo", JSON.stringify(userInfo));
          LocalStorage.set("token", token);
          await this.addMenuAndRoute();

          Notify.create({
            message: "Login success",
            color: "positive",
            icon: "check",
          });
          nextTick(() => {
            this.router.push({ path: redirectPath || "/" });
          });
        } else {
          Notify.create({
            message: response.message,
            color: "negative",
            icon: "warning",
          });
        }
      } catch (error) {
        console.log("login-error", error);
        Notify.create({
          message: "Login failed",
          color: "negative",
          icon: "warning",
        });
      }
    },

    async logout() {
      Dialog.create({
        title: "Logout",
        message: "Are you sure to logout?",
        cancel: true,
        persistent: true,
      }).onOk(() => {
        api.post("/admin/account/logout").then(() => {
          LocalStorage.remove("userInfo");
          LocalStorage.remove("token");
          this.router.push("/login");
          Notify.create({
            message: "Logout success",
            color: "positive",
            icon: "check",
          });
        });
      });
    },

    async addMenuAndRoute() {
      const response = await api.post("/admin/account/menus");

      if (!response.status) {
        Notify.create({
          message: response.message,
          color: "negative",
          icon: "warning",
        });
        return;
      }
      //路由
      console.log("before-router", this.router.getRoutes());
      const { routes } = response.data;
      let userRoutes = [];
      if (routes) {
        userRoutes = routes
          .filter(
            (it) =>
              !!it && !!it.route && !!it.componentPath && !!it.componentName
          )
          .map((item) => ({
            path: item.route,
            name: item.componentName,
            component: () => import(`../pages/${item.componentPath}.vue`),
            meta: { breadCrumb: item.breadCrumb, requiresAuth: true },
          }));

        /*  let a = this.router.getRoutes();
        console.log("a", a);
        routes
          .filter((it) => !!it && !!it.route && !!it.componentPath && !!it.componentName)
          .forEach((item) => {
            debugger;
            const route = {
              path: item.route,
              name: item.componentName,
              component: () => import(`pages/${item.componentPath}.vue`),
              //component: () => this.loadComponent(item.componentPath),
            };
            this.router.addRoute("home", route);
          });

        let b = this.router.getRoutes();
        console.log("b", b);*/
      }
      console.log("userRoutes", userRoutes);
      console.log("Routes before1111:", this.router.getRoutes());
     /*  this.router.addRoute({
        path: "/",
        name: "home",
        component: () => import("layouts/MainLayout.vue"),
        children: [],
        meta: { breadCrumb: "首页", requiresAuth: true },
      }); */
      console.log("Routes before2222:", this.router.getRoutes());
      userRoutes.forEach((route) => {
        this.router.addRoute("home", route);
      });
      console.log("this.router.getRoutes()", this.router.getRoutes());

      //菜单
      let { menus } = response.data;
      menus = this.transformMenus(menus);
      this.menus = menus;
      this.isReady = true;
    },

    transformMenus(menus) {
      return menus.map((menu) => {
        const newMenu = {
          id: menu.id,
          name: menu.name,
          route: menu.route,
          icon: menu.icon,
          level: +menu.level,
        };

        if (menu.children && menu.children.length > 0) {
          newMenu.children = this.transformMenus(menu.children);
        }

        return newMenu;
      });
    },

    removeUserInfo() {
      LocalStorage.remove("userInfo");
      LocalStorage.remove("token");
      this.userInfo = {};
      this.menus = [];
      this.token = "";
      this.isReady = false;
    },

    loadComponent(component) {
      return (resolve) => require([`@/pages/${component}.vue`], resolve);
    },
  },
});
