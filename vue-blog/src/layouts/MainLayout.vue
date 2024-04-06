<script setup>
import { ref } from 'vue'
import { useQuasar } from 'quasar'

const $q = useQuasar();
console.log($q);

const rightDrawerOpen = ref(false);

const toggleRightDrawer = () => {
  rightDrawerOpen.value = !rightDrawerOpen.value;
}

</script>
<template>
  <q-layout view="hHh lpR fFf">

    <q-header reveal elevated bordered class="bg-primary text-white">
      <q-toolbar  >
        <q-btn flat label="Homepage" />
        <q-space />
        <q-tabs   shrink  >
          <q-route-tab
            label="article"
            to="/article-list"
            exact
          />
          <q-route-tab
            label="mail"
            to="/mails"
            exact
          />
          <q-route-tab
            label="alarm"
            to="/alarms"
            exact
          />
        </q-tabs>
        <q-space />

        <q-btn dense flat round icon="close" />
        <q-btn dense flat round icon="menu" @click="toggleRightDrawer" />
      </q-toolbar>
    </q-header>

    <q-drawer v-model="rightDrawerOpen" side="right" overlay bordered>
      <!-- drawer content -->
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>

  </q-layout>
</template>
