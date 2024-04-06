<script setup>
import {useQuasar} from "quasar";
import ResourceForm from "src/pages/permission/ResourceForm.vue";
import {useOperate, useTable} from "src/utils/useTable";
import {onMounted, ref} from "vue";
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
  apiUrl: "/admin/resource/page",
  inInt: false,
});

const columns = [
  {name: "menuName", label: "所属菜单", field: "menuName", align: "center"},
  {name: "name", label: "资源名称", field: "name", align: "center"},
  {name: "code", label: "资源编码", field: "code", align: "center"},
  {name: "sort", label: "排序", field: "sort", align: "center"},
  {name: "operate", label: "操作", field: "id", align: "center"},
];

const $q = useQuasar();

const addOrEdit = (id) => {
  $q.dialog({
    component: ResourceForm,
    componentProps: {
      id,
      menuId: menuId.value
    },
  }).onOk(() => {
    refreshTable();
  });
};

const {remove} = useOperate();

const treeData = ref([]);
onMounted(async () => {
  let res = await api.get("/admin/resource/menuTree");
  treeData.value = res.data
});
const menuId = ref(null)
const changeSelected = (val) => {
  params.value.menuId = val
  refreshTable();
}
</script>

<template>
  <div class="q-pa-md row no-wrap">
    <div class="col-2 q-pa-md">
      <q-tree
        :nodes="treeData"
        node-key="value"
        v-model:selected="menuId"
        no-selection-unset
        default-expand-all
        selected-color="primary"
        @update:selected="changeSelected"
      />
    </div>
    <div class="col-10">
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
        <template #top-right>
          <q-btn
            color="primary"
            label="新增"
            icon="add"
            @click="addOrEdit('')"
          />
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
              @click="remove(row.id, '/admin/resource/remove', refreshTable)"
            />
          </q-td>
        </template>
      </q-table>
    </div>
  </div>
</template>
