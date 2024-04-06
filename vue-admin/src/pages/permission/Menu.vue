<script setup>
import {ref} from "vue";
import {useQuasar} from "quasar";
import MenuForm from "src/pages/permission/MenuForm.vue";
import {useOperate, useTable} from "src/utils/useTable";
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
  apiUrl: "/admin/menu/page",
  inInt: false,
});

const columns = ref([
  {
    name: "name",
    label: "菜单名称",
    field: "name",
    align: "center",
    required: true,
  },
  {name: "code", label: "菜单编码", field: "code", align: "center"},
  {name: "parentName", label: "父菜单", field: "parentName", align: "center"},
  {name: "icon", label: "菜单图标", field: "icon", align: "center"},
  {name: "sort", label: "菜单排序", field: "sort", align: "center"},
  {name: "isHidden", label: "显示/隐藏", field: "isHidden", align: "center"},
  {name: "route", label: "菜单路由", field: "route", align: "center"},
  {
    name: "componentName",
    label: "组件名称",
    field: "componentName",
    align: "center",
  },
  {
    name: "componentPath",
    label: "组件路径",
    field: "componentPath",
    align: "center",
  },
  {
    name: "operate",
    label: "操作",
    field: "id",
    align: "center",
    required: true,
  },
]);

const visibleColumns = ref([
  "name",
  "code",
  "parentName",
  "icon",
  "sort",
  "isHidden",
  "route",
  "componentName",
  "componentPath",
  "operate",
]);

const $q = useQuasar();

const addOrEdit = (id) => {
  $q.dialog({
    component: MenuForm,
    componentProps: {
      id,
    },
  }).onOk(() => {
    refreshTable();
  });
};

const {remove} = useOperate();

const switchVisible = (id) => {
  api
    .get("/admin/menu/switchVisible", {params: { id}})
    .then(() => {
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
          v-model="params.name"
          label="菜单名称"
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
      :visible-columns="visibleColumns"
    >
      <template #top-right>
        <q-btn color="primary" label="新增" icon="add" @click="addOrEdit('')"/>

        <q-select
          class="q-ml-md"
          v-model="visibleColumns"
          multiple
          outlined
          dense
          options-dense
          :display-value="$q.lang.table.columns"
          emit-value
          map-options
          :options="columns"
          option-value="name"
          options-cover
          style="min-width: 100px"
        />
      </template>
      <template #body-cell-icon="{ value }">
        <q-td class="q-pa-md q-gutter-sm text-center">
          <q-icon v-if="value" :name="value" size="md"/>
        </q-td>
      </template>
      <template #body-cell-isHidden="{ value, row }">
        <q-td class="q-pa-md q-gutter-sm text-center">
          <q-toggle
            :model-value="value"
            checked-icon="check"
            color="green"
            unchecked-icon="clear"
            true-value="0"
            false-value="1"
            @update:model-value="switchVisible(row.id)"
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
            @click="remove(row.id, '/admin/menu/remove', refreshTable)"
          />
        </q-td>
      </template>
    </q-table>
  </div>
</template>
