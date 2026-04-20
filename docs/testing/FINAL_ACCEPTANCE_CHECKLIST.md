# Final Acceptance Checklist

Use this checklist to decide whether the BS renovation can replace the legacy desktop client.

## Runtime Baseline

- [x] Backend starts with `foundation.persistence.mode=db`.
- [x] `GET /foundation/status` returns `persistenceMode = db`.
- [x] `GET /foundation/status/smoke` returns non-zero `projectCount`.
- [x] `GET /foundation/status/smoke` returns non-zero `nodeCount`.
- [x] Frontend production build completes successfully.

## Project And Node

- [ ] Project list matches the legacy MySQL project list.
- [ ] Project create, update, and delete work against the legacy table.
- [ ] Node list and node detail match the legacy client.
- [ ] Node create, update, and delete work against the legacy table.
- [ ] Node delete cleans related survey, electric assignment, and leg records as expected.

## Data Preparation

- [ ] Survey type loads correctly for representative nodes.
- [ ] Survey type update round-trips to the legacy database.
- [ ] Survey points load correctly.
- [ ] Survey points edit and save round-trip to the legacy database.
- [ ] Single file survey import works.
- [ ] Batch survey import works.
- [ ] TIF upload works.
- [ ] TIF clear works.
- [ ] Import history records the latest actions.

## Tower And Legs

- [ ] Tower library summary matches the legacy client.
- [ ] Tower assignment list matches the legacy client.
- [ ] Tower body update round-trips to the legacy database.
- [ ] Default tower body lookup and apply work for a representative tower type.
- [ ] Four-leg records load correctly.
- [ ] Four-leg edit and save round-trip to the legacy database.
- [ ] Project leg rule read and write work.
- [ ] Automatic leg fill generates and saves four legs.

## Foundation Calculation

- [ ] Calculation schema loads for each supported foundation type.
- [ ] Template/default parameters load.
- [ ] Preview returns valid 2D geometry.
- [ ] Single calculation executes successfully.
- [ ] Batch calculation executes successfully when applicable.
- [ ] Result summary matches expected legacy meaning.
- [ ] Result detail exposes input snapshot, derived metrics, raw JSON, and graphics.
- [ ] Result export works for JSON and CSV.

## 2D Rendering

- [ ] Quick preview renders correctly in the browser.
- [ ] Result graphics render expected layers.
- [ ] Layer labels and geometry remain readable for representative samples.

## 3D Rendering

- [x] Scene summary loads for representative project nodes.
- [x] Scene payload includes terrain, tower, legs, helpers, camera, and layers.
- [x] Three.js page renders the scene without browser errors.
- [x] Layer toggles are present and wired to scene rendering.
- [x] Terrain source and blockers match the migrated scene DTO state.

## Regression Snapshot

- [ ] `GET /foundation/projects/{projectId}/nodes/{nodeId}/regression-snapshot` works for at least one node.
- [ ] Project page can export the same regression snapshot as JSON.
- [ ] `tools/regression/export-regression.ps1` exports status, smoke, and node snapshot JSON.
- [ ] Snapshot comparison with the legacy client has been recorded.

## Legacy Retirement

- [x] Remaining legacy-only features have been listed.
- [x] Missing legacy features have either been migrated or explicitly accepted as hardening/out-of-scope.
- [ ] Operators know the BS system is the main entry point.
- [x] Legacy desktop client is retained only as a reference or emergency fallback.
