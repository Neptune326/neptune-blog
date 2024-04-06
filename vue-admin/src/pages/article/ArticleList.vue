<script setup>
import TableSearch from "../../components/TableSearch.vue";

import {useTable} from "src/utils/useTable";

const {pagination, isLoading, tableData, fetchData} = useTable();

const columns = [
  {
    name: "id",
    label: "id",
    field: "id",
  },
  {
    name: "userId",
    label: "userId",
    field: "userId",
  },
  {
    name: "title",
    label: "title",
    field: "title",
  },
  {
    name: "body",
    label: "body",
    field: "body",
  },
]

</script>

<template>
  <div class="q-pa-md">
    <TableSearch :options="[{type:'text'}]" @search="fetchData"/>

    <q-table
      title="Treats"
      :rows="tableData"
      :columns="columns"
      row-key="id"
      v-model:pagination="pagination"
      :loading="isLoading"
      @request="fetchData"
      binary-state-sort
    >
      <template v-slot:top-right>
        <q-input
          borderless
          dense
          debounce="300"
          placeholder="Search"
        >
          <template v-slot:append>
            <q-icon name="search"/>
          </template>
        </q-input>
      </template>
    </q-table>
  </div>
</template>
