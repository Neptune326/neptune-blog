<script setup>
import {useDialogPluginComponent} from "quasar";
import {useForm} from "src/utils/useForm";
import {onMounted, ref} from "vue";
import {useMenu, useQInputSelect} from "src/utils/useCommon";

const props = defineProps({
  id: {
    type: String,
    default: "",
  },
  menuId: {
    type: String,
    default: "",
  },
});

defineEmits([...useDialogPluginComponent.emits]);

const {dialogRef, onDialogHide, onDialogOK, onDialogCancel} =
  useDialogPluginComponent();

const {formData, myForm, check, initForm, submitForm} = useForm(props);
formData.value.menuId = props.menuId;

const {menuSelects} = useMenu({select: true});
onMounted(() => {
  initForm("/admin/resource/editInfo");
});

const {
  filterOptions: nameOptions,
  createValue: nameCreateValue,
  filterFn: nameFilterFn
} = useQInputSelect(['查看', '编辑', '删除']);
const {
  filterOptions: codeOptions,
  createValue: codeCreateValue,
  filterFn: codeFilterFn
} = useQInputSelect(['view', 'edit', 'delete']);

const nameRef = ref(null);
const codeRef = ref(null);
const menuBlur = () => {
  nameRef.value.validate();
  codeRef.value.validate();
}
</script>
<template>
  <q-dialog persistent ref="dialogRef" @hide="onDialogHide">
    <q-card class="q-dialog-plugin">
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6"></div>
        <q-space/>
        <q-btn icon="close" flat round v-close-popup/>
      </q-card-section>
      <q-card-section class="scroll" style="max-height: 75vh">
        <q-form ref="myForm" class="row q-ma-md">
          <div class="col-12 q-pa-md">
            <q-select
              ref="nameRef"
              clearable
              outlined
              v-model="formData.name"
              label="资源名称"
              lazy-rules
              :rules="[
                (v) => !!v || '资源名称不能为空',
                (v) => /^.{2,20}$/.test(v) || '资源名称必须为2-20个字符',
                (v) => !formData.menuId ||check( '/admin/resource/check', {name:v,menuId:formData.menuId}),
              ]"
              :debounce="500"
              use-input
              @new-value="nameCreateValue"
              :options="nameOptions"
              @filter="nameFilterFn"
            />

          </div>
          <div class="col-12 q-pa-md">
            <q-select
              ref="codeRef"
              clearable
              outlined
              v-model="formData.code"
              label="资源编码"
              lazy-rules
              :rules="[
                (v) => !!v || '资源编码不能为空',
                (v) => /^.{2,20}$/.test(v) || '资源编码必须为2-20个字符',
                (v) => !formData.menuId ||check('/admin/resource/check', {code:v,menuId:formData.menuId}),
              ]"
              :debounce="500"
              use-input
              @new-value="codeCreateValue"
              :options="codeOptions"
              @filter="codeFilterFn"
            />
          </div>

          <div class="col-12 q-pa-md">
            <q-select
              outlined
              v-model="formData.menuId"
              label="所属菜单"
              :options="menuSelects"
              emit-value
              map-options
              @blur="menuBlur"
            />
          </div>
          <div class="col-12 q-pa-md">
            <q-input
              outlined
              v-model="formData.sort"
              label="资源排序"
              lazy-rules
              :rules="[
                (v) => !!v || '资源排序不能为空',
                (v) => /^[0-9]{1,5}$/.test(v) || '资源排序必须为1-5位数字',
              ]"
              :debounce="500"
            />
          </div>

        </q-form>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn
          color="primary"
          label="保存"
          @click="
            submitForm('/admin/resource/save', '/admin/resource/update', onDialogOK)
          "
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<style>
.q-dialog-plugin {
  min-width: 50vw;
  max-width: 80vw;
}
</style>
