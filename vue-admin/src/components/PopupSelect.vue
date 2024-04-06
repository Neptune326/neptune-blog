<script setup>

import {ref,computed,watch} from "vue";

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  label: {
    type: String,
    default: "选择"
  },
  options: {
    type: Array,
    default: () => []
  },
  limit: {
    type: Number,
    default: 10
  },
  nameKey: {
    type: String,
    default: "name"
  },
  valueKey: {
    type: String,
    default: "value"
},
});
const emits = defineEmits(["update:modelValue"]);

const dict = computed(() => {
  const result = {};
  props.options.forEach((item) => {
    result[item[props.valueKey]] = item[props.nameKey];
  });
  return result;
});

const input = ref('');
const showing = ref(false);

const filterOptions = computed(() => {
  let options = props.options;
  if (input.value !== '') {
    const needle = input.value.toLowerCase();
    options = options.filter(
      v => v.name.toLowerCase().indexOf(needle) > -1
    );
  }
  return options;
});

const selectItem = (item) => {
  let value = item[props.valueKey];
  let list = props.modelValue;
  let limit = props.limit;
  if (list.includes(value)) {
    list = list.filter(v => v !== value);
  } else {
    if(list.length >= limit){
      return;
    }
    list.push(value);
  }
  emits("update:modelValue", list);
};

watch(
  ()=>showing.value,
  (val)=>{
    if(!val){
      input.value = '';
    }
  }
);
</script>

<template>
  <div >
    <q-btn color="primary" :label="label" class="q-mr-sm">
      <q-menu v-model="showing" class="rounded-borders"  >
        <div class="q-ma-md">
          <div class="row no-wrap justify-between items-center q-mb-md">
            <q-input
              outlined
              v-model="input"
              label="请输入"
              dense
              class="full-width"
              :debounce="300"
            />
          </div>

          <q-separator/>

          <div class="q-mt-sm q-gutter-md">
            <q-btn
              v-for="(item,index) in filterOptions"
              :key="index"
              :label="item[props.nameKey]"
              :color="props.modelValue.includes(item[props.valueKey]) ? 'primary' : undefined"
              @click="selectItem(item)"
            />
          </div>
        </div>
      </q-menu>
      <q-tooltip>{{ `最多选择${props.limit}个` }}</q-tooltip>
    </q-btn>

    <q-chip
      v-for="key in props.modelValue"
      :key="key"
      removable
      @remove="selectItem({[props.valueKey]:key})"
      color="positive"
       text-color="white"
    class="q-mx-sm"
    >
      {{ dict[key] }}
    </q-chip>
  </div>
</template>

<style scoped>

</style>
