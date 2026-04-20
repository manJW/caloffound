# Legacy Desktop Retirement Checklist

Date: 2026-04-20

## Retirement Gate

The desktop client should not be deleted immediately. Treat it as a locked reference source until regression evidence is sufficient for production operations.

## Required Before Disabling Desktop Use

- [x] BS backend connects to the legacy MySQL database in `db` mode.
- [x] Project and node reads return real legacy data.
- [x] Project and node write APIs exist.
- [x] Data preparation read/write APIs exist.
- [x] Tower assignment and leg read/write APIs exist.
- [x] Calculation execution, history, detail, and export are available in BS.
- [x] Calculation records persist in MySQL through `foundation_calc_record`.
- [x] Batch calculation writes one result record per node.
- [x] 2D preview and result graphics render in the browser.
- [x] 3D scene payload and Three.js page are available.
- [x] Final browser smoke has no console errors.
- [ ] Production users confirm representative old desktop workflows in the browser.
- [ ] Production deployment auth and backup policy are approved.
- [ ] Legacy-vs-BS numerical regression samples are archived for each major foundation type.

## Desktop Freeze Rules

- Do not add new business features to the Python desktop client.
- Only allow emergency bug fixes that protect current production operation.
- Record any emergency desktop-only fix as a BS backlog item.
- Keep the desktop repository available for source-level comparison until production acceptance is signed off.

## Data Safety Rules

- Back up the legacy MySQL database before enabling broad BS write access.
- Run write operations first against a copied database when possible.
- Keep `foundation_calc_record` as additive data; it does not replace legacy engineering source tables.
- For project/node/tower/leg/survey writes, validate on representative projects before bulk operation.

## Cutover Steps

1. Run backend build and frontend build.
2. Start backend in `db` mode.
3. Run `/foundation/status/smoke`.
4. Run one representative project/node browser acceptance flow.
5. Run one single calculation and one batch calculation.
6. Export regression snapshot and archive the run report.
7. Announce BS as the main entry point.
8. Restrict desktop client to fallback/reference use.

