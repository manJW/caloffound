# AGENTS.md

This repository is the long-term BS renovation line for `caloffound`.
The target is to replace the old Python desktop client with a web system that
can later be merged into `PowerGrid` with minimal structural changes.

## 1. Mission

Complete the full renovation plan without requiring repeated human prompting.
Work continuously through the backlog in the defined order. Only stop when one
of these conditions is true:

- a destructive production decision needs confirmation
- a required external credential or environment value is missing
- two valid implementation paths have materially different product outcomes
- the current step cannot proceed without information that is not discoverable

Otherwise, continue executing.

## 2. Non-Negotiable Constraints

- Frontend stack: `Vue2 + Vue Router + Vuex + Element UI + Three.js`
- Backend stack: `Spring Boot 3 + Spring Security + MyBatis + MySQL`
- 3D is web-first. Do not reintroduce desktop-only rendering as the main path.
- Align naming, routes, permissions, and module structure with `PowerGrid`.
- Do not import the old Python project as the new runtime. It is only a source
  of behavior, SQL references, data contracts, and regression samples.
- Prefer incremental replacement over large rewrites that break existing slices.

## 3. Source of Truth

Read these files before major work:

- `README.md`
- `docs/architecture/RENOVATION_PLAN.md`
- `docs/architecture/MODULE_MAP.md`
- `docs/migration/BACKLOG.md`
- `docs/database/LEGACY_SCHEMA.md`

Use the old desktop project only as a reference source for:

- SQL table and field discovery
- business workflow order
- data import/export behavior
- 2D/3D rendering semantics

## 4. Working Mode

Default behavior:

- inspect current state
- implement the next unresolved slice
- validate it
- continue to the next slice

Do not wait for a user message that says "continue".

When progress is partial, leave the repository in a state where the next run can
resume from this file and existing docs without conversation context.

## 5. Current Baseline

Already established:

- repository skeleton
- backend multi-module Maven structure
- frontend Vue2 admin shell
- overview page
- project and node UI/API slice
- data prep UI/API slice
- tower and leg UI/API slice
- foundation calculation UI/API slice
- 2D preview and result export baseline
- Three.js scene page baseline
- datasource and MyBatis bootstrap
- first batch of legacy schema notes
- project/survey/tower read-chain mappers

Important note:

- write operations are still mostly in-memory service logic
- frontend full build has not yet been validated with `npm install` and
  `npm run build`

## 6. Execution Order

Always prefer this order unless a later item blocks an earlier one.

### Phase A. Database completion

1. Continue replacing in-memory reads with MyBatis-backed reads
   - tower assignments
   - tower legs
   - survey and terrain reads
2. Replace write operations with MyBatis-backed writes
   - survey type update
   - survey point replace
   - batch survey import
   - tower assignment update
   - leg replace
3. Add mapper coverage for the remaining core legacy tables
   - `dced_prj_electric`
   - `dced_prj_leginfo`
   - `dced_prj_legsetconfig`
   - `dced_project_towertype`
   - `dced_project_towerbody`
4. Keep `foundation.persistence.mode` switchable until DB-backed flows are
   stable enough to become default

### Phase B. Data preparation completion

1. Finish project-level survey import reporting
2. Add TIF upload/sync/clear backed by persistent storage
3. Add input-type switching with durable backend model
4. Add import result history/log display

### Phase C. Tower and leg completion

1. Move tower assignment writes to DB
2. Move leg writes to DB
3. Add default tower body lookup against reference tables
4. Add leg rule configuration read/write
5. Add automatic leg fill rule baseline

### Phase D. Foundation calculation completion

1. Persist calculation records to database
2. Add complete result detail API
3. Add result comparison/history management
4. Extend 2D output model beyond preview to full result graphics
5. Add export/report formats beyond simple JSON/CSV if required by legacy flow

### Phase E. 3D completion

1. Keep improving Three.js scene semantics
2. Add terrain source handling and scene summary consistency
3. Add more explicit scene payload contract for future mesh builder work
4. Add layer visibility persistence and richer scene metadata

### Phase F. PowerGrid merge readiness

1. Normalize route names, labels, and permission codes
2. Normalize response contracts and controller naming
3. Remove old placeholder or duplicate patterns
4. Prepare module split points for later merge into `PowerGrid`

### Phase G. Validation and release readiness

1. Run frontend install/build validation
2. Add backend tests where practical
3. Create regression samples from legacy data
4. Verify old critical workflows against new BS flows
5. Flip default persistence mode from `memory` to `db` once stable

## 7. Definition of Done

The renovation is done only when all of the following are true:

- project, node, data prep, tower, leg, calculation, and 3D flows are usable
  through the BS system
- core reads and writes are backed by MySQL, not in-memory placeholders
- frontend builds successfully
- backend compiles successfully
- the system can run with `foundation.persistence.mode=db`
- old desktop behavior needed by users is covered by web flows or explicitly
  documented as intentionally removed
- the codebase is structurally ready to merge into `PowerGrid`

## 8. Validation Rules

Run these checks as work proceeds:

### Backend

- `mvn -q -f backend/pom.xml -pl powergrid-foundation-admin -am compile`

### Frontend

When dependencies are installed:

- `npm install` in `frontend`
- `npm run build` in `frontend`

### Static checks

- verify new mapper XML files exist
- verify new view files exist
- verify routes point to real pages

## 9. Environment Expectations

Expected environment variables for DB mode:

- `FOUNDATION_DB_URL`
- `FOUNDATION_DB_USERNAME`
- `FOUNDATION_DB_PASSWORD`

If these are unavailable, continue building against `memory` mode but keep DB
paths complete and ready.

## 10. Editing Discipline

- Prefer small, reversible changes
- Keep new files ASCII unless the file already uses Unicode meaningfully
- Do not delete old fallback logic until DB-backed logic is proven stable
- Do not reintroduce direct SQL in controllers or frontend code
- Keep UI, business logic, and persistence separated

## 11. Immediate Next Step

If resuming from this file, the next priority is:

1. finish DB-backed write paths for tower and survey flows
2. then finish DB-backed data prep import reporting
3. then validate frontend build

