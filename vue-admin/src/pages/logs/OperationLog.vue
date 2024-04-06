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
  apiUrl: "/admin/operationLog/page",
  inInt: false,
});

const columns = [
  {name: "optModule", label: "操作模块", field: "optModule", align: "center"},
  {name: "optType", label: "操作类型", field: "optType", align: "center"},
  {name: "optUri", label: "操作URI", field: "optUri", align: "center"},
  {name: "optMethod", label: "操作方法", field: "optMethod", align: "center"},
  {
    name: "requestMethod",
    label: "请求方式",
    field: "requestMethod",
    align: "center",
  },
  {name: "ipAddress", label: "操作IP", field: "ipAddress", align: "center"},
  {name: "ipSource", label: "操作地址", field: "ipSource", align: "center"},
  {
    name: "handleTime",
    label: "处理时长",
    field: "handleTime",
    align: "center",
  },
  {name: "operate", label: "操作", field: "id", align: "center"},
];

const $q = useQuasar();

const viewLog = (row) => {
  $q.dialog({
    component: LogDetail,
    componentProps: {
      row,
      columns: [
        {label: "创建时间", name: "createTime"},
        {label: "操作人", name: "createUser"},
        {label: "操作模块", name: "optModule"},
        {label: "操作类型", name: "optType"},
        {label: "操作URI", name: "optUri"},
        {label: "操作方法", name: "optMethod"},
        {label: "请求方式", name: "requestMethod"},
        {label: "操作IP", name: "ipAddress"},
        {label: "操作地址", name: "ipSource"},
        {label: "处理时长", name: "handleTime"},
        {label: "请求参数", name: "requestParam"},
        {label: "响应结果", name: "responseData"},
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
