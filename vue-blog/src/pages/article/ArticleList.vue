<script setup>
import {ref, onMounted} from 'vue'
import {useQuasar, debounce} from 'quasar'
import {api} from 'boot/axios'
import {useRouter} from 'vue-router'

const $q = useQuasar();

const input = ref('');
const checkedCategory = ref('');
const page = ref({
  pageNo: 1,
  pageSize: 10,
  total: 0
});
const isLoading = ref(true);
const dataList = ref([]);
const fetchData = debounce(async () => {
  isLoading.value = true;
  const response = await api.get('/blog/article/page', {
    params: {
      pageNo: page.value.pageNo,
      pageSize: page.value.pageSize,
      search: input.value,
      categoryIds: checkedCategory.value
    }
  });
  const {records, totalPage} = response.data;
  dataList.value = records;
  page.value.total = totalPage;
  isLoading.value = false;

}, 500);

const jumpPage = (value) => {
  page.value.pageNo = value;
  fetchData();
}
const search = () => {
  fetchData();
}

const clear = () => {
  input.value = '';
  checkedCategory.value = '';
  page.value.pageNo = 1;
  fetchData();
}

const categoryList = ref([]);
const fetchCategory = async () => {
  const response = await api.get('/blog/article/categoryList');
  categoryList.value = response.data;
}

onMounted(() => {
  fetchCategory();
  fetchData();
})

const router = useRouter();
const viewArticle = (id) => {
  router.push(`/article/${id}`);
}
</script>

