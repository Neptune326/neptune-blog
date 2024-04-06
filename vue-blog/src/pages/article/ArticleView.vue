<script setup>
import { ref,onMounted } from 'vue'
import {api} from 'boot/axios'
import { useQuasar } from 'quasar'
import { useRoute } from 'vue-router'
import '@wangeditor/editor/dist/css/style.css'

const $q = useQuasar();
const route = useRoute();
const id = route.params.id;

const data = ref({});
const loading = ref(true);

onMounted(() => {
  let startTimestamp = new Date().getTime();
  api.get('/blog/article/info', {
    params: {
      id: id
    }
  }).then((res) => {
    data.value = res.data;
  }).finally(() => {
    let endTimestamp = new Date().getTime();
    let diff = endTimestamp - startTimestamp;
    if (diff < 1000) {
      setTimeout(() => {
        loading.value = false;
      }, 1000 - diff);
    } else {
      loading.value = false;
    }
  });
});

</script>

<template>
  <div class="q-pa-lg ">

    <div class="   neptune-article-content"  >
      <h3>{{ data.title }}</h3>
      <p class="text-grey-8 text-right">{{ data.createTime }}</p>

      <q-img
        :src="data.cover"
        :alt="data.title"
        style="width: 100%;"
      />

      <div class="w-e-text-container neptune-article-content" >
        <div v-html="data.content" data-slate-editor></div>
      </div>
    </div>

    <q-inner-loading :showing="loading">
      <q-spinner-cube size="200px" color="deep-orange" />
    </q-inner-loading>

  </div>
</template>

<style scoped>
.neptune-article-content{
  max-width: 80%;
  min-width: 50%;
  margin: 0 auto;
}

.editor-content-view {
  border: 3px solid #ccc;
  border-radius: 5px;
  padding: 0 10px;
  margin-top: 20px;
  overflow-x: auto;
}

.editor-content-view p,
.editor-content-view li {
  white-space: pre-wrap; /* 保留空格 */
}

.editor-content-view blockquote {
  border-left: 8px solid #d0e5f2;
  padding: 10px 10px;
  margin: 10px 0;
  background-color: #f1f1f1;
}

.editor-content-view code {
  font-family: monospace;
  background-color: #eee;
  padding: 3px;
  border-radius: 3px;
}
.editor-content-view pre>code {
  display: block;
  padding: 10px;
}

.editor-content-view table {
  border-collapse: collapse;
}
.editor-content-view td,
.editor-content-view th {
  border: 1px solid #ccc;
  min-width: 50px;
  height: 20px;
}
.editor-content-view th {
  background-color: #f1f1f1;
}

.editor-content-view ul,
.editor-content-view ol {
  padding-left: 20px;
}

.editor-content-view input[type="checkbox"] {
  margin-right: 5px;
}
</style>
