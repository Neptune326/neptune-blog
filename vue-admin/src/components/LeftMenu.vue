<script setup>
import LeftMenu from "components/LeftMenu.vue";

const props = defineProps({
  menus: {
    type: Array,
    default: () => [],
  },
});
</script>
<template>
  <div>
    <template v-for="menu in props.menus" :key="menu.id">
      <q-expansion-item
        v-if="menu.children"
        expand-separator
        :icon="menu.icon"
        :label="menu.name"
        expand-icon-class="text-primary"
        :content-inset-level="0.5"
        active-class="bg-blue-1 text-orange"
        default-opened
      >
        <LeftMenu :menus="menu.children" />
      </q-expansion-item>

      <q-item
        v-else
        clickable
        v-ripple
        :to="menu.route"
        active-class="bg-blue-1 text-orange"
      >
        <q-item-section avatar>
          <q-icon :name="menu.icon" />
        </q-item-section>
        <q-item-section>{{ menu.name }}</q-item-section>
      </q-item>
    </template>
  </div>
</template>
