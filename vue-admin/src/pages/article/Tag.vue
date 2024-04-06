<script setup>
import {ref} from "vue";
import {useQuasar} from "quasar";
import {useOperate, useTable} from "src/utils/useTable";
import TagForm from "./TagForm.vue";

const {
  params,
  pagination,
  isLoading,
  tableData,
  fetchData,
  refreshTable,
  clearTable,
} = useTable({
  apiUrl: "/admin/tag/page",
  inInt: false,
});

const columns = ref([
  {
    name: "name",
    label: "标签",
    field: "name",
    align: "center",
    required: true,
  },
  {name: "sort", label: "排序", field: "sort", align: "center"},
  {
    name: "operate",
    label: "操作",
    field: "id",
    align: "center",
    required: true,
  },
]);

const $q = useQuasar();

const addOrEdit = (id) => {
  $q.dialog({
    component: TagForm,
    componentProps: {
      id,
    },
  }).onOk(() => {
    refreshTable();
  });
};

const {remove} = useOperate();

</script>

<template>
  <div class="q-pa-md">
    <div class="">
      <q-form class="row">
        <q-input
          v-model="params.name"
          label="标签"
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
            @click="remove(row.id, '/admin/tag/remove', refreshTable)"
          />
        </q-td>
      </template>
    </q-table>
  </div>
</template>
