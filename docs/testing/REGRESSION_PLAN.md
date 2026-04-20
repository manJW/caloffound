# Regression Plan

## Scope
- Project and node CRUD against the legacy MySQL schema.
- Survey type, survey points, and TIF write chains.
- Tower body assignment, project leg rules, and four-leg persistence.
- Foundation calculation request, preview, execution, detail, and export.
- Three.js scene summary and structured scene payload.

## Smoke Checks
1. Start the backend with the target legacy MySQL datasource.
2. Call `GET /foundation/status` and confirm `persistenceMode = db`.
3. Call `GET /foundation/status/smoke` and confirm project/node counts are non-zero.
4. Call `GET /foundation/projects/{projectId}/nodes/{nodeId}/regression-snapshot` for one representative node.
5. Open the frontend overview, project, data-prep, tower, calc, and scene pages.
6. Optional scripted export:
   - `powershell -ExecutionPolicy Bypass -File .\tools\regression\export-regression.ps1 -BaseUrl http://localhost:8088 -ProjectId <projectId> -NodeId <nodeId>`

`memory` mode is not an acceptance target. Use it only when checking frontend behavior without a legacy MySQL connection.

## Data Regression Samples
- `/samples/regression/project-smoke-snapshot.json`
- `/samples/regression/node-regression-snapshot.json`
- `/samples/regression/calc-detail-sample.json`
- `/samples/regression/scene-payload-sample.json`
- `/samples/regression/RUN_TEMPLATE.md`

These files are baseline payload examples for manual and scripted comparison. Update them only when the API shape changes intentionally.

## Recommended Comparison Flow
1. Pick one legacy project and one node with survey points.
2. Compare the BS project summary and node detail with the old client.
3. Fetch the regression snapshot endpoint and archive the JSON for the chosen node.
4. Compare survey points before and after one edit round-trip.
5. Compare tower assignment and four-leg records before and after one update.
6. Run one foundation calculation and compare summary, detail, export, and graphics.
7. Load the 3D scene payload and compare terrain source, blockers, layer counts, and camera defaults.
8. Record the run using `/samples/regression/RUN_TEMPLATE.md`.

## Scripted Capture

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\regression\export-regression.ps1 `
  -BaseUrl http://localhost:8088 `
  -ProjectId PRJ-DEMO-001 `
  -NodeId NODE-001
```
