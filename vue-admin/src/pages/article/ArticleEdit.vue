<script setup>
import "@wangeditor/editor/dist/css/style.css"; // 引入 css
import {onBeforeUnmount, onMounted, ref, shallowRef} from "vue";
import {Editor, Toolbar} from "@wangeditor/editor-for-vue";
import {api} from "boot/axios";

const mode = "default"; // 或 'simple
// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef();

// 内容 HTML
const valueHtml = ref("<p>hello</p>");

const toolbarConfig = {};
const editorConfig = {placeholder: "请输入内容..."};

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});

const handleCreated = (editor) => {
  editorRef.value = editor; // 记录 editor 实例，重要！
};

onMounted(() => {

});

const formData = ref({
  title: '',
  content: ''
});
const save = () => {
  api.post('/admin/article/save', {content: valueHtml.value}).then(res => {
    console.log(res)
  })
};
</script>


<template>
  <div class="q-ma-md" style="border: 1px solid #ccc">
    <div class="row q-ma-md">
      <q-input class="col-6" v-model="formData.title" placeholder="请输入标题" dense/>
      <q-space/>
      <q-btn
        class="col-1"
        color="primary"
        label="保存"
        @click="save"

      />
    </div>
    <Toolbar
      style="border-bottom: 1px solid #ccc"
      :editor="editorRef"
      :defaultConfig="toolbarConfig"
      :mode="mode"
    />
    <Editor
      style="height: 500px; overflow-y: hidden"
      v-model="formData.content"
      :defaultConfig="editorConfig"
      :mode="mode"
      @onCreated="handleCreated"
    />
  </div>
</template>

<style scoped>
.w-e-full-screen-container {
  z-index: 2000;
}
</style>
