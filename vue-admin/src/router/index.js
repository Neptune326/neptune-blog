import { route } from "quasar/wrappers";
import {
  createRouter,
  createMemoryHistory,
  createWebHistory,
  createWebHashHistory,
} from "vue-router";
import routes from "./routes";
import { LocalStorage, Notify, Loading, QSpinnerFacebook } from "quasar";
import { useAuthStore } from "src/stores/authStore";

export default route(function ({ store /* , ssrContext */ }) {
  const createHistory = process.env.SERVER
    ? createMemoryHistory
    : process.env.VUE_ROUTER_MODE === "history"
    ? createWebHistory
    : createWebHashHistory;

  const Router = createRouter({
    scrollBehavior: () => ({ left: 0, top: 0 }),
    routes,

    history: createHistory(process.env.VUE_ROUTER_BASE),
  });

  Router.beforeEach(async (to, from, next) => {
    if (to.name === "login" || to.name === "test") {
      next();
    } else {
      const authStore = useAuthStore(store);
      const token = authStore.getToken;
      if (token) {
        const isReady = authStore.isReady;

        if (!isReady) {
          let startTimer = Date.now();
          Loading.show({
            spinner: QSpinnerFacebook,
            spinnerColor: "yellow",
            spinnerSize: 140,
            backgroundColor: "primary",
            message: "Some important process is in progress. Hang on...",
            messageColor: "black",
          });
          try {
            await authStore.addMenuAndRoute();
            //next({ ...to });
            next({ path: to.path, replace: true });
          } catch (e) {
            console.log(e);
            LocalStorage.remove("userInfo");
            LocalStorage.remove("token");
            next({ name: "login", query: { redirect: to.path } });
          } finally {
            let endTimer = Date.now();
            let time = endTimer - startTimer;
            if (time < 1000) {
              setTimeout(() => {
                Loading.hide();
              }, 1000 - time);
            } else {
              Loading.hide();
            }
          }
        } else {
          next();
        }
      } else {
        Notify.create({
          message: "请先登录",
          color: "negative",
          icon: "warning",
        });
        next({ name: "login", query: { redirect: to.path } });
      }
    }
  });

  return Router;
});
