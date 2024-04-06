<script setup>
import {onMounted, ref} from "vue";
import IconSelect from "components/IconSelect.vue";
import {useDialogPluginComponent} from "quasar";
import {useMenu} from "src/utils/useCommon";
import {useForm} from "src/utils/useForm";

const props = defineProps({
  id: {
    type: String,
    default: "",
  },
});

defineEmits([...useDialogPluginComponent.emits]);

const {dialogRef, onDialogHide, onDialogOK, onDialogCancel} =
  useDialogPluginComponent();

const {formData, myForm, check, initForm, submitForm} = useForm(props);
formData.value.type = "0";
formData.value.isHidden = "0";

const {menus} = useMenu({list: true});
onMounted(async () => {
  await initForm("/admin/menu/editInfo");
  changeMenuType(formData.value.type);
});

const showFlag = ref(true);

const changeMenuType = (value) => {
  showFlag.value = value === "0";
};
</script>
<template>
  <q-dialog persistent ref="dialogRef" @hide="onDialogHide">
    <q-card class="q-dialog-plugin">
      <q-card-section class="row items-center q-pb-none">
        <div class="text-h6"></div>
        <q-space/>
        <q-btn icon="close" flat round v-close-popup/>
      </q-card-section>
      <q-card-section class="scroll" style="max-height: 75vh">
        <q-form ref="myForm" class="row q-ma-md">
          <div class="col-12">
            <q-input
              outlined
              v-model="formData.name"
              label="菜单名称"
              lazy-rules
              :rules="[
                (v) => !!v || '菜单名称不能为空',
                (v) => /^.{2,20}$/.test(v) || '菜单名称必须为2-20个字符',
                (v) => check('/admin/menu/check', { name: v }),
              ]"
              :debounce="500"
            />
          </div>
          <div class="col-12">
            <q-input
              outlined
              v-model="formData.code"
              label="菜单编码"
              lazy-rules
              :rules="[
                (v) => !!v || '菜单编码不能为空',
                (v) => /^.{2,20}$/.test(v) || '菜单编码必须为2-20个字符',
                (v) => check('/admin/menu/check', { code: v }),
              ]"
              :debounce="500"
            />
          </div>

          <div class="col-12">
            <q-select
              clearable
              outlined
              v-model="formData.parentId"
              label="父菜单"
              :options="menus"
              emit-value
              map-options
              option-label="name"
              option-value="id"
              options-selected-class="text-deep-orange"
            >
              <template #option="{ itemProps, opt }">
                <q-item v-bind="itemProps">
                  <q-item-section avatar>
                    <q-icon :name="opt['icon']"/>
                  </q-item-section>
                  <q-item-section>
                    <q-item-label>{{ opt["name"] }}</q-item-label>
                  </q-item-section>
                </q-item>
              </template>
            </q-select>
          </div>
          <div class="col-12">
            <q-option-group
              label="菜单类型"
              class="q-ma-md q-ml-none"
              outlined
              v-model="formData.type"
              :options="[
                { label: '菜单', value: '0' },
                { label: '目录', value: '1' },
              ]"
              color="primary"
              inline
              @update:model-value="changeMenuType"
            />
          </div>

          <div class="col-12">
            <IconSelect v-model="formData.icon" label="菜单图标"/>
          </div>
          <div class="col-12">
            <q-input
              outlined
              v-model="formData.sort"
              label="菜单排序"
              lazy-rules
              :rules="[
                (v) => !!v || '菜单排序不能为空',
                (v) => /^[0-9]{1,5}$/.test(v) || '菜单排序必须为1-5位数字',
              ]"
              :debounce="500"
            />
          </div>
          <div class="col-12 q-field--with-bottom" v-show="showFlag">
            <q-input outlined v-model="formData.route" label="菜单路由"/>
          </div>
          <div class="col-12 q-field--with-bottom" v-show="showFlag">
            <q-input
              outlined
              v-model="formData.componentName"
              label="组件名称"
            />
          </div>
          <div class="col-12 q-field--with-bottom" v-show="showFlag">
            <q-input
              outlined
              v-model="formData.componentPath"
              label="组件路径"
              prefix="pages/"
              suffix=".vue"
            />
          </div>
          <div class="col-12">
            <q-option-group
              outlined
              v-model="formData.isHidden"
              :options="[
                { label: '显示', value: '0' },
                { label: '隐藏', value: '1' },
              ]"
              color="primary"
              inline
            />
          </div>
        </q-form>
      </q-card-section>
      <q-card-actions align="right">
        <q-btn
          color="primary"
          label="保存"
          @click="
            submitForm('/admin/menu/save', '/admin/menu/update', onDialogOK)
          "
        />
      </q-card-actions>
    </q-card>
  </q-dialog>
</template>

<style>
.q-dialog-plugin {
  min-width: 50vw;
  max-width: 80vw;
}
</style>