<template>
  <q-page class="q-pa-md" style="margin: auto 150px;">
    <div class="row no-wrap q-px-md q-pb-md">
      <q-input outlined dense v-model="input" class="col-5 q-mx-md"/>
      <q-select
        outlined
        dense
        emit-value
        map-options
        option-label="name"
        option-value="id"
        v-model="checkedCategory"
        :options="categoryList"
        label="分类"
        class="col-5 q-mx-xs"/>
      <q-space/>
        <q-btn icon="search" class="q-ml-sm" @click="search"/>
        <q-btn icon="remove" class="q-ml-sm" @click="clear"/>
    </div>

    <q-separator inset color="deep-orange" size="4px"/>

    <div class="row wrap q-pb-md ">

      <div
        v-for="data in dataList"
        :key="data.id"
        class="q-pa-md  col-sm-12 col-md-4 col-lg-3"
      >
        <q-card
          class="cursor-pointer article-card"
          @click="viewArticle(data.id)"
        >
          <q-img
            :src="data.cover"
            height="150px"
          >

          </q-img>

          <q-card-section>
            <div class="text-h5 neptune-text-one q-my-sm">{{ data.title }}</div>
            <div class="text-subtitle1 q-my-sm">by John Doe</div>
          </q-card-section>

          <q-card-actions align="right">
            <q-btn flat round color="red" icon="favorite"/>
            <q-btn flat round color="teal" icon="bookmark"/>
            <q-btn flat round color="primary" icon="share"/>
          </q-card-actions>
        </q-card>
      </div>

      <div class="q-pa-lg flex flex-center text-center full-width q-mt-lg" v-show="isLoading && dataList.length === 0">
        <svg id="b21613c9-2bf0-4d37-bef0-3b193d34fc5d" data-name="Layer 1" xmlns="http://www.w3.org/2000/svg" width="400" height="400" viewBox="0 0 647.63626 632.17383"><path d="M687.3279,276.08691H512.81813a15.01828,15.01828,0,0,0-15,15v387.85l-2,.61005-42.81006,13.11a8.00676,8.00676,0,0,1-9.98974-5.31L315.678,271.39691a8.00313,8.00313,0,0,1,5.31006-9.99l65.97022-20.2,191.25-58.54,65.96972-20.2a7.98927,7.98927,0,0,1,9.99024,5.3l32.5498,106.32Z" transform="translate(-276.18187 -133.91309)" fill="#f2f2f2"></path><path d="M725.408,274.08691l-39.23-128.14a16.99368,16.99368,0,0,0-21.23-11.28l-92.75,28.39L380.95827,221.60693l-92.75,28.4a17.0152,17.0152,0,0,0-11.28028,21.23l134.08008,437.93a17.02661,17.02661,0,0,0,16.26026,12.03,16.78926,16.78926,0,0,0,4.96972-.75l63.58008-19.46,2-.62v-2.09l-2,.61-64.16992,19.65a15.01489,15.01489,0,0,1-18.73-9.95l-134.06983-437.94a14.97935,14.97935,0,0,1,9.94971-18.73l92.75-28.4,191.24024-58.54,92.75-28.4a15.15551,15.15551,0,0,1,4.40966-.66,15.01461,15.01461,0,0,1,14.32032,10.61l39.0498,127.56.62012,2h2.08008Z" transform="translate(-276.18187 -133.91309)" fill="#3f3d56"></path><path d="M398.86279,261.73389a9.0157,9.0157,0,0,1-8.61133-6.3667l-12.88037-42.07178a8.99884,8.99884,0,0,1,5.9712-11.24023l175.939-53.86377a9.00867,9.00867,0,0,1,11.24072,5.9707l12.88037,42.07227a9.01029,9.01029,0,0,1-5.9707,11.24072L401.49219,261.33887A8.976,8.976,0,0,1,398.86279,261.73389Z" transform="translate(-276.18187 -133.91309)" fill="#1976D2"></path><circle cx="190.15351" cy="24.95465" r="20" fill="#1976D2"></circle><circle cx="190.15351" cy="24.95465" r="12.66462" fill="#fff"></circle><path d="M878.81836,716.08691h-338a8.50981,8.50981,0,0,1-8.5-8.5v-405a8.50951,8.50951,0,0,1,8.5-8.5h338a8.50982,8.50982,0,0,1,8.5,8.5v405A8.51013,8.51013,0,0,1,878.81836,716.08691Z" transform="translate(-276.18187 -133.91309)" fill="#e6e6e6"></path><path d="M723.31813,274.08691h-210.5a17.02411,17.02411,0,0,0-17,17v407.8l2-.61v-407.19a15.01828,15.01828,0,0,1,15-15H723.93825Zm183.5,0h-394a17.02411,17.02411,0,0,0-17,17v458a17.0241,17.0241,0,0,0,17,17h394a17.0241,17.0241,0,0,0,17-17v-458A17.02411,17.02411,0,0,0,906.81813,274.08691Zm15,475a15.01828,15.01828,0,0,1-15,15h-394a15.01828,15.01828,0,0,1-15-15v-458a15.01828,15.01828,0,0,1,15-15h394a15.01828,15.01828,0,0,1,15,15Z" transform="translate(-276.18187 -133.91309)" fill="#3f3d56"></path><path d="M801.81836,318.08691h-184a9.01015,9.01015,0,0,1-9-9v-44a9.01016,9.01016,0,0,1,9-9h184a9.01016,9.01016,0,0,1,9,9v44A9.01015,9.01015,0,0,1,801.81836,318.08691Z" transform="translate(-276.18187 -133.91309)" fill="#1976D2"></path><circle cx="433.63626" cy="105.17383" r="20" fill="#1976D2"></circle><circle cx="433.63626" cy="105.17383" r="12.18187" fill="#fff"></circle></svg>
      </div>

    </div>

    <div class="q-pa-lg flex flex-center" v-show="dataList.length > 0">
      <q-pagination
        v-model="page.pageNo"
        color="deep-orange"
        :max="page.total"
        :max-pages="5"
        boundary-links
        boundary-numbers
        direction-links
        size="lg"
        @update:model-value="jumpPage"
      />
    </div>

  </q-page>
</template>

<style scoped>
.article-card:hover {
  box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.4);
}
</style>
