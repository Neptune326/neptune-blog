<script setup>
import {useQuasar} from "quasar";
import {useTable} from "src/utils/useTable";
import LogDetail from "./LogDetail.vue";

const {
  params,
  pagination,
  isLoading,
  tableData,
  fetchData,
  refreshTable,
  clearTable,
} = useTable({
  apiUrl: "/admin/exceptionLog/page",
  inInt: false,
});

const columns = [
  {
    label: "创建时间",
    name: "createTime",
    field: "createTime",
    align: "center",
  },
  {label: "异常URI", name: "optUri", field: "optUri", align: "center"},
  {label: "异常方法", name: "optMethod", field: "optMethod", align: "center"},
  {
    label: "请求方式",
    name: "requestMethod",
    field: "requestMethod",
    align: "center",
  },
  {label: "操作IP", name: "ipAddress", field: "ipAddress", align: "center"},
  {label: "操作地址", name: "ipSource", field: "ipSource", align: "center"},


  {label: "操作", name: "operate", field: "id", align: "center"},
];

const $q = useQuasar();

const viewLog = (row) => {
  $q.dialog({
    component: LogDetail,
    componentProps: {
      row,
      columns: [
        {label: "创建时间", name: "createTime",},
        {label: "异常URI", name: "optUri",},
        {label: "异常方法", name: "optMethod",},
        {label: "请求方式", name: "requestMethod",},
        {label: "操作IP", name: "ipAddress"},
        {label: "操作地址", name: "ipSource"},
        {label: "请求参数", name: "requestParam",},
        {label: "异常", name: "exceptionInfo",},
      ],
    },
  });
};
</script>

<template>
  <div class="q-pa-md">
    <div class="">
      <q-form class="row">
        <q-input
          v-model="params.name"
          label="资源名称"
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
      <template #body-cell-operate="{ row }">
        <q-td class="q-pa-md q-gutter-sm text-center">
          <q-btn
            flat
            color="primary"
            label="查看"
            icon="visibility"
            @click="viewLog(row)"
          />
        </q-td>
      </template>
    </q-table>
  </div>
</template>
