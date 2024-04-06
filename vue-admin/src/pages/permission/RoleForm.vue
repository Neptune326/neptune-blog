<script setup>
import {useDialogPluginComponent} from "quasar";
import {useForm} from "src/utils/useForm";
import {onMounted} from "vue";

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
  initForm("/admin/role/editInfo");
});

const saveRole = () => {
  submitForm("/admin/role/save", "/admin/role/update", onDialogOK);
};

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
              v-model="formData.name"
              label="角色名称"
              lazy-rules
              :rules="[
                (v) => !!v || '角色名称不能为空',
                (v) => /^.{2,20}$/.test(v) || '角色名称必须为2-20个字符',
                (v) => check('/admin/role/check', { name: v }),
              ]"
              :debounce="500"
            />
          </div>
          <div class="col-12 q-pa-md">
            <q-input
              outlined
              v-model="formData.code"
              label="角色编码"
              lazy-rules
              :rules="[
                (v) => !!v || '角色编码不能为空',
                (v) => /^.{2,20}$/.test(v) || '角色编码必须为2-20个字符',
                (v) => check('/admin/role/check', { code: v }),
              ]"
              :debounce="500"
            />
          </div>
          <div class="col-12 q-pa-md">
            <q-input
              outlined
              v-model="formData.sort"
              label="排序"
              lazy-rules
              :rules="[
                (v) => !!v || '角色排序不能为空',
                (v) => /^[0-9]{1,5}$/.test(v) || '角色排序必须为1-5位数字',
              ]"
              :debounce="500"
            />
          </div>
        </q-form>
      </q-card-section>

      <q-card-actions align="right">
        <q-btn color="primary" label="保存" @click="saveRole"/>
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
