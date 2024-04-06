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
    default: "上传图片",
  },
});
const { modelValue } = toRefs(props);
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
  <div  >
    <q-card   class="q-pa-none" style="width: 200px;height: 100px;">
      <q-card-section
        class=" row justify-center items-center"

        v-if="!modelValue"
      >
        <q-btn round flat size="lg" icon="upload"  @click.stop="fileInput.click()">
          <q-tooltip >{{ props.label }}</q-tooltip>
        </q-btn>
      </q-card-section>
      <q-card-section   v-if="modelValue" class="q-pa-none" >
        <q-img
          fit="cover"
          width="200px"
          height="100px"
          :src="modelValue"
          spinner-color="primary"
          class="rounded-borders"
        >
          <q-btn
            round
            flat
            size="lg"
            class="absolute all-pointer-events "
            icon="clear"
            style="left: 50%; top: 50%; transform: translate(-50%, -50%)"
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
