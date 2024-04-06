import { ref, onMounted,nextTick } from "vue";
import { Notify,Dialog } from "quasar";
import { api } from "boot/axios";



export function useUpload( ) {

  const fileRef = ref(null);

  const uploadSingleFile = async (url)=>{
    try {
      if (!fileRef.value) {
        Notify.create({
          message: "请选择文件",
          color: "negative",
        });
        return;
      }
      const files = fileRef.value.files;
      if (files.length > 1) {
        Notify.create({
          message: "只能上传一个文件",
          color: "negative",
        });
        return;
      }

      const formData = new FormData();
      formData.append("file", files[0]);
      const response = await api.post(url, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      });
      return response.data;
    } catch (error) {
      console.log(error);
      Notify.create({
        message: "上传失败",
        color: "negative",
      });
    }finally {
      fileRef.value = null;
    }
  }


  return {
    fileRef,
    uploadSingleFile
  };

}
