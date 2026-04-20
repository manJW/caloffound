# Final Migration Report

Date: 2026-04-20

## Conclusion

The main caloffound desktop workflow has been migrated into the BS baseline under `E:\4developwork\caloffound`.

The new implementation is no longer a direct Python desktop code transplant. It is a Spring Boot 3 + MyBatis backend and Vue2 + Element UI frontend that connects to the legacy MySQL schema in `db` mode. 2D rendering is browser-side payload rendering, and 3D rendering is handled by a Three.js scene page fed by backend scene DTOs.

## Migrated Scope

- Project and node management: project list/detail, project CRUD, node list/detail, node CRUD, and node regression snapshot.
- Data preparation: survey type read/write, survey point read/write, single text import, batch import, TIF metadata/blob write and clear, import history.
- Tower and legs: tower type summary, assignment list, body assignment update, default body suggestion/apply, four-leg load/save, auto-fill baseline, project leg rule read/write.
- Foundation calculation: schema-driven inputs, default template, preview, single execution, batch execution, MySQL result persistence, history, detail, JSON/CSV export.
- 2D: preview geometry and persisted result graphics payloads rendered by the frontend result page.
- 3D: scene node list, scene payload, fallback terrain, tower body, four legs, helper layers, blockers, camera defaults, Three.js rendering page.
- Operations and regression: runbook, configuration docs, legacy schema notes, regression plan, final acceptance checklist, smoke endpoint, regression export helper.

## Live Database Baseline

The current backend default is `foundation.persistence.mode=db`.

Smoke snapshot against the legacy MySQL database:

- Projects: 22
- Nodes: 2375
- Ready scenes: 1994
- Blocked scenes: 0

Representative validation project:

- Project: `浙江环网`
- Project ID: `8dfe408c-8f6f-11ef-a7ac-2c6dc19d600b`
- Node: `L1`
- Node ID: `e674e3e1-9046-11ef-abec-2c6dc19d600b`

## Browser Acceptance Notes

The Vue dev server was validated through a real browser session at `http://127.0.0.1:8080`.

- Overview page loads DB smoke data.
- Data Prep page lists legacy nodes after the large terrain resource request completes.
- Tower Library page loads tower types, assignments, legs, and leg-rule controls.
- Calculation page loads schema, history, result detail, exports, and 2D graphics tabs.
- 3D Scene page renders a Three.js canvas and displays the ready scene banner for `浙江环网 / L1`.
- Browser console reported no errors or warnings during the final 3D validation.
- Network requests for foundation APIs returned HTTP 200.

## Fixes Made During Final Pass

- Unified 3D scene list and scene detail launch rules.
- `fallback-terrain` now remains launchable when the node is not blocked and four-leg data exists.
- Scene list now reports fallback terrain point count consistently with the scene payload.

## Remaining Work Classification

No main workflow remains unmigrated at the baseline level.

The following are product-hardening tasks, not blockers for the BS migration baseline:

- Replace simplified engineering formulas with formally approved calculation kernels if required by production audit.
- Add more legacy-vs-BS numerical comparison fixtures for each foundation type and project data variant.
- Optimize large project table loading with backend pagination or virtual scrolling.
- Replace development auth with the final production auth model when deployment ownership is decided.
- Expand 3D terrain from fallback/local node scene to full terrain mesh when real DEM/TIF processing rules are finalized.

## Retirement Decision

The Python desktop client can be retained as a reference baseline and emergency fallback. New feature work should land in the BS repository first.

