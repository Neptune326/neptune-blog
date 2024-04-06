<script setup>
import {useQuasar} from "quasar";
import UserForm from "src/pages/user/UserForm.vue";
import {useOperate, useTable} from "src/utils/useTable";

const {
  params,
  pagination,
  isLoading,
  tableData,
  fetchData,
  refreshTable,
  clearTable,
} = useTable({
  apiUrl: "/admin/user/page",
  inInt: false,
});

const columns = [
  {name: "userName", label: "用户名", field: "userName", align: "center"},
  {name: "nickName", label: "昵称", field: "nickName", align: "center"},
  {name: "sort", label: "排序", field: "sort", align: "center"},
  {name: "operate", label: "操作", field: "id", align: "center"},
];

const $q = useQuasar();

const addOrEdit = (id) => {
  $q.dialog({
    component: UserForm,
    componentProps: {
      id,
    },
  })
    .onOk(() => {
      refreshTable();
    })
    .onDismiss(() => {
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
          v-model="params.userName"
          label="用户名称"
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
            @click="remove(row.id, '/admin/user/remove', refreshTable)"
          />
        </q-td>
      </template>
    </q-table>
  </div>
</template>
