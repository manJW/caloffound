# Configuration

## Persistence Mode

`foundation.persistence.mode` controls the active persistence strategy.

Allowed values:

- `db`: default. Read and write the legacy MySQL schema used by the old desktop client.
- `memory`: transitional sample mode. Use only for isolated UI demos or local smoke checks without a database.

Unknown or blank values are parsed as `db`.

## Legacy MySQL Connection

The backend reads the legacy MySQL connection from environment variables when provided:

- `FOUNDATION_DB_URL`
- `FOUNDATION_DB_USERNAME`
- `FOUNDATION_DB_PASSWORD`

The application defaults are defined in:

- `backend/powergrid-foundation-admin/src/main/resources/application.yml`

The default local connection mirrors the legacy desktop client's `APPConfig.json` and targets
`127.0.0.1:3306/dceddata`. Override it with environment variables for shared, test, or production databases.

## Expected Production Direction

The mainline system should run with:

```yaml
foundation:
  persistence:
    mode: db
```

`memory` mode remains only as a controlled fallback while the renovation is being validated.

## Verification

After startup, confirm the effective mode and database visibility:

- `GET /foundation/status`
- `GET /foundation/status/smoke`

The frontend overview page shows the same smoke snapshot and warns when persistence mode is not `db`.
