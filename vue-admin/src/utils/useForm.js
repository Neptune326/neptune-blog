import { ref } from "vue";
import { api } from "boot/axios";
import { Notify, Dialog } from "quasar";

export function useForm(props) {
  const formData = ref({
    id: props.id,
  });
  const myForm = ref(null);

  const check = async (url, params={}) => {
    const res = await api.get(url, {
      params: {
        id: formData.value.id,
       ...params
      },
    });
    return res?.status || res?.message;
  };

  const initForm = async (url) => {
    let id = formData.value.id;
    if (!id) {
      return;
    }
    const res = await api.get(url, {
      params: { id: formData.value.id },
    });
    formData.value = res.data;
  };

  const submitForm = (saveUrl, updateUrl, callback) => {
    myForm.value.validate().then((success) => {
      if (!success) {
        return;
      }
      api.post(props.id ? updateUrl : saveUrl, formData.value).then((res) => {
        if (res.status) {
          Notify.create({
            message: "保存成功",
            type: "positive",
          });
          callback && callback(res.data);
        } else {
          Notify.create({
            message: res.msg,
            type: "negative",
          });
        }
      });
    });
  };

  return {
    formData,
    myForm,

    check,
    initForm,
    submitForm,
  };
}
