<template>
  <el-dialog
    v-model="visible"
    title="新增优惠券"
    :close-on-click-modal="false"
    width="680px"
  >
    <el-form
      ref="dataFormRef"
      :model="dataForm"
      :rules="dataRule"
      label-width="110px"
    >
      <el-form-item
        label="优惠券编号"
        prop="couponNo"
      >
        <el-input
          v-model="dataForm.couponNo"
          maxlength="64"
          placeholder="请输入优惠券编号"
        />
      </el-form-item>
      <el-form-item
        label="优惠券名称"
        prop="couponName"
      >
        <el-input
          v-model="dataForm.couponName"
          maxlength="128"
          placeholder="请输入优惠券名称"
        />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item
            label="优惠金额"
            prop="reduceAmount"
          >
            <el-input-number
              v-model="dataForm.reduceAmount"
              :min="0.01"
              :precision="2"
              :step="1"
              :controls="false"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="使用门槛"
            prop="conditionAmount"
          >
            <el-input-number
              v-model="dataForm.conditionAmount"
              :min="0"
              :precision="2"
              :step="1"
              :controls="false"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item
        label="领取方式"
        prop="receiveMode"
      >
        <el-radio-group v-model="dataForm.receiveMode">
          <el-radio :label="1">
            后台发放
          </el-radio>
          <el-radio :label="2">
            公开领取
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item
        v-if="dataForm.receiveMode === 1"
        label="用户ID"
        prop="userId"
      >
        <el-input
          v-model="dataForm.userId"
          placeholder="请输入指定用户ID"
        />
      </el-form-item>
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item
            label="生效时间"
            prop="startTime"
          >
            <el-date-picker
              v-model="dataForm.startTime"
              type="datetime"
              value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="请选择生效时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item
            label="失效时间"
            prop="endTime"
          >
            <el-date-picker
              v-model="dataForm.endTime"
              type="datetime"
              value-format="YYYY-MM-DD HH:mm:ss"
              placeholder="请选择失效时间"
              style="width: 100%"
            />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item
        label="备注"
        prop="remark"
      >
        <el-input
          v-model="dataForm.remark"
          type="textarea"
          :rows="3"
          maxlength="255"
          show-word-limit
          placeholder="请输入备注"
        />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="visible = false">取消</el-button>
        <el-button
          type="primary"
          @click="onSubmit()"
        >
          确定
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ElMessage } from 'element-plus'
import { Debounce } from '@/utils/debounce'

const emit = defineEmits(['refreshDataList'])

const visible = ref(false)
const dataFormRef = ref(null)
const dataForm = reactive({
  couponNo: '',
  couponName: '',
  reduceAmount: 10,
  conditionAmount: 0,
  receiveMode: 2,
  userId: '',
  startTime: '',
  endTime: '',
  remark: ''
})

const validateUserId = (value, callback) => {
  if (dataForm.receiveMode === 1 && !value) {
    callback(new Error('请输入指定用户ID'))
    return
  }
  callback()
}

const validateEndTime = (value, callback) => {
  if (!value) {
    callback(new Error('请选择失效时间'))
    return
  }
  if (dataForm.startTime && value < dataForm.startTime) {
    callback(new Error('失效时间不能早于生效时间'))
    return
  }
  callback()
}

const dataRule = reactive({
  couponNo: [
    { required: true, message: '请输入优惠券编号', trigger: 'blur' }
  ],
  couponName: [
    { required: true, message: '请输入优惠券名称', trigger: 'blur' }
  ],
  reduceAmount: [
    { required: true, message: '请输入优惠金额', trigger: 'blur' }
  ],
  conditionAmount: [
    { required: true, message: '请输入使用门槛', trigger: 'blur' }
  ],
  receiveMode: [
    { required: true, message: '请选择领取方式', trigger: 'change' }
  ],
  userId: [
    {
      validator: (...args) => validateUserId(args[1], args[2]),
      trigger: 'blur'
    }
  ],
  startTime: [
    { required: true, message: '请选择生效时间', trigger: 'change' }
  ],
  endTime: [
    {
      validator: (...args) => validateEndTime(args[1], args[2]),
      trigger: 'change'
    }
  ]
})

const resetForm = () => {
  dataForm.couponNo = ''
  dataForm.couponName = ''
  dataForm.reduceAmount = 10
  dataForm.conditionAmount = 0
  dataForm.receiveMode = 2
  dataForm.userId = ''
  dataForm.startTime = ''
  dataForm.endTime = ''
  dataForm.remark = ''
}

const init = () => {
  visible.value = true
  nextTick(() => {
    dataFormRef.value?.resetFields()
    resetForm()
  })
}

defineExpose({ init })

const onSubmit = Debounce(() => {
  dataFormRef.value?.validate((valid) => {
    if (!valid) {
      return
    }
    http({
      url: http.adornUrl('/admin/coupon'),
      method: 'post',
      data: http.adornData({
        couponNo: dataForm.couponNo,
        couponName: dataForm.couponName,
        reduceAmount: dataForm.reduceAmount,
        conditionAmount: dataForm.conditionAmount,
        receiveMode: dataForm.receiveMode,
        userId: dataForm.receiveMode === 1 ? dataForm.userId : '',
        startTime: dataForm.startTime,
        endTime: dataForm.endTime,
        remark: dataForm.remark
      })
    }).then(() => {
      ElMessage({
        message: '操作成功',
        type: 'success',
        duration: 1500,
        onClose: () => {
          visible.value = false
          emit('refreshDataList')
        }
      })
    })
  })
})
</script>
