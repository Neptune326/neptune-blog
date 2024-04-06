<script setup>
import {useDialogPluginComponent} from "quasar";
import {computed} from "vue";
import "highlight.js/styles/stackoverflow-light.css";
import "highlight.js/lib/common";
import hljsVuePlugin from '@highlightjs/vue-plugin'

const highlightjs = hljsVuePlugin.component

const props = defineProps({
  columns: {type: Array, default: () => []},
  row: {
    type: Object, default: () => {
    }
  },
});

defineEmits([
  // REQUIRED; 需要明确指出
  // 组件通过 useDialogPluginComponent() 暴露哪些事件
  ...useDialogPluginComponent.emits,
]);

const {dialogRef, onDialogHide, onDialogOK, onDialogCancel} =
  useDialogPluginComponent();

const codeArr = ["requestParam", "responseData", "exceptionInfo"];
const defaultColumns = computed(() => {
  return props.columns.filter((column) => !codeArr.includes(column.name));
});

const codeColumns = computed(() => {
  return props.columns.filter((column) => codeArr.includes(column.name));
});

const formatCode = (name, code) => {
  if (name === "exceptionInfo") return code;
  return JSON.stringify(JSON.parse(code), null, 2);
};
</script>
<template>
  <q-dialog ref="dialogRef" @hide="onDialogHide">
    <q-card class="q-dialog-plugin" style="min-width: 75vw">
      <q-card-section>
        <q-list bordered>
          <q-item
            v-for="column in defaultColumns"
            :key="column.name"
            tag="label"
            v-ripple
          >
            <q-item-section>
              <q-item-label>{{ column.label }}</q-item-label>
            </q-item-section>
            <q-item-section side>
              <q-item-label>{{ row[column.name] }}</q-item-label>
            </q-item-section>
          </q-item>

          <q-expansion-item
            v-for="column in codeColumns"
            :key="column.name"
            expand-separator
            :label="column.label"
            default-opened
          >
            <q-card>
              <q-card-section>

                <highlightjs autodetect :code="formatCode(column.name, row[column.name])"/>

              </q-card-section>
            </q-card>
          </q-expansion-item>
        </q-list>
      </q-card-section>
    </q-card>
  </q-dialog>
</template>

