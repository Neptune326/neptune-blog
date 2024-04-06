<script setup>
import { ref } from "vue";
import { useRouter,useRoute } from "vue-router";
import { useQuasar } from "quasar";
import { useAuthStore } from "src/stores/authStore";

const authStore = useAuthStore();

const router = useRouter();
const route = useRoute();
const $q = useQuasar();
const userInfo = ref({ username: "admin", password: "123@admin" });

// 定义校验规则对象
const validationRules = {
  required: (val) => !!val || "必填项",
  //username: (val) => /^[a-zA-Z0-9_\.]{6,18}$/.test(val) ||"用户名必须为6-18个字符，包含字母、数字、下划线、点",
  //password: (val) =>/^[a-zA-Z0-9_\.]{6,18}$/.test(val) || "密码必须为6-18个字符，包含字母、数字、下划线、点",
};

const loginForm = ref(null);
const userLogin = async () => {
  loginForm.value.validate().then(async (valid) => {
    if (valid) {
       await authStore.login({
        userName: userInfo.value.username,
        password: userInfo.value.password,
      },route.query.redirect);
    }
  });
};
</script>
<template>
  <div class="login-page">
    <div class="form-container">
      userName:{{ authStore.userInfo.userName }}
      <q-form @submit.prevent="userLogin" ref="loginForm">
        <div class="login-card">
          <div class="text-h6">登录</div>
          <q-input
            v-model="userInfo.username"
            label="用户名"
            filled
            outlined
            :rules="[validationRules.required, validationRules.username]"
          />
          <q-input
            v-model="userInfo.password"
            label="密码"
            type="password"
            filled
            outlined
            :rules="[validationRules.required, validationRules.password]"
          />
          <q-btn color="primary" label="登录" type="submit" class="q-mt-md" />
        </div>
      </q-form>
    </div>
  </div>
</template>

<style scoped lang="scss">
.login-page {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.form-container {
  background-color: #e0e0e0; /* 修改表单容器背景颜色 */
  padding: 20px;
  border-radius: 8px; /* 添加边框圆角 */
  width: 500px; /* 增加表单容器宽度 */
}

.login-card {
  text-align: center; /* 居中登录文字和按钮 */
  padding: 20px 10px;
}

.text-h6 {
  font-size: 1.5rem;
  font-weight: bold;
  margin-bottom: 1rem;
}

.q-input {
  width: 100%;
  margin-bottom: 10px;
}

.q-btn {
  width: 100%;
  max-width: 200px; /* 设置按钮最大宽度 */
  margin-top: 20px;
}
</style>
