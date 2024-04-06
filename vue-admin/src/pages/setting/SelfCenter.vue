<script setup>
import {ref} from "vue";
import {useForm} from "src/utils/useForm";
import {api} from "boot/axios";
import {useAuthStore} from "src/stores/authStore";
import {useQuasar} from "quasar";

const authStore = useAuthStore();
const userInfo = authStore.getUserInfo;

const isOldPwd = ref(true);
const isNewPwd = ref(true);

const {myForm, check} = useForm({id: userInfo.userId});

const formData = ref({
  userName: userInfo.userName,
  nickName: userInfo.nickName,
  oldPassword: "",
  newPassword: "",
});

const $q = useQuasar();
const saveInfo = () => {
  myForm.value.validate().then((success) => {
    if (!success) {
      return;
    }
    api.post("/admin/user/updateSelfInfo", formData.value).then((res) => {
      if (!res.status) {
        $q.notify({
          message: res.msg,
          color: "negative",
        });
      } else {
        $q.notify({
          message: "保存成功",
          color: "positive",
        });
      }
    });
  });
};
</script>
<template>
  <q-form ref="myForm" class="row justify-center q-ma-md">
    <q-card
      class="card-bg no-shadow col-lg-8 col-md-8 col-xs-12 col-sm-12"
      bordered
    >
      <q-card-section class="text-h6 q-pa-sm">
        <div class="text-h6">个人信息</div>
      </q-card-section>
      <q-card-section class="q-pa-sm row">
        <q-item class="col-lg-8 col-md-8 col-sm-12 col-xs-12">
          <q-item-section>
            <q-input
              outlined
              v-model="formData.userName"
              label="用户名"
              lazy-rules
              :rules="[
                (v) => !!v || '用户名不能为空',
                (v) => /^.{2,20}$/.test(v) || '用户名必须为2-20个字符',
                (v) =>
                  check('/admin/user/check', {
                    userName: v,
                  }),
              ]"
              :debounce="500"
            />
          </q-item-section>
        </q-item>

        <q-item class="col-lg-8 col-md-8 col-sm-12 col-xs-12">
          <q-item-section>
            <q-input
              outlined
              v-model="formData.nickName"
              label="昵称"
              lazy-rules
              :rules="[
                (v) => !!v || '昵称不能为空',
                (v) => /^.{2,20}$/.test(v) || '昵称必须为2-20个字符',
                (v) =>
                  check('/admin/user/check', {
                    nickName: v,
                  }),
              ]"
              :debounce="500"
            />
          </q-item-section>
        </q-item>

        <q-item class="col-lg-8 col-md-8 col-sm-12 col-xs-12">
          <q-item-section>
            <q-input
              outlined
              v-model="formData.oldPassword"
              label="原密码"
              lazy-rules
              :rules="[
                (v) => !!v || '原密码不能为空',
                (v) => /^.{2,20}$/.test(v) || '原密码必须为2-20个字符',
                (v) => v != formData.newPassword || '原密码不能与新密码相同',
              ]"
              :debounce="500"
              :type="isOldPwd ? 'password' : 'text'"
            >
              <template v-slot:append>
                <q-icon
                  :name="isOldPwd ? 'visibility_off' : 'visibility'"
                  class="cursor-pointer"
                  @click="isOldPwd = !isOldPwd"
                />
              </template>
            </q-input>
          </q-item-section>
        </q-item>

        <q-item class="col-lg-8 col-md-8 col-sm-12 col-xs-12">
          <q-item-section>
            <q-input
              outlined
              v-model="formData.newPassword"
              label="新密码"
              lazy-rules
              :rules="[
                (v) => !!v || '新密码不能为空',
                (v) => /^.{2,20}$/.test(v) || '新密码必须为2-20个字符',
                (v) => v != formData.oldPassword || '新密码不能与原密码相同',
              ]"
              :debounce="500"
              :type="isNewPwd ? 'password' : 'text'"
            >
              <template v-slot:append>
                <q-icon
                  :name="isNewPwd ? 'visibility_off' : 'visibility'"
                  class="cursor-pointer"
                  @click="isNewPwd = !isNewPwd"
                />
              </template>
            </q-input>
          </q-item-section>
        </q-item>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn class="text-capitalize bg-info q-mx-md" @click="saveInfo"
        >保存
        </q-btn
        >
      </q-card-actions>
    </q-card>
  </q-form>
</template>

<style></style>
