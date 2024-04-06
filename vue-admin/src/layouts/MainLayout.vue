<script setup>
import { defineComponent, ref, computed } from "vue";
import { useRoute } from "vue-router";
import LeftMenu from "components/LeftMenu.vue";
import { useQuasar } from "quasar";
import { useAuthStore } from "src/stores/authStore";

const authStore = useAuthStore();
const userInfo = authStore.getUserInfo;
const menus = authStore.menus;

const route = useRoute();
const $q = useQuasar();

const leftDrawer = ref(false);
const isDark = ref(false);
const toggleDark = () => {
  $q.dark.set(isDark.value);
};

const breadCrumbs = computed(() => {
  let breadCrumb = route.meta.breadCrumb;
  let arr = ["首页"];
  if (breadCrumb) {
    arr = [...arr, ...breadCrumb.split("/")];
  }
  return arr;
});
</script>
<template>
  <q-layout view="lHh lpR fFf">
    <q-header elevated class="bg-grey-3 text-primary">
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="leftDrawer = !leftDrawer"
        />

        <q-breadcrumbs class="text-orange q-ml-lg">
          <q-breadcrumbs-el
            v-for="(breadCrumb, i) in breadCrumbs"
            :key="i"
            :label="breadCrumb"
          />
        </q-breadcrumbs>

        <q-space />
        <div class="q-gutter-sm row items-center no-wrap">
          <q-btn
            round
            dense
            flat
            :icon="$q.fullscreen.isActive ? 'fullscreen_exit' : 'fullscreen'"
            @click="$q.fullscreen.toggle()"
            v-if="$q.screen.gt.sm"
          >
          </q-btn>
          <q-toggle
            v-model="isDark"
            color="green"
            checked-icon="check"
            unchecked-icon="clear"
            @click="toggleDark"
          />
          <q-btn round dense flat icon="notifications">
            <q-badge color="red" text-color="white" floating> 5 </q-badge>
            <q-menu>
              <q-list style="min-width: 100px">
                Message组件
                <q-card class="text-center no-shadow no-border">
                  <q-btn
                    label="View All"
                    style="max-width: 120px !important"
                    flat
                    dense
                    class="text-indigo-8"
                  ></q-btn>
                </q-card>
              </q-list>
            </q-menu>
          </q-btn>
          <q-avatar rounded color="primary" text-color="white">
            <img v-if="userInfo.avatar" :src="userInfo.avatar" />
            <img
              v-if="!userInfo.avatar"
              src="https://cdn.quasar.dev/img/avatar4.jpg"
            />
            <q-menu>
              <q-list style="min-width: 200px; text-align: center">
                <q-item>
                  <q-item-section>
                    <q-avatar>
                      <img src="https://cdn.quasar.dev/img/avatar4.jpg" />
                    </q-avatar>
                  </q-item-section>
                </q-item>
                <q-item>
                  <q-item-section>
                    <div class="text-subtitle1 q-mt-md q-mb-xs">
                      {{ userInfo.userName }}
                    </div>
                  </q-item-section>
                </q-item>
                <q-item clickable v-close-popup>
                  <q-item-section @click="authStore.logout"
                    >退出</q-item-section
                  >
                </q-item>
                <q-item clickable v-close-popup>
                  <q-item-section>个人中心</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
          </q-avatar>
        </div>
      </q-toolbar>
    </q-header>

    <q-drawer v-model="leftDrawer" show-if-above bordered>
      <q-list separator padding>
        <q-item clickable v-ripple>
          <q-item-section avatar>
            <q-avatar color="primary" text-color="white"> N </q-avatar>
          </q-item-section>

          <q-item-section>Neptune</q-item-section>
        </q-item>

        <q-item-label header> 菜 单 </q-item-label>

        <LeftMenu :menus="menus" />
      </q-list>
    </q-drawer>

    <q-page-container>
      <router-view v-slot="{ Component }">
        <transition name="fade">
          <component :is="Component" />
        </transition>
      </router-view>
      <!-- <router-view /> -->
    </q-page-container>
  </q-layout>
</template>
<style lang="scss" scoped>
.fade-enter,
.fade-leave-to {
  visibility: hidden;
  opacity: 0;
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s ease;
}
.fade-enter-to,
.fade-leave {
  visibility: visible;
  opacity: 1;
}
.fade-leave-to {
  display: none;
}
</style>
