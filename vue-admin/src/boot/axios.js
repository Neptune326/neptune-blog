import { boot } from "quasar/wrappers";
import axios from "axios";
import { Notify, LocalStorage } from "quasar";
import { useAuthStore } from "src/stores/authStore";

const api = axios.create({
  baseURL: process.env.APP_BASE_URL,
  timeout: 5000,
  headers: {
    "Content-Type": "application/json;charset=UTF-8",
  },
});

api.interceptors.request.use(
  (config) => {
    const token = LocalStorage.getItem("token");
    config.headers["Authorization"] = `Bearer ${token}`;
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default boot(({ app, store,router }) => {
  api.interceptors.response.use(
    (response) => {
      const status = response?.data?.status;
      if (status) {
        return response.data;
      }
      const code = response?.data?.code;
      if (code === 401) {
        Notify.create({
          message: "登录信息已过期，请重新登录",
          color: "negative",
          icon: "warning",
        });
        const authStore = useAuthStore(store);
        authStore.removeUserInfo();
        router.replace({ name: "login" });

        return Promise.reject(response.message || "Error");
      } else if (code === 403) {
        Notify.create({
          message: "您没有权限访问",
          color: "negative",
          icon: "warning",
        });
        router.replace({ name: "403" });
        return Promise.reject(response.message || "Error");
      } else if (code === 500) {
        Notify.create({
          message: "服务器异常，请稍后重试",
          color: "negative",
          icon: "warning",
        });
        return Promise.reject(response.message || "Error");
      }

      return response.data;
    },
    (error) => {
      console.log("接口信息报错" + error);

      return Promise.reject("网络异常，请稍后重试！");
    }
  );

  app.config.globalProperties.$axios = axios;

  app.config.globalProperties.$api = api;
});

export { api };
