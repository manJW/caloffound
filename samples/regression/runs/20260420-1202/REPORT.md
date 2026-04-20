# Regression Run Report - 2026-04-20 12:02

## Scope

Validate the BS renovation backend against the legacy MySQL connection used by the Python desktop client.

## Environment

- Repository: `E:\4developwork\caloffound`
- Legacy source config: `E:\4developwork\caloffoundClient\APPConfig.json`
- Backend port: `8088`
- Persistence mode: `db`
- Database target: `127.0.0.1:3306/dceddata`
- Database user: `root`
- Database password: from legacy `APPConfig.json` and redacted in this report

## Actions

- Stopped the previous backend process that was started with the wrong default password.
- Updated backend default datasource password to match the legacy desktop client configuration.
- Confirmed `/foundation/**` routes are open for the current migration validation phase.
- Rebuilt backend modules with Maven.
- Started `powergrid-foundation-admin` from the admin module.
- Called `/foundation/status`.
- Called `/foundation/status/smoke`.

## Results

### `/foundation/status`

- Result: passed
- `success`: `true`
- `persistenceMode`: `db`
- Backend stack reported: `Spring Boot 3 + MyBatis`
- Frontend stack reported: `Vue2 + Element UI`
- Render stack reported: `Three.js`

### `/foundation/status/smoke`

- Result: passed
- `success`: `true`
- `persistenceMode`: `db`
- `projectCount`: `22`
- `nodeCount`: `2375`
- `readySceneCount`: `1994`
- `blockedSceneCount`: `0`

## Interpretation

The new backend can connect to the same local legacy MySQL database configuration used by the old desktop client and can read the project table through the migrated DB-backed service path.

The first smoke attempt reported zero nodes because legacy line nodes are attached to `dced_sys_version.ZPDIVERSIONID`, while the project list exposes parent project IDs from `dced_prj_prjinfo.ZPDIPRJID`. The mapper now resolves the latest project version internally and keeps the public API keyed by parent project ID.

The legacy electric and leg tables store numeric fields as `varchar` and can contain non-numeric text. The mapper now safely casts numeric-looking values and returns `NULL` for dirty values instead of failing the endpoint.

Controller path-variable binding initially failed after packaging because Java parameter names were not retained. Maven compiler configuration now enables `-parameters`.

## Endpoint Samples

- Project list: passed; highest-node project `8dfe408c-8f6f-11ef-a7ac-2c6dc19d600b` reports `331` nodes.
- Project detail: passed for `8dfe408c-8f6f-11ef-a7ac-2c6dc19d600b`.
- Terrain resources: passed; same project returns `331` resources.
- Tower library: passed; same project returns `8` tower types.
- Tower assignments: passed for `towerType=JC31203C`, returning `41` assignments.
- Tower legs: passed for node `e674e3e1-9046-11ef-abec-2c6dc19d600b`, returning `4` legs.
- 3D scene payload: passed for node `e674e3e1-9046-11ef-abec-2c6dc19d600b`, returning `launchable=true`, `4` terrain points, `4` leg points, and `4` layers.
- Calculation schema: passed for `foundationType=slab`, `operation=calculate`, `iterationMode=single`.
- Calculation preview: passed with `0` missing fields and `5` preview shapes.
- Calculation execute: passed with record `CALC-6F0F5328` and `8` summary result items.
- Calculation history/detail/export: passed with `1` history record, `5` derived items, `6` 2D graphics layers, JSON export, and CSV export.
- Calculation persistence: passed; MySQL `foundation_calc_record` contains record `CALC-6F0F5328`.
- Batch calculation: passed for three legacy DB nodes with `3/3` success and `0` failures.
- Batch calculation persistence: passed; MySQL contains `3` recent `series` records, including `CALC-407F5A8E`.
- Browser Overview: passed; DB smoke data displayed.
- Browser Data Prep: passed after large terrain-resource request completed; legacy nodes displayed.
- Browser Tower Library: passed; tower types, assignments, legs, and leg-rule controls displayed.
- Browser Calculation: passed; schema, history, persisted detail, exports, and 2D graphics tabs displayed.
- Browser 3D Scene: passed; `浙江环网 / L1` displayed `Ready`, `fallback-terrain`, `4` terrain points, `4` legs, `4` layers, and a Three.js canvas.
- Browser console/network: passed; no console errors or warnings, foundation API requests returned HTTP 200.

## Final Fixes

- Scene list launchability was aligned with scene payload launchability.
- `fallback-terrain` is now launchable when the node is not blocked and four-leg data exists.
- Scene list fallback terrain point count now reports the same baseline point count as the scene payload.

## Follow-up

- Add deeper numerical regression fixtures for every major foundation type before production sign-off.
- Add backend pagination or frontend virtual scrolling for very large legacy projects.
- Keep the Python desktop client as a locked reference and emergency fallback until operator acceptance is complete.
