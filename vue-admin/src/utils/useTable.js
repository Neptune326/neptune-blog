import { ref, onMounted,nextTick } from "vue";
import { Notify,Dialog } from "quasar";
import { api } from "boot/axios";



export function useTable({
  apiUrl = "",
  isInit = true,
} = {}) {

  const params = ref({});
  const tableData = ref([]);
  const isLoading = ref(false);
  const errorMessage = ref(null);
  const pagination = ref({
    page: 1,
    rowsPerPage: 10,
    rowsNumber: 10,
  });

  // 实际的数据获取逻辑
  async function fetchData(props) {
    const { page, rowsPerPage } = props.pagination;
    let searchParams = params.value;
    let realParams = {
      ...searchParams,
      pageNumber: page,
      pageSize: rowsPerPage,
    };
    isLoading.value = true;
    try {
      api.get(apiUrl, { params: realParams }).then((res) => {
        let { pageNumber, pageSize, records, totalRow } = res.data;
        pagination.value.page = pageNumber;
        pagination.value.rowsPerPage = pageSize;
        pagination.value.rowsNumber = totalRow;
        tableData.value = records;
      });
    } catch (err) {
      errorMessage.value = err.message;
      Notify.create({
        message: "Error fetching data: " + err.value,
        color: "negative",
      });
    } finally {
      isLoading.value = false;
    }
  }

  function refreshTable() {
    fetchData({ pagination: pagination.value });
  }

  function clearTable(){
    params.value = {};
    nextTick(() => {
      refreshTable();
    }
    )
  }

  if (isInit) {
    onMounted(() => {
      refreshTable();
    });
  }

  // 返回响应式数据和方法
  return {
    params,
    pagination,
    tableData,
    isLoading,
    errorMessage,
    fetchData,
    refreshTable,
    clearTable,
  };
}


export function useOperate() {
  const remove = (id,url,callback) => {

    Dialog.create({
      title: "提示",
      message: "确认删除该记录吗？",
      cancel: true,
      persistent: true,
    }).onOk(() => {
      api.get(url, { params: { id } }).then((res) => {
        Notify.create({
          message: "删除成功",
          color: "positive",
          icon: "check",
          position: "top",
        });
        callback && callback();
      });
    });
  };



  return {
    remove,

  };
}


