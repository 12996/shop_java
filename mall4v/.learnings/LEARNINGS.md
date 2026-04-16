## [LRN-20260416-001] correction

**Logged**: 2026-04-16T13:19:15.8919701+08:00
**Priority**: high
**Status**: pending
**Area**: frontend

### Summary
mall4v 管理后台新增或修改交互时，必须保持旧式 Element/Avue 后台风格，不能引入突兀的新组件表达。

### Details
用户明确指出我在现有页面中追加的组件和交互显得突兀。复查后确认，这个项目的后台页面并不是现代卡片化、强调视觉层次的设计，而是典型的旧式管理台风格：
- 顶部通常是紧凑的筛选表单和一排标准 `el-button`
- 列表页以 `avue-crud` 或朴素表格/分栏为主
- 操作入口偏文本按钮或标准主/危险按钮，不使用额外胶囊、悬浮工具条、二级卡片动作区
- 色彩克制，主要依赖边框、浅灰底、表格线、少量蓝色操作色和红色危险色
- 布局优先“信息密度”和“业务直给”，而不是视觉包装

这意味着后续在该后台中补功能时，应优先复用现有按钮位置、表单结构、表格列、弹窗模式和文案密度；不要为了“更现代”而新造操作块、强调卡片、额外说明区或视觉上过强的组件。

### Suggested Action
- 修改 mall4v 后台页面前，先对照同模块已有页面的按钮区、列表区、弹窗区写法
- 新增操作优先落在现有 `menu-left`、`menu`、筛选区按钮行、详情弹窗已有动作区
- 非必要不新增独立卡片、说明横幅、浮层式操作容器、彩色状态块
- 颜色、间距、边框、按钮类型保持和同模块页一致

### Metadata
- Source: user_feedback
- Related Files: src/views/modules/order/order/index.vue
- Tags: style-consistency, admin-ui, avue, element-plus

---
