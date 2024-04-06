<script setup>
import IconSelect from 'src/components/IconSelect.vue'
import {ref} from 'vue'
import AvatarUpload from "components/AvatarUpload.vue";
import PopupSelect from "components/PopupSelect.vue";

const icon = ref(null);

const select = ref(null);
const stringOptions = [
  'Google', 'Facebook', 'Twitter', 'Apple', 'Oracle'
]
const filterOptions = ref(stringOptions)
const createValue = (val, done) => {

  if (val.length > 0) {
    if (!stringOptions.includes(val)) {
      stringOptions.push(val)
    }
    done(val, 'toggle')
  }
}

const filterFn = (val, update) => {
  update(() => {
    if (val === '') {
      filterOptions.value = stringOptions
    } else {
      const needle = val.toLowerCase()
      filterOptions.value = stringOptions.filter(
        v => v.toLowerCase().indexOf(needle) > -1
      )
    }
  })
}


//const avatar = ref("http://47.116.112.58:9000/neptune/20240218/1759149366897152000.png")
const avatar = ref("");

const select1 = ref([]);
</script>

<template>
  <div class="q-ma-md q-pa-md">
    <div>
      <p>IconSelect</p>
      <IconSelect v-model="icon" label="图标" clearable/>
      icon:{{ icon }}
    </div>

    <q-space/>

    <div>
      <p>QSelect</p>
      <div class="q-pa-md">
        <q-select
          filled
          v-model="select"
          use-input
          use-chips
          input-debounce="0"
          @new-value="createValue"
          :options="filterOptions"
          @filter="filterFn"
          style="width: 250px"
        />
      </div>
    </div>

    <q-space/>

    <div>
<!--      <AvatarUpload v-model="avatar"/>-->
    </div>

    <q-separator/>
    <div>
      PopupSelect
      <br/>
      <PopupSelect v-model="select1" label="选择" :options="[
        {name: 'Google', value: 'google'},
        {name: 'Facebook', value: 'facebook'},
        {name: 'Twitter', value: 'twitter'},
        {name: 'Apple', value: 'apple'},
        {name: 'Oracle', value: 'oracle'}
      ]"/>
    </div>
  </div>
</template>

<style scoped>

</style>
