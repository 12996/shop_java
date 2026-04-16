# Coupon Admin Page Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a minimal admin coupon management page in `src/views/modules/order/check` with list, search, create, and invalidate actions.

**Architecture:** Reuse the existing admin console pattern based on `avue-crud` for the main table and an `el-dialog` form for creation. Keep the page single-purpose and map directly to the backend coupon endpoints that already exist.

**Tech Stack:** Vue 3, Element Plus, Avue, Vite, Axios

---

### Task 1: Add table configuration

**Files:**
- Create: `src/crud/order/check.js`

**Step 1: Define the minimum searchable fields**

Add table config for:
- `couponNo`
- `couponName`
- `status`

**Step 2: Define visible columns**

Add visible columns for:
- coupon number and name
- discount amount and threshold amount
- receive mode and source type
- user id
- start/end time
- status

### Task 2: Build the list page

**Files:**
- Modify: `src/views/modules/order/check/index.vue`

**Step 1: Render the page with `avue-crud`**

Use the same structure as other admin pages:
- left toolbar add button
- list request on load
- row action for invalidate

**Step 2: Connect backend APIs**

Use:
- `GET /admin/coupon/page`
- `POST /admin/coupon/{couponId}/invalidate`

**Step 3: Add status and enum display**

Render tags for:
- coupon status
- receive mode
- source type

### Task 3: Build the create dialog

**Files:**
- Create: `src/views/modules/order/check/add-or-update.vue`

**Step 1: Add the minimal form**

Fields:
- `couponNo`
- `couponName`
- `reduceAmount`
- `conditionAmount`
- `receiveMode`
- `userId` when admin assign
- `startTime`
- `endTime`
- `remark`

**Step 2: Add minimal validation**

Validate:
- required text and amount fields
- required time fields
- `userId` required when `receiveMode = 1`
- start time must not be later than end time

**Step 3: Submit to backend**

Use:
- `POST /admin/coupon`

### Task 4: Verify

**Files:**
- Verify: `src/views/modules/order/check/index.vue`
- Verify: `src/views/modules/order/check/add-or-update.vue`
- Verify: `src/crud/order/check.js`

**Step 1: Run lint**

Run: `pnpm lint -- src/views/modules/order/check/index.vue src/views/modules/order/check/add-or-update.vue src/crud/order/check.js`

**Step 2: Fix any lint issues**

Keep the implementation aligned with the existing admin style and avoid unrelated refactors.
