# caloffound

BS renovation repository for the legacy `caloffoundClient` desktop system.

## Stack

- Frontend: Vue2 + Vue Router + Vuex + Element UI + Three.js
- Backend: Spring Boot 3 + MyBatis
- Database: legacy MySQL schema used by the old desktop client

## Current Default

- Persistence mode: `db`
- Backend port: `8088`
- Frontend build: validated

## Key Docs

- Architecture plan: `/docs/architecture/RENOVATION_PLAN.md`
- Legacy schema notes: `/docs/database/LEGACY_SCHEMA.md`
- Configuration: `/docs/operations/CONFIGURATION.md`
- Regression guide: `/docs/testing/REGRESSION_PLAN.md`
- Runbook: `/docs/operations/RUNBOOK.md`

## Notes

- The repository keeps a limited `memory` fallback only for controlled transition code paths.
- Mainline reads and writes are now expected to run against the legacy MySQL database.
