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

formData.value.resourceIds = [];

onMounted(() => {
  initForm("/admin/role/editInfo");

  api.get("/admin/role/resourceTree").then((res) => {
    resourceData.value = res.data;
  });
});

const resourceData = ref([]);

const $q = useQuasar();
const saveResource = () => {
  let param = {
    id: formData.value.id,
    resourceIds: formData.value.resourceIds,
  };
  api.post("/admin/role/saveResource", param).then((res) => {
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

      <q-card-section class="scroll" style="min-height: 50vh">
        <q-tree
          class="scroll q-ma-md"
          :nodes="resourceData"
          node-key="value"
          tick-strategy="leaf"
          v-model:ticked="formData.resourceIds"
          default-expand-all
        />
      </q-card-section>

      <q-card-actions align="right">
        <q-btn color="primary" label="保存资源" @click="saveResource"/>
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
