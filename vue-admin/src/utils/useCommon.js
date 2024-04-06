import {ref, onMounted} from "vue";
import {api} from "boot/axios";
import {Notify, Dialog} from "quasar";

export function useMenu({
                          isMounted = true,
                          list = false,
                          select = false,
                        } = options) {
  const getMenuList = async () => {
    let res = await api.get("/admin/menu/list")
    return res.data;
  }

  const getSelectList = async () => {
    let res = await api.get("/admin/menu/selectList");
    return res.data;
  }

  const menus = ref([]);
  const menuSelects = ref([]);
  if (isMounted) {
    onMounted(async () => {
      if (list) {
        menus.value = await getMenuList();
      }
      if (select) {
        menuSelects.value = await getSelectList();
      }
    })
  }

  return {
    menus,
    menuSelects,
    getMenuList,
    getSelectList
  };
}

export function useQInputSelect(options = []) {
  const stringOptions = options
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
  return {
    stringOptions,
    filterOptions,
    createValue,
    filterFn
  }
}
