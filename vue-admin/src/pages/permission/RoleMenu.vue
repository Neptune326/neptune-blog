<script setup>
import {useDialogPluginComponent, useQuasar} from "quasar";
import {useForm} from "src/utils/useForm";
import {onMounted, ref} from "vue";
import {api} from "boot/axios";

const props = defineProps({
  id: {
    type: String,
    default: "",
  },
});

defineEmits([...useDialogPluginComponent.emits]);

const {dialogRef, onDialogHide, onDialogOK, onDialogCancel} =
  useDialogPluginComponent();

const {formData, initForm} = useForm(props);
formData.value.menuIds = [];

onMounted(() => {
  initForm("/admin/role/editInfo");

  api.get("/admin/role/menuTree").then((res) => {
    menuData.value = res.data;
  });
});

const menuData = ref([]);

const $q = useQuasar();
const saveMenu = () => {
  let param = {
    id: formData.value.id,
    menuIds: formData.value.menuIds,
  };
  api.post("/admin/role/saveMenu", param).then((res) => {
    if (res.status) {
      $q.notify({
        color: "positive",
        message: "保存成功",
        icon: "check",
      });
      onDialogHide();
    }
  });
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

      <q-card-section class="scroll " style="min-height: 50vh;">
        <q-tree
          class="scroll q-ma-md"
          :nodes="menuData"
          node-key="value"
          tick-strategy="leaf"
          v-model:ticked="formData.menuIds"
          default-expand-all
        />
      </q-card-section>

      <q-card-actions align="right">
        <q-btn color="primary" label="保存菜单" @click="saveMenu"/>
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
