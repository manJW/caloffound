# Runbook

## Backend Startup

1. Set legacy MySQL connection variables when needed:
   - `FOUNDATION_DB_URL`
   - `FOUNDATION_DB_USERNAME`
   - `FOUNDATION_DB_PASSWORD`
2. Confirm persistence mode:
   - default: `foundation.persistence.mode=db`
   - `memory` is only for controlled local sample mode
3. Build/install backend modules:
   - `mvn -f backend/pom.xml -pl powergrid-foundation-admin -am install -DskipTests`
4. Start backend:
   - `mvn -f backend/powergrid-foundation-admin/pom.xml spring-boot:run`
5. Verify:
   - `GET /foundation/status`
   - `GET /foundation/status/smoke`

## Frontend Startup

1. Install dependencies:
   - `npm install`
2. Start dev server:
   - `npm run serve`
3. Production build:
   - `npm run build`

## Recommended Verification Order

1. Open overview page and confirm:
   - persistence mode is `db`
   - smoke counts are non-zero
2. Open project page:
   - load one project
   - load one node
   - export one regression snapshot
3. Open data prep page:
   - confirm survey data loads
4. Open tower page:
   - confirm tower assignment and legs load
5. Open calc page:
   - run one calculation
   - inspect detail and result graphics
6. Open scene page:
   - confirm terrain source, blockers, and layer rendering

## Regression Artifacts

- `/samples/regression/project-smoke-snapshot.json`
- `/samples/regression/node-regression-snapshot.json`
- `/samples/regression/calc-detail-sample.json`
- `/samples/regression/scene-payload-sample.json`
- `/samples/regression/RUN_TEMPLATE.md`

Use these as shape baselines when comparing new BS outputs with the old desktop client.

## Final Acceptance

- `/docs/testing/FINAL_ACCEPTANCE_CHECKLIST.md`

## Configuration Reference

- `/docs/operations/CONFIGURATION.md`

## Scripted Regression Export

- `powershell -ExecutionPolicy Bypass -File .\tools\regression\export-regression.ps1 -BaseUrl http://localhost:8088`
- `powershell -ExecutionPolicy Bypass -File .\tools\regression\export-regression.ps1 -BaseUrl http://localhost:8088 -ProjectId <projectId> -NodeId <nodeId>`
