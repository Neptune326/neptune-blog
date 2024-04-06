<script setup>
import "@wangeditor/editor/dist/css/style.css"; // 引入 css
import {ref, onBeforeUnmount, onMounted, shallowRef} from "vue";
import {Editor, Toolbar} from "@wangeditor/editor-for-vue";
import {useDialogPluginComponent, useQuasar} from "quasar";
import ImgUpload from "src/components/ImgUpload.vue";
import {api} from "boot/axios";
import PopupSelect from "components/PopupSelect.vue";

const props = defineProps({
  id: {
    type: String,
    default: "",
  },
});

const $q = useQuasar();
defineEmits([...useDialogPluginComponent.emits]);

const {dialogRef, onDialogHide, onDialogOK, onDialogCancel} =
  useDialogPluginComponent();


const mode = "default"; // 或 'simple
// 编辑器实例，必须用 shallowRef
const editorRef = shallowRef();

const toolbarConfig = {};
const editorConfig = {
  placeholder: "请输入内容...",
  MENU_CONF: {
    uploadImage: {
      server: process.env.APP_BASE_URL + '/admin/attachment/upload',
      fieldName: 'file',
      uploadFileName: 'file',
      uploadImgMaxSize: 10 * 1024 * 1024,
      uploadImgMaxLength: 1,
      allowedFileTypes: ['image/*'],
      timeout: 5 * 1000,
      headers: {
        Authorization: 'Bearer ' + $q.localStorage.getItem('token')
      },
      onSuccess(file, res) {
        $q.notify({
          message: '上传成功',
          color: "positive",
        });
      },
      onFailed(file, res) {
        $q.notify({
          message: '上传失败',
          color: "negative",
        });
      },
      customInsert(res, insertFn) {
        let url = res.data;
        insertFn(url, '', '')
      },
    }
  },
};

const handleChange = () => {
  // let reg = /<h[1-6][\s\S]*?h[1-6]>/g;
  // let hArr = formData.value.content.match(reg);//所有的h标签
  // let anchorArr = [];//添加锚链接的标题
  // if (hArr) {
  //   hArr.forEach((item, i) => {
  //     // 标题
  //     let text = item.replace(/<[^>]+>/g, "");
  //     anchorArr.push(`<a href="#miao${i}">${text}</a>`);
  //   });
  // }
  // formData.value.anchor = anchorArr;
}

// 组件销毁时，也及时销毁编辑器
onBeforeUnmount(() => {
  const editor = editorRef.value;
  if (editor == null) return;
  editor.destroy();
});

const handleCreated = (editor) => {
  editorRef.value = editor; // 记录 editor 实例，重要！
};

const myForm = ref(null);
const formData = ref({
  title: "",
  summary: "",
  cover: "",
  categoryId: "",
  tagIds: [],
  isTop: "0",
  isOriginal: "0",
  originalUrl: "",
  content: "",
  anchor: []

})

const categoryOptions = ref([]);
const tagOptions = ref([]);

onMounted(() => {
  if (props.id) {
    api.get('/admin/article/info', {
      params: {
        id: props.id
      }

    }).then((res) => {
      formData.value = res.data;
    });
  }
  api.get('/admin/category/list').then((res) => {
    categoryOptions.value = res.data;
    if (!props.id) {
      formData.value.categoryId = categoryOptions.value[0].id;
    }
  });
  api.get('/admin/tag/list').then((res) => {
    tagOptions.value = res.data;
  });
});

const saveOrUpdate = () => {
  myForm.value.validate().then((success) => {
    if (!success) {
      return;
    }
    //设置锚点
    let submitData = formData.value;
    // let anchor = submitData.anchor;
    // let content = JSON.parse(JSON.stringify(submitData));
    // if (anchor) {
    //   anchor.forEach((item, i) => {
    //     // 替换文章标签
    //     let itemArr = item.split("");
    //     itemArr.splice(3, 0, ` id="miao${i}"`);
    //     let newItem = itemArr.join("");
    //     content = content.replace(item, newItem);
    //   });
    //   submitData.content = content;
    // }

    let url = props.id ? '/admin/article/update' : '/admin/article/save';
    api.post(url, submitData).then((res) => {
      if (res.status) {
        $q.notify({
          message: '保存成功',
          color: "positive",
        });
        onDialogOK(res);
      }
    });
  });
};

const validError = (ref) => {
  console.log(ref);
  ref.$el.scrollIntoView({behavior: 'smooth'})
};
</script>
<template>
  <q-dialog maximized persistent ref="dialogRef" @hide="onDialogHide">
    <q-card class="no-shadow">
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6">Article</div>
        <q-space/>
        <q-btn icon="close" flat round v-close-popup/>
      </q-card-section>
      <q-card-section>
        <q-form ref="myForm" class="q-ma-md scroll" style="min-height: 90vh" @validation-error="validError">

          <q-list class="">
            <q-item>
              <q-item-section>
                <q-input v-model="formData.title" label="标题" outlined
                         :rules="[val => !!val || '请输入标题']" :debounce="300"/>
              </q-item-section>
              <q-item-section side>
                <ImgUpload v-model="formData.cover" label="上传封面"/>
              </q-item-section>
            </q-item>
            <q-item>
              <q-item-section>
                <q-input v-model="formData.summary" label="摘要" outlined/>
              </q-item-section>
            </q-item>
            <q-item>
              <q-item-section>
                <q-select
                  v-model="formData.categoryId"
                  label="分类"
                  outlined
                  emit-value
                  map-options
                  option-label="name"
                  option-value="id"
                  :options="categoryOptions"
                  :rules="[val => !!val || '请选择分类']"
                />
              </q-item-section>
            </q-item>
            <q-item>
              <q-item-section>
                <PopupSelect v-model="formData.tagIds" label="选择标签" value-key="id" :options="tagOptions"
                             :limit="5"/>
              </q-item-section>
            </q-item>
            <q-item>
              <q-item-section side>
                是否置顶
              </q-item-section>
              <q-toggle v-model="formData.isTop" true-value="1" false-value="0">
                <q-tooltip>置顶文章将在首页展示</q-tooltip>
              </q-toggle>
            </q-item>
            <q-item>
              <q-item-section side>
                是否转载
              </q-item-section>
              <q-toggle v-model="formData.isOriginal" true-value="1" false-value="0">
                <q-tooltip>原创文章将在首页展示</q-tooltip>
              </q-toggle>

              <q-item-section class="q-ml-md">
                <q-input v-model="formData.originalUrl" label="原文地址" outlined/>
              </q-item-section>
            </q-item>

            <q-item>
              <q-item-section>

                <Toolbar
                  style="border-bottom: 1px solid #ccc"
                  :editor="editorRef"
                  :defaultConfig="toolbarConfig"
                  :mode="mode"
                />
                <Editor
                  style=" height: 500px; overflow-y: hidden"
                  v-model="formData.content"
                  :defaultConfig="editorConfig"
                  :mode="mode"
                  @onCreated="handleCreated"
                  @onChange="handleChange"
                />
              </q-item-section>
            </q-item>
          </q-list>

        </q-form>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn
          color="primary"
          label="保存"
          @click="saveOrUpdate"
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<style scoped>
.w-e-full-screen-container {
  z-index: 2000;
}
</style>
