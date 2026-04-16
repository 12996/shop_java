const statusDic = [
  { label: '待发放', value: 0 },
  { label: '未使用', value: 1 },
  { label: '已使用', value: 2 },
  { label: '已过期', value: 3 }
]

export const couponStatusDic = statusDic

export const receiveModeDic = [
  { label: '后台发放', value: 1 },
  { label: '公开领取', value: 2 }
]

export const sourceTypeDic = [
  { label: '后台发放', value: 1 },
  { label: '用户领取', value: 2 }
]

export const tableOption = {
  searchMenuSpan: 6,
  columnBtn: false,
  border: true,
  selection: false,
  index: true,
  indexLabel: '序号',
  stripe: true,
  menuAlign: 'center',
  menuWidth: 160,
  align: 'center',
  refreshBtn: true,
  searchSize: 'mini',
  addBtn: false,
  editBtn: false,
  delBtn: false,
  viewBtn: false,
  column: [
    {
      label: '优惠券编号',
      prop: 'couponNo',
      search: true
    },
    {
      label: '优惠券名称',
      prop: 'couponName',
      search: true,
      minWidth: 160
    },
    {
      label: '优惠金额',
      prop: 'reduceAmount',
      minWidth: 110
    },
    {
      label: '使用门槛',
      prop: 'conditionAmount',
      minWidth: 110
    },
    {
      label: '领取方式',
      prop: 'receiveMode',
      search: true,
      slot: true,
      type: 'select',
      dicData: receiveModeDic
    },
    {
      label: '来源',
      prop: 'sourceType',
      slot: true,
      minWidth: 110
    },
    {
      label: '用户ID',
      prop: 'userId',
      minWidth: 120
    },
    {
      label: '状态',
      prop: 'status',
      search: true,
      slot: true,
      type: 'select',
      dicData: statusDic
    },
    {
      label: '生效时间',
      prop: 'startTime',
      minWidth: 170
    },
    {
      label: '失效时间',
      prop: 'endTime',
      minWidth: 170
    },
    {
      label: '备注',
      prop: 'remark',
      minWidth: 180,
      overHidden: true
    }
  ]
}
