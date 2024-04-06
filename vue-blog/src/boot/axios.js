import { boot } from "quasar/wrappers";
import axios from "axios";
import { Notify, LocalStorage } from "quasar";

// Be careful when using SSR for cross-request state pollution
// due to creating a Singleton instance here;
// If any client changes this (global) instance, it might be a
// good idea to move this instance creation inside of the
// "export default () => {}" function below (which runs individually
// for each client)
const api = axios.create({
  baseURL: process.env.APP_BASE_URL,
  timeout: 5000,
  headers: {
    "Content-Type": "application/json;charset=UTF-8",
  },
});

api.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
    (response) => {
      const status = response?.data?.status;
      if (status) {
        return response.data;
      }
      const code = response?.data?.code;
      Notify.create({
          message: "服务器异常，请稍后重试",
          color: "negative",
          icon: "warning",
        });
        return Promise.reject(response.message || "Error");
      
    },
    (error) => {
      console.log("接口信息报错" + error);

      return Promise.reject("网络异常，请稍后重试！");
    }
  );


export default boot(({ app }) => {
  
  app.config.globalProperties.$axios = axios;

  app.config.globalProperties.$api = api;
  
})

export { api }
