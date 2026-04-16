<template>
  <div class="edit-address-page fade-in">
    <div class="page-header">
      <div>
        <h1>{{ form.addrId ? '编辑地址' : '新增地址' }}</h1>
        <p>填写完整的收货信息，提交订单时可直接复用。</p>
      </div>
    </div>

    <div class="form-card">
      <div class="form-grid">
        <label class="field">
          <span>收货人</span>
          <input v-model.trim="form.receiver" type="text" placeholder="请输入收货人姓名" />
        </label>

        <label class="field">
          <span>手机号</span>
          <input v-model.trim="form.mobile" type="text" maxlength="11" placeholder="请输入手机号" />
        </label>

        <label class="field">
          <span>省份</span>
          <select v-model="form.provinceId" @change="handleProvinceChange">
            <option :value="0">请选择省份</option>
            <option v-for="item in provinceOptions" :key="item.areaId" :value="item.areaId">
              {{ item.areaName }}
            </option>
          </select>
        </label>

        <label class="field">
          <span>城市</span>
          <select v-model="form.cityId" @change="handleCityChange" :disabled="!cityOptions.length">
            <option :value="0">请选择城市</option>
            <option v-for="item in cityOptions" :key="item.areaId" :value="item.areaId">
              {{ item.areaName }}
            </option>
          </select>
        </label>

        <label class="field">
          <span>区县</span>
          <select v-model="form.areaId" @change="handleAreaChange" :disabled="!areaOptions.length">
            <option :value="0">请选择区县</option>
            <option v-for="item in areaOptions" :key="item.areaId" :value="item.areaId">
              {{ item.areaName }}
            </option>
          </select>
        </label>

        <label class="field field-full">
          <span>详细地址</span>
          <textarea v-model.trim="form.addr" rows="4" placeholder="请输入详细地址"></textarea>
        </label>
      </div>

      <div class="actions">
        <button v-if="form.addrId" class="danger-btn" :disabled="saving" @click="deleteAddress">删除地址</button>
        <div class="action-right">
          <button class="ghost-btn" :disabled="saving" @click="goBack">取消</button>
          <button class="primary-btn" :disabled="saving" @click="saveAddress">
            {{ saving ? '保存中...' : '保存地址' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import http from '../../utils/http'

const route = useRoute()
const router = useRouter()

const saving = ref(false)
const provinceOptions = ref([])
const cityOptions = ref([])
const areaOptions = ref([])

const form = reactive({
  addrId: 0,
  receiver: '',
  mobile: '',
  addr: '',
  province: '',
  provinceId: 0,
  city: '',
  cityId: 0,
  area: '',
  areaId: 0
})

const mobileRegExp = /^[1]([3-9])[0-9]{9}$/

const fetchAreaList = async (pid) => {
  const res = await http.get('/p/area/listByPid', {
    params: { pid }
  })
  return Array.isArray(res) ? res : []
}

const setProvinceMeta = () => {
  const province = provinceOptions.value.find((item) => item.areaId === Number(form.provinceId))
  form.province = province?.areaName || ''
}

const setCityMeta = () => {
  const city = cityOptions.value.find((item) => item.areaId === Number(form.cityId))
  form.city = city?.areaName || ''
}

const setAreaMeta = () => {
  const area = areaOptions.value.find((item) => item.areaId === Number(form.areaId))
  form.area = area?.areaName || ''
}

const loadProvinceOptions = async () => {
  provinceOptions.value = await fetchAreaList(0)
}

const loadCityOptions = async (provinceId) => {
  cityOptions.value = provinceId ? await fetchAreaList(provinceId) : []
}

const loadAreaOptions = async (cityId) => {
  areaOptions.value = cityId ? await fetchAreaList(cityId) : []
}

const initAreaOptions = async () => {
  await loadProvinceOptions()
  if (form.provinceId) {
    await loadCityOptions(form.provinceId)
  }
  if (form.cityId) {
    await loadAreaOptions(form.cityId)
  }
  setProvinceMeta()
  setCityMeta()
  setAreaMeta()
}

const loadAddressDetail = async (addrId) => {
  const res = await http.get(`/p/address/addrInfo/${addrId}`)
  form.addrId = Number(res.addrId || addrId)
  form.receiver = res.receiver || ''
  form.mobile = res.mobile || ''
  form.addr = res.addr || ''
  form.province = res.province || ''
  form.provinceId = Number(res.provinceId || 0)
  form.city = res.city || ''
  form.cityId = Number(res.cityId || 0)
  form.area = res.area || ''
  form.areaId = Number(res.areaId || 0)
}

const handleProvinceChange = async () => {
  setProvinceMeta()
  form.cityId = 0
  form.city = ''
  form.areaId = 0
  form.area = ''
  areaOptions.value = []
  await loadCityOptions(form.provinceId)
}

const handleCityChange = async () => {
  setCityMeta()
  form.areaId = 0
  form.area = ''
  await loadAreaOptions(form.cityId)
}

const handleAreaChange = () => {
  setAreaMeta()
}

const validateForm = () => {
  if (!form.receiver) {
    alert('请输入收货人姓名')
    return false
  }
  if (!form.mobile) {
    alert('请输入手机号')
    return false
  }
  if (!mobileRegExp.test(form.mobile)) {
    alert('请输入正确的手机号')
    return false
  }
  if (!form.provinceId || !form.cityId || !form.areaId) {
    alert('请选择完整的省市区')
    return false
  }
  if (!form.addr) {
    alert('请输入详细地址')
    return false
  }
  return true
}

const buildPayload = () => ({
  receiver: form.receiver,
  mobile: form.mobile,
  addr: form.addr,
  province: form.province,
  provinceId: Number(form.provinceId),
  city: form.city,
  cityId: Number(form.cityId),
  area: form.area,
  areaId: Number(form.areaId),
  userType: 0,
  addrId: Number(form.addrId || 0)
})

const goBack = () => {
  router.push({
    path: '/address',
    query: {
      ...route.query
    }
  })
}

const saveAddress = async () => {
  if (!validateForm()) {
    return
  }

  saving.value = true
  try {
    const payload = buildPayload()
    if (form.addrId) {
      await http.put('/p/address/updateAddr', payload)
    } else {
      await http.post('/p/address/addAddr', payload)
    }
    goBack()
  } catch (error) {
    alert(error?.message || '保存地址失败')
  } finally {
    saving.value = false
  }
}

const deleteAddress = async () => {
  if (!form.addrId) {
    return
  }
  if (!window.confirm('确定要删除这个收货地址吗？')) {
    return
  }

  saving.value = true
  try {
    await http.delete(`/p/address/deleteAddr/${form.addrId}`)
    goBack()
  } catch (error) {
    alert(error?.message || '删除地址失败')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const addrId = Number(route.query.addrId || 0)
  try {
    if (addrId) {
      await loadAddressDetail(addrId)
    }
    await initAreaOptions()
  } catch (error) {
    console.error('Failed to initialize address page', error)
    alert('地址信息加载失败')
  }
})
</script>

<style scoped>
.fade-in {
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.edit-address-page {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.page-header,
.form-card {
  background: white;
  border-radius: 20px;
  padding: 28px 32px;
  border: 1px solid rgba(226, 232, 240, 0.6);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.02);
}

.page-header h1 {
  margin: 0 0 8px;
  font-size: 28px;
  color: #1e293b;
}

.page-header p {
  margin: 0;
  color: #94a3b8;
  font-size: 14px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.field {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.field span {
  font-size: 14px;
  color: #334155;
  font-weight: 600;
}

.field input,
.field select,
.field textarea {
  width: 100%;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  padding: 14px 16px;
  font-size: 14px;
  color: #1e293b;
  outline: none;
  background: #fff;
  box-sizing: border-box;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.field input:focus,
.field select:focus,
.field textarea:focus {
  border-color: #3b9dfd;
  box-shadow: 0 0 0 3px rgba(59, 157, 253, 0.12);
}

.field textarea {
  resize: vertical;
  min-height: 110px;
}

.field-full {
  grid-column: 1 / -1;
}

.actions {
  margin-top: 28px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.action-right {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-left: auto;
}

.primary-btn,
.ghost-btn,
.danger-btn {
  border-radius: 12px;
  padding: 12px 20px;
  font-size: 14px;
  cursor: pointer;
}

.primary-btn {
  border: none;
  background: linear-gradient(135deg, #4aa3ff 0%, #3594fb 100%);
  color: white;
}

.ghost-btn {
  border: 1px solid #e2e8f0;
  background: white;
  color: #475569;
}

.danger-btn {
  border: 1px solid #fecaca;
  background: #fff1f2;
  color: #e11d48;
}

.primary-btn:disabled,
.ghost-btn:disabled,
.danger-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 900px) {
  .page-header,
  .form-card {
    padding: 20px;
    border-radius: 16px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .actions {
    flex-direction: column;
    align-items: stretch;
  }

  .action-right {
    width: 100%;
    margin-left: 0;
  }

  .ghost-btn,
  .primary-btn,
  .danger-btn {
    flex: 1;
  }
}
</style>
