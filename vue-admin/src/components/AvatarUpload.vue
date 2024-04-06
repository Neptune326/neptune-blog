<script setup>
import { ref, toRefs } from "vue";
import { useQuasar } from "quasar";
import { api } from "boot/axios";

const props = defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  label: {
    type: String,
    default: "上传头像",
  },
  height:{
    type: String,
    default: "300px",
  }
});
const { modelValue, label,height } = toRefs(props);
const emits = defineEmits(["update:modelValue"]);

const $q = useQuasar();

const fileInput = ref(null);
const uploadImage = async () => {
  const file = fileInput.value.files[0];

  // 验证文件类型
  if (!file.type.startsWith("image/")) {
    $q.notify({
      message: "请选择图片文件",
      color: "negative",
    });
    return;
  }

  // 验证文件大小（10M = 10 * 1024 * 1024 字节）
  if (file.size > 10 * 1024 * 1024) {
    $q.notify({
      message: "图片文件大小不能超过 10M",
      color: "negative",
    });
    return;
  }
  const formData = new FormData();
  formData.append("file", file);

  try {
    const response = await api.post("/admin/user/uploadAvatar", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    console.log(response.data);
    emits("update:modelValue", response.data);
  } catch (error) {
    console.error(error);
  }
};
</script>

<template>
  <div>
    <q-toolbar class="bg-primary text-white">
      <q-toolbar-title class="q-uploader__title">{{ label }}</q-toolbar-title>

      <q-btn flat round dense icon="cloud_upload" @click="fileInput.click()">
        <q-tooltip>上传</q-tooltip>
      </q-btn>
    </q-toolbar>

    <q-card>
      <q-card-section class="q-pa-none">
        <q-img
          fit="cover"
          :height="height"
          :src="modelValue"
          spinner-color="primary"
        >
          <q-btn
            v-if="modelValue"
            round
            class="absolute all-pointer-events"
            icon="clear"
            style="top: 8px; right: 8px"
            @click="emits('update:modelValue', '')"
          />
        </q-img>
      </q-card-section>
    </q-card>

    <input
      type="file"
      ref="fileInput"
      accept="image/*"
      style="display: none"
      @change="uploadImage"
    />
  </div>
</template>

<style scoped></style>
