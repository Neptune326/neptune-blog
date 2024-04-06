<script setup>
import { useDialogPluginComponent } from "quasar";
import { ref, onMounted } from "vue";
import { useQuasar } from "quasar";
import { api } from "boot/axios";
import IconSelect from "components/IconSelect.vue";

const props = defineProps({
  id: {
    type: String,
    default: "",
  },
  saveUrl: {
    type: String,
    default: "",
  },
  updateUrl: {
    type: String,
    default: "",
  },
  initUrl: {
    type: String,
    default: "",
  },
  colNum: {
    type: Number,
    default: 1,
  },
  options: {
    type: Array,
    default: () => [],
  },
});

defineEmits([
  // REQUIRED; 需要明确指出
  // 组件通过 useDialogPluginComponent() 暴露哪些事件
  ...useDialogPluginComponent.emits,
]);

const { dialogRef, onDialogHide, onDialogOK, onDialogCancel } =
  useDialogPluginComponent();
// dialogRef      - 用在 QDialog 上的 Vue ref 模板引用
// onDialogHide   - 处理 QDialog 上 @hide 事件的函数
// onDialogOK     - 对话框结果为 ok 时会调用的函数
//                    示例: onDialogOK() - 不带参数
//                    示例: onDialogOK({ /*.../* }) - 带参数
// onDialogCancel - 对话框结果为 cancel 时调用的函数

// 这是示例的内容，不是必需的
const $q = useQuasar();
const formData = ref({});

const myForm = ref(null);

function onOKClick() {
  myForm.value.validate().then((success) => {
    if (!success) {
      return;
    }
    api
      .post(props.id ? props.updateUrl : props.saveUrl, formData.value)
      .then((res) => {
        $q.notify({
          message: "保存成功",
          color: "positive",
          icon: "check",
          position: "top",
        });
        onDialogOK();
      });
  });
}

onMounted(() => {
  if (props.initUrl && props.id) {
    api.get(props.initUrl, { params: { id: props.id } }).then((res) => {
      formData.value = res.data;
    });
  }
});
</script>
<template>
  <q-dialog persistent ref="dialogRef" @hide="onDialogHide">
    <q-card class="q-dialog-plugin">
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6"></div>
        <q-space />
        <q-btn icon="close" flat round v-close-popup />
      </q-card-section>
      <q-card-section>
        <q-form ref="myForm">
          <div class="row q-ma-md">
            <div
              v-for="(opt, index) in options"
              :key="index"
              :class="`col-${12 / props.colNum} q-pa-md`"
            >
              <q-input
                v-if="opt.type === 'text'"
                outlined
                v-model="formData[opt.key]"
                :label="opt.label"
                lazy-rules
                :rules="opt.rules"
                :debounce="500"
              />
              <q-select
                v-else-if="opt.type === 'select'"
                outlined
                v-model="formData[opt.key]"
                :label="opt.label"
                :options="opt.options"
                lazy-rules
                :rules="opt.rules"
              />
              <q-checkbox
                v-else-if="opt.type === 'checkbox'"
                outlined
                v-model="formData[opt.key]"
                :label="opt.label"
                :value="opt.value"
                :true-value="opt.trueValue"
                :false-value="opt.falseValue"
                lazy-rules
                :rules="opt.rules"
              />
              <q-radio
                v-else-if="opt.type === 'radio'"
                outlined
                v-model="formData[opt.key]"
                :label="opt.label"
                :options="opt.options"
                lazy-rules
                :rules="opt.rules"
              />
              <IconSelect
                v-else-if="opt.type === 'icon'"
                outlined
                v-model="formData[opt.key]"
                :label="opt.label"
                clearable
                lazy-rules
                :rules="opt.rules"
              />
            </div>
          </div>
        </q-form>
      </q-card-section>
      <!-- 按钮示例 -->
      <q-card-actions align="right">
        <q-btn color="primary" label="保  存" @click="onOKClick" />
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
