<script setup>
import {useQuasar} from "quasar";
import {useOperate, useTable} from "src/utils/useTable";
import ArticleForm from "./ArticleForm.vue";
import {api} from "src/boot/axios";

const {
  params,
  pagination,
  isLoading,
  tableData,
  fetchData,
  refreshTable,
  clearTable,
} = useTable({
  apiUrl: "/admin/article/page",
  inInt: false,
});

const columns = [
  {name: "title", label: "标题", field: "title", align: "center"},
  {name: "createTime", label: "创建时间", field: "createTime", align: "center"},
  {name: "cover", label: "封面", field: "cover", align: "center"},
  {name:"categoryName", label:"分类", field:"categoryName", align:"center"},
  {name:"tagNames", label:"标签", field:"tagNames", align:"center"},
  {name: "isTop", label: "是否置顶", field: "isTop", align: "center"},
  {name: "isOriginal", label: "是否原创", field: "isOriginal", align: "center"},
  {name: "quantity", label: "阅读量", field: "quantity", align: "center"},
  {name: "operate", label: "操作", field: "id", align: "center"},
];

const $q = useQuasar();

const addOrEdit = (id) => {
  $q.dialog({
    component: ArticleForm,
    componentProps: {
      id,
    },
  }).onOk(() => {
    refreshTable();
  });
};

const {remove} = useOperate();

const updateTop = (id) => {
  api.get("/admin/article/switchTop", {params:{id}}).then(() => {
    $q.notify({message: "修改成功", color: "positive"});
    refreshTable();
  });
};


</script>

<template>
  <div class="q-pa-md">
    <div class="">
      <q-form class="row">
        <q-input
          v-model="params.title"
          label="标题"
          outlined
          class="col-4 q-pa-md"
          dense
        />
        <div class="q-pa-md q-gutter-sm">
          <q-btn color="primary" label="查询" @click="refreshTable"/>
          <q-btn color="primary" label="清除" @click="clearTable"/>
        </div>
      </q-form>
    </div>

    <q-table
      bordered
      :rows="tableData"
      :columns="columns"
      row-key="id"
      v-model:pagination="pagination"
      :loading="isLoading"
      @request="fetchData"
    >
      <template #top-right>
        <q-btn color="primary" label="新增" icon="add" @click="addOrEdit('')"/>
      </template>
      <template #body-cell-categoryName="{value}">
        <q-td class="q-pa-md q-gutter-sm text-center">
          {{ value }}
        </q-td>
      </template>
      <template #body-cell-tagNames="{value}">
        <q-td class="q-pa-md q-gutter-sm text-center ">
          <div class="row wrap justify-center"  v-if="!!value && value.length > 0">

            <q-chip v-for="tag in value" :key="tag" :label="tag" />
          </div>
        </q-td>
      </template>
      <template #body-cell-cover="{ row }">
        <q-td class="q-pa-md q-gutter-sm">
<!--          <q-img
            fit="cover"
            :src="row.cover"
            width="200px"
            height="100px"
            spinner-color="white"
          />-->
        </q-td>
      </template>
      <template #body-cell-isTop="{ row }">
        <q-td class="q-pa-md q-gutter-sm text-center">
          <q-toggle
            v-model="row.isTop"
            true-value="1"
            false-value="0"
            @update:model-value="updateTop(row.id)"
          />
        </q-td>
      </template>
      <template #body-cell-isOriginal="{ row }">
        <q-td class="q-pa-md q-gutter-sm text-center">
          <q-chip
            :label="row.isOriginal === '1' ? '转载' : '原创'"
            :color="row.isOriginal === '1' ? 'secondary' : 'primary'"
          />
        </q-td>
      </template>

      <template #body-cell-operate="{ row }">
        <q-td class="q-pa-md q-gutter-sm text-center">
          <q-btn
            flat
            color="primary"
            label="编辑"
            icon="edit"
            @click="addOrEdit(row.id)"
          />
          <q-btn
            flat
            color="red"
            label="删除"
            icon="delete"
            @click="remove(row.id, '/admin/article/remove', refreshTable)"
          />
        </q-td>
      </template>
    </q-table>
  </div>
</template>
