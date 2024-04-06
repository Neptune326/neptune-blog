<script setup>
import {useDialogPluginComponent} from "quasar";
import {useForm} from "src/utils/useForm";
import {onMounted, ref} from "vue";
import {api} from "boot/axios";
import AvatarUpload from "src/components/AvatarUpload.vue";

const props = defineProps({
  id: {
    type: String,
    default: "",
  },
});

defineEmits([...useDialogPluginComponent.emits]);

const {dialogRef, onDialogHide, onDialogOK, onDialogCancel} =
  useDialogPluginComponent();

const {formData, myForm, check, initForm, submitForm} = useForm(props);

onMounted(() => {
  initForm("/admin/user/editInfo");

  api.get("/admin/role/selectList").then((res) => {
    roleSelects.value = res.data;
  });
});

const saveUser = () => {
  submitForm("/admin/user/save", "/admin/user/update", onDialogOK);
};

const isPwd = ref(true);

const roleSelects = ref([]);
</script>
<template>
  <q-dialog persistent ref="dialogRef" @hide="onDialogHide">
    <q-card class="q-dialog-plugin">
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6"></div>
        <q-space/>
        <q-btn icon="close" flat round v-close-popup/>
      </q-card-section>

      <q-card-section>
        <q-form ref="myForm" class="row q-ma-md scroll">
          <div class="col-12 q-pa-md">
            <q-input
              outlined
              v-model="formData.userName"
              label="用户名"
              lazy-rules
              :rules="[
                (v) => !!v || '用户名不能为空',
                (v) => /^.{2,20}$/.test(v) || '用户名必须为2-20个字符',
                (v) => check('/admin/user/check', { userName: v }),
              ]"
              :debounce="500"
            />
          </div>
          <div class="col-12 q-pa-md">
            <q-input
              outlined
              v-model="formData.nickName"
              label="昵称"
              lazy-rules
              :rules="[
                (v) => !!v || '昵称不能为空',
                (v) => /^.{2,20}$/.test(v) || '昵称必须为2-20个字符',
                (v) => check('/admin/user/check', { nickName: v }),
              ]"
              :debounce="500"
            />
          </div>
          <div class="col-12 q-pa-md" v-if="!props.id">
            <q-input
              outlined
              v-model="formData.password"
              label="密码"
              lazy-rules
              :rules="[
                (v) => !!v || '密码不能为空',
                (v) => /^.{2,20}$/.test(v) || '密码必须为2-20个字符',
              ]"
              :debounce="500"
              :type="isPwd ? 'password' : 'text'"
            >
              <template v-slot:append>
                <q-icon
                  :name="isPwd ? 'visibility_off' : 'visibility'"
                  class="cursor-pointer"
                  @click="isPwd = !isPwd"
                />
              </template>
            </q-input>
          </div>
          <div class="col-12 q-pa-md">
            <q-select
              outlined
              v-model="formData.roleIds"
              label="角色"
              multiple
              :options="roleSelects"
              emit-value
              map-options
            />
          </div>
          <div class="col-12 q-pa-md">
            <AvatarUpload v-model="formData.avatar" height="150px"/>
          </div>
        </q-form>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn color="primary" label="保存" @click="saveUser"/>
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<style>
.q-dialog-plugin {
  min-width: 60vw;
  max-width: 80vw;
}
</style>
