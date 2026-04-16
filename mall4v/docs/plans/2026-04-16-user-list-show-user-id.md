# User List Show User ID Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Show `userId` in the admin user list page before the user name column.

**Architecture:** Apply the smallest possible change by updating the Avue table column config only. Do not change the page request, search form, or edit dialog.

**Tech Stack:** Vue 3, Avue, Element Plus

---

### Task 1: Add the visible user ID column

**Files:**
- Modify: `src/crud/user/user.js`

**Step 1: Update the table column list**

Insert a `userId` column before `nickName`.

**Step 2: Keep scope minimal**

Do not add search support or any new interactions.

### Task 2: Verify

**Files:**
- Verify: `src/crud/user/user.js`

**Step 1: Run lint**

Run: `npx eslint src/crud/user/user.js`

**Step 2: Confirm no lint errors**
