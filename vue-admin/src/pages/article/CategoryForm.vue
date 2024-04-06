<script setup>
import {onMounted} from "vue";
import IconSelect from "components/IconSelect.vue";
import {useDialogPluginComponent} from "quasar";
import {useForm} from "src/utils/useForm";

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

onMounted(async () => {
  await initForm("/admin/menu/info");
});

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
          <div class="col-12">
            <q-input
              outlined
              v-model="formData.name"
              label="分类"
              lazy-rules
              :rules="[
                (v) => !!v || '分类不能为空',
                (v) => /^.{2,20}$/.test(v) || '分类必须为2-20个字符',
                (v) => check('/admin/category/check', { name: v }),
              ]"
              :debounce="500"
            />
          </div>

          <div class="col-12">
            <IconSelect v-model="formData.icon" label="菜单图标"/>
          </div>
          <div class="col-12">
            <q-input
              outlined
              v-model="formData.sort"
              label="菜单排序"
              lazy-rules
              :rules="[
                (v) => !!v || '排序不能为空',
                (v) => /^[0-9]{1,5}$/.test(v) || '排序必须为1-5位数字',
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
            submitForm('/admin/category/save', '/admin/category/update', onDialogOK)
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
