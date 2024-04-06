<script setup>
import { ref, defineProps, defineEmits } from "vue";

const props = defineProps({
  options: {
    type: Array,
    required: true,
    default: () => [],
  },
});
const emits = defineEmits(["search"]);
const options = ref(props.options);

const params = ref({});

const search = () => {
  emits("search", params.value);
};

const reset = () => {
  params.value = {};
  emits("search", {});
};
</script>

<template>
  <div class="q-my-md">
    <q-form class="row">
      <div class="col-5 q-pa-md" v-for="(opt, index) in options" :key="index">
        <q-input
          v-if="opt.type === 'text' || !opt.type"
          v-model="params[opt.key]"
          :label="opt.label"
          outlined
          placeholder="Search"
          class="col-5"
          dense
        />
        <q-select
          v-else-if="opt.type === 'select'"
          v-model="params[opt.key]"
          :label="opt.label"
          outlined
          :options="opt.options"
          label="Search By"
          class="col-5"
          dense
        />
      </div>
      <div class="col-1 q-pa-sm self-center">
        <q-btn
          color="primary"
          label="查询"
          class="full-width"
          @click="search"
        />
      </div>
      <div class="col-1 q-pa-sm self-center">
        <q-btn color="primary" label="清除" class="full-width" @click="reset" />
      </div>
    </q-form>
  </div>
</template>

<style scoped></style>
