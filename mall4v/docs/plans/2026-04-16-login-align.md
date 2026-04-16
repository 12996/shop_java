# Login Align Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Make the login page logo, inputs, and submit button share the same visual center line without changing the existing admin style.

**Architecture:** Keep the current login page structure and only adjust the scoped styles in the login view. Use the existing `410px` form width as the single alignment baseline so the logo and form controls render on the same center line.

**Tech Stack:** Vue 3 single-file component, scoped SCSS, Element Plus

---

### Task 1: Align login page content

**Files:**
- Modify: `src/views/common/login/index.vue`
- Verify: `docs/plans/2026-04-16-login-align-design.md`

**Step 1: Update the logo container styles**

Change the login page scoped styles so `.top` uses the same fixed width as the form content instead of `max-width: 50%`.

**Step 2: Normalize image centering**

Add `display: block`, `max-width: 100%`, and `margin: 0 auto` to the logo image so the asset itself centers inside the shared content width.

**Step 3: Align the button container**

Make sure `.item-btn` is centered within the shared width and does not rely on incidental layout.

**Step 4: Run a lightweight verification**

Run: `npx eslint src/views/common/login/index.vue`

Expected: PASS with no lint errors.
