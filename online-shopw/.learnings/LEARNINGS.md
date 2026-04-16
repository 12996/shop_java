## [LRN-20260416-001] correction

**Logged**: 2026-04-16T15:15:00Z
**Priority**: high
**Status**: pending
**Area**: frontend

### Summary
Do not restrict web UI elements to mobile widths (e.g. max-width: 480px) just because the backend or mockups derive from Mini Programs. 

### Details
When given a screenshot from a mini program as a design reference for a Web app, I previously restricted the `.user-page` to `max-width: 480px` to mimic the mobile view exactly. The user corrected me, stating that because this is a Web platform, I should adapt the UI for standard web widths and not shrink the viewport layout artificially.  Design translations from mobile mockups to web must remain responsive and utilize standard desktop widths (e.g. `1440px` or `1600px`).

### Suggested Action
Modify `f:\work\project\shop_java\online-shopw\web\src\views\User\index.vue` to restore `max-width: 1600px` and style the container flexibly for full web screens. When creating web components based on mobile screenshots, use web-friendly layouts (flexbox spacing, grid distributions extending across the screen or central max-width containers).

### Metadata
- Source: user_feedback
- Related Files: src/views/User/index.vue
- Tags: layout, responsive, web-adaptation
