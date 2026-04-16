<template>
  <div class="mod-coupon">
    <avue-crud
      ref="crudRef"
      :page="page"
      :data="dataList"
      :table-loading="dataListLoading"
      :option="tableOption"
      @search-change="onSearch"
      @on-load="getDataList"
      @refresh-change="refreshChange"
    >
      <template #menu-left>
        <el-button
          v-if="isAuth('admin:coupon:save')"
          type="primary"
          icon="el-icon-plus"
          @click="onAddOrUpdate()"
        >
          新增
        </el-button>
      </template>

      <template #status="scope">
        <el-tag :type="statusTypeMap[scope.row.status] || 'info'">
          {{ statusLabelMap[scope.row.status] || '-' }}
        </el-tag>
      </template>

      <template #receiveMode="scope">
        <el-tag
          type="warning"
          effect="plain"
        >
          {{ receiveModeLabelMap[scope.row.receiveMode] || '-' }}
        </el-tag>
      </template>

      <template #sourceType="scope">
        <el-tag
          type="success"
          effect="plain"
        >
          {{ sourceTypeLabelMap[scope.row.sourceType] || '-' }}
        </el-tag>
      </template>

      <template #menu="scope">
        <el-button
          v-if="isAuth('admin:coupon:invalidate') && canInvalidate(scope.row.status)"
          type="danger"
          icon="el-icon-delete"
          @click.stop="onInvalidate(scope.row.couponId)"
        >
          作废
        </el-button>
      </template>
    </avue-crud>

    <add-or-update
      v-if="addOrUpdateVisible"
      ref="addOrUpdateRef"
      @refresh-data-list="refreshChange"
    />
  </div>
</template>

<script setup>
import { isAuth } from '@/utils'
import { ElMessage, ElMessageBox } from 'element-plus'
import AddOrUpdate from './add-or-update.vue'
import {
  couponStatusDic,
  receiveModeDic,
  sourceTypeDic,
  tableOption
} from '@/crud/order/check'

const statusLabelMap = couponStatusDic.reduce((acc, item) => {
  acc[item.value] = item.label
  return acc
}, {})

const receiveModeLabelMap = receiveModeDic.reduce((acc, item) => {
  acc[item.value] = item.label
  return acc
}, {})

const sourceTypeLabelMap = sourceTypeDic.reduce((acc, item) => {
  acc[item.value] = item.label
  return acc
}, {})

const statusTypeMap = {
  0: 'warning',
  1: 'success',
  2: 'info',
  3: 'danger'
}

const dataList = ref([])
const dataListLoading = ref(false)
const addOrUpdateVisible = ref(false)
const page = reactive({
  total: 0,
  currentPage: 1,
  pageSize: 10
})

const normalizeSearchParams = (params = {}) => {
  return Object.keys(params).reduce((acc, key) => {
    if (params[key] !== '' && params[key] !== null && params[key] !== undefined) {
      acc[key] = params[key]
    }
    return acc
  }, {})
}

const getDataList = (pageParam, params, done) => {
  dataListLoading.value = true
  http({
    url: http.adornUrl('/admin/coupon/page'),
    method: 'get',
    params: http.adornParams(Object.assign({
      current: pageParam == null ? page.currentPage : pageParam.currentPage,
      size: pageParam == null ? page.pageSize : pageParam.pageSize
    }, normalizeSearchParams(params)))
  }).then(({ data }) => {
    dataList.value = data.records
    page.total = data.total
    dataListLoading.value = false
    if (done) done()
  }).catch(() => {
    dataListLoading.value = false
    if (done) done()
  })
}

const addOrUpdateRef = ref(null)

const onAddOrUpdate = () => {
  addOrUpdateVisible.value = true
  nextTick(() => {
    addOrUpdateRef.value?.init()
  })
}

const canInvalidate = (status) => {
  return status === 0 || status === 1
}

const onInvalidate = (couponId) => {
  ElMessageBox.confirm('确定将这张优惠券作废吗?', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    http({
      url: http.adornUrl(`/admin/coupon/${couponId}/invalidate`),
      method: 'post',
      data: http.adornData({})
    }).then(() => {
      ElMessage({
        message: '操作成功',
        type: 'success',
        duration: 1500,
        onClose: () => {
          refreshChange()
        }
      })
    })
  }).catch(() => { })
}

const onSearch = (params, done) => {
  getDataList(page, params, done)
}

const refreshChange = () => {
  getDataList(page)
}
</script>
