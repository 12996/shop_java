<template>
  <div class="mod-log">
    <avue-crud
      ref="crudRef"
      :page="page"
      :data="dataList"
      :option="tableOption"
      @search-change="onSearch"
      @search-reset="onSearchReset"
      @selection-change="selectionChange"
      @on-load="getDataList"
    >
      <template #menu-left>
        <el-button
          type="danger"
          :disabled="dataListSelections.length <= 0"
          @click="onDelete"
        >
          批量删除
        </el-button>
        <el-button
          type="danger"
          plain
          @click="onClear"
        >
          清空
        </el-button>
      </template>
    </avue-crud>
  </div>
</template>

<script setup>
import { ElMessage, ElMessageBox } from 'element-plus'
import { tableOption } from '@/crud/sys/log.js'

const dataList = ref([])
const dataListSelections = ref([])
const page = reactive({
  total: 0,
  currentPage: 1,
  pageSize: 10
})

onMounted(() => {
  getDataList()
})

const getDataList = (pageParam, params, done) => {
  http({
    url: http.adornUrl('/sys/log/page'),
    method: 'get',
    params: http.adornParams(
      Object.assign(
        {
          current: pageParam == null ? page.currentPage : pageParam.currentPage,
          size: pageParam == null ? page.pageSize : pageParam.pageSize
        },
        params
      )
    )
  }).then(({ data }) => {
    dataList.value = data.records
    page.total = data.total
    if (done) done()
  })
}

const onSearch = (params, done) => {
  getDataList(page, params, done)
}

const selectionChange = (val) => {
  dataListSelections.value = val
}

const onDelete = () => {
  const ids = dataListSelections.value.map(item => item.id)
  if (ids.length <= 0) {
    ElMessage.warning('请先选择需要删除的日志')
    return
  }
  ElMessageBox.confirm(`确定对[id=${ids.join(',')}]进行[批量删除]操作?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/sys/log'),
      method: 'delete',
      data: http.adornData(ids, false)
    }).then(() => {
      ElMessage({
        message: '操作成功',
        type: 'success',
        duration: 1500,
        onClose: () => {
          dataListSelections.value = []
          getDataList()
        }
      })
    })
  }).catch(() => { })
}

const onSearchReset = () => {
  onDelete()
}

const onClear = () => {
  ElMessageBox.confirm('确定进行[清空系统日志]操作?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl('/sys/log/clear'),
      method: 'delete'
    }).then(() => {
      ElMessage({
        message: '操作成功',
        type: 'success',
        duration: 1500,
        onClose: () => {
          dataListSelections.value = []
          getDataList()
        }
      })
    })
  }).catch(() => { })
}
</script>
