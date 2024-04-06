<script setup>
import {ref, onMounted} from 'vue'
import {useQuasar, debounce} from 'quasar'
import {api} from 'boot/axios'
import {useRouter} from 'vue-router'

const $q = useQuasar();

const columns = [
  {
    name: 'desc',
    required: true,
    label: 'Dessert (100g serving)',
    align: 'left',
    field: row => row.name,
    format: val => `${val}`,
    sortable: true
  },
  {name: 'calories', align: 'center', label: 'Calories', field: 'calories', sortable: true},
  {name: 'fat', label: 'Fat (g)', field: 'fat', sortable: true},
  {name: 'carbs', label: 'Carbs (g)', field: 'carbs'}
]
const rows = ref([])
const filter = ref({
  title: '',
  categoryId: ''
})
const loading = ref(false)
const pagination = ref({
  page: 1,
  rowsPerPage: 5,
  rowsNumber: 0
});

const fetchData = (props) => {

  loading.value = true
  const {pagination: {page, rowsPerPage}, filter: {title, categoryId}} = props;

  api.get('/blog/article/page', {
    params: {
      pageNumber: page,
      pageSize: rowsPerPage,
      title,
      categoryId
    }
  }).then(response => {
    const {records, totalRow} = response.data;
    rows.value = records;
    pagination.value.page = page
    pagination.value.rowsPerPage = rowsPerPage
    pagination.value.rowsNumber = totalRow;
  }).finally(() => {
    loading.value = false
  })
}

const categoryList = ref([]);
const fetchCategory = async () => {
  const response = await api.get('/blog/article/categoryList');
  categoryList.value = response.data;
}

onMounted(() => {
  fetchData({
    pagination: pagination.value,
    filter: {}
  })
  fetchCategory();
})

const router = useRouter();
const viewArticle = (id) => {
  router.push(`/article/${id}`);
}
</script>

<template>
  <q-page class="q-pa-md q-mx-xl-xl">
    <q-table
      grid
      hide-header
      :rows="rows"
      :columns="columns"
      row-key="id"
      v-model:pagination="pagination"
      :loading="loading"
      :filter="filter"
      :rows-per-page-options="[10, 20,50,100]"
      @request="fetchData"
    >
      <template v-slot:top>
        <q-space/>
        <q-input clearable dense debounce="500" v-model="filter.title" label="标题" class="q-mx-md"/>
        <q-select
          clearable
          dense
          emit-value
          map-options
          option-label="name"
          option-value="id"
          v-model="filter.categoryId"
          :options="categoryList"
          label="分类"
          style="min-width: 160px;"
          class="q-mx-md"/>
      </template>

      <template v-slot:item="{row}">
        <div class="q-pa-xs col-xs-12 col-sm-6 col-md-4">
          <q-card
            class="cursor-pointer article-card q-ma-md"
            @click="viewArticle(row.id)"
          >
            <q-img
              :src="row.cover"
              height="150px"
            >

            </q-img>

            <q-card-section>
              <div class="text-h5 neptune-text-one q-my-sm">{{ row.title }}</div>
              <div class="text-subtitle1 q-my-sm">by John Doe</div>
            </q-card-section>

            <q-card-actions align="right">
              <q-btn flat round color="red" icon="favorite"/>
              <q-btn flat round color="teal" icon="bookmark"/>
              <q-btn flat round color="primary" icon="share"/>
            </q-card-actions>
          </q-card>
        </div>
      </template>

      <template v-slot:no-data>
        <q-tr class="full-width row justify-center items-center q-mt-lg q-pt-lg">
          <q-td>
            <q-icon name="sentiment_very_dissatisfied" size="100px" color="grey-8"/>
          </q-td>
        </q-tr>
      </template>

    </q-table>
  </q-page>
</template>

<style scoped>
.article-card:hover {
  box-shadow: 0 0 20px 0 rgba(0, 0, 0, 0.4);
}
</style>
