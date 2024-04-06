<script setup>
import {useQuasar} from "quasar";
import RoleForm from "src/pages/permission/RoleForm.vue";
import RoleMenu from "src/pages/permission/RoleMenu.vue";
import RoleResource from "src/pages/permission/RoleResource.vue";
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
  apiUrl: "/admin/role/page",
  inInt: false,
});

const columns = [
  {name: "name", label: "角色名称", field: "name", align: "center"},
  {name: "code", label: "角色编码", field: "code", align: "center"},
  {name: "sort", label: "排序", field: "sort", align: "center"},
  {name: "operate", label: "操作", field: "id", align: "center"},
];

const $q = useQuasar();

const addOrEdit = (id) => {
  $q.dialog({
    component: RoleForm,
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

const editMenu = (id) => {
  $q.dialog({
    component: RoleMenu,
    componentProps: {
      id,
    },
  });
};

const editResource = (id) => {
  $q.dialog({
    component: RoleResource,
    componentProps: {
      id,
    },
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
          label="角色名称"
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
            color="primary"
            label="菜单"
            icon="edit"
            @click="editMenu(row.id)"
          />
          <q-btn
            flat
            color="primary"
            label="资源"
            icon="edit"
            @click="editResource(row.id)"
          />
          <q-btn
            flat
            color="red"
            label="删除"
            icon="delete"
            @click="remove(row.id, '/admin/role/remove', refreshTable)"
          />
        </q-td>
      </template>
    </q-table>
  </div>
</template>
