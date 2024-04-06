<script setup>
import { ref } from "vue";

const icons = [
  "check_circle",
  "warning",
  "info",
  "priority_high",
  "arrow_upward",
  "arrow_forward",
  "arrow_downward",
  "arrow_back",
  "arrow_drop_down",
  "chevron_left",
  "chevron_right",
  "gradient",
  "tune",
  "style",
  "refresh",
  "chevron_left",
  "chevron_right",
  "keyboard_arrow_up",
  "keyboard_arrow_down",
  "lens",
  "cancel",
  "check",
  "chevron_left",
  "chevron_right",
  "access_time",
  "today",
  "format_bold",
  "format_italic",
  "strikethrough_s",
  "format_underlined",
  "format_list_bulleted",
  "format_list_numbered",
  "vertical_align_bottom",
  "vertical_align_top",
  "link",
  "fullscreen",
  "format_quote",
  "format_align_left",
  "format_align_center",
  "format_align_right",
  "format_align_justify",
  "print",
  "format_indent_decrease",
  "format_indent_increase",
  "format_clear",
  "text_format",
  "format_size",
  "format_align_left",
  "remove",
  "undo",
  "redo",
  "format_size",
  "code",
  "format_size",
  "font_download",
  "code",
  "keyboard_arrow_down",
  "arrow_drop_down",
  "add",
  "close",
  "cancel",
  "error",
  "first_page",
  "keyboard_arrow_left",
  "keyboard_arrow_right",
  "last_page",
  "grade",
  "check",
  "edit",
  "warning",
  "chevron_left",
  "chevron_right",
  "keyboard_arrow_up",
  "keyboard_arrow_down",
  "arrow_upward",
  "warning",
  "first_page",
  "chevron_left",
  "chevron_right",
  "last_page",
  "play_arrow",
  "done",
  "clear",
  "add_box",
  "cloud_upload",
  "clear_all",
  "done_all",
];

const props = defineProps({
  label: {
    type: String,
    default: "Label",
  },
  dense: {
    type: Boolean,
    default: false,
  },
  clearable: {
    type: Boolean,
    default: false,
  },
  modelValue: {
    type: String,
    default: "",
  },
});
const emits = defineEmits(["update:modelValue"]);

const dialog = ref(false);

const checked = (icon) => {
  emits("update:modelValue", icon);
  dialog.value = false;
};

const clear = () => {
  emits("update:modelValue", null);
};

const updateIcon = (icon) => {
  emits("update:modelValue", icon);
};
</script>

<template>
  <div @click="dialog = !dialog" class="q-field--with-bottom">
    <q-field
      outlined
      :label="props.label"
      :dense="props.dense"
      clearable
      :model-value="props.modelValue"
      @@update:model-value="updateIcon"
    >
      <template v-slot:before>
        <q-icon :name="props.modelValue" />
      </template>

      <template v-slot:control>
        <div class="self-center full-width no-outline" tabindex="0">
          {{ props.modelValue }}
        </div>
      </template>
    </q-field>

    <q-dialog v-model="dialog">
      <q-card style="min-width: 60vw">
        <q-card-section class="row items-center q-pb-none">
          <div class="text-h6">Select Icon</div>
          <q-space />
          <q-btn icon="close" flat round dense v-close-popup />
        </q-card-section>

        <q-card-section class="row">
          <div
            class="col-1 row justify-center q-my-md"
            v-for="icon in icons"
            :key="icon"
          >
            <q-btn
              round
              class="text-center"
              :color="props.modelValue === icon ? 'primary' : undefined"
              :icon="icon"
              @click="checked(icon)"
            />
          </div>
        </q-card-section>
      </q-card>
    </q-dialog>
  </div>
</template>

<style scoped></style>
