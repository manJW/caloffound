# Regression Export Tool

Use `export-regression.ps1` to capture the current BS runtime state into JSON files.

## Example

```powershell
powershell -ExecutionPolicy Bypass -File .\tools\regression\export-regression.ps1 `
  -BaseUrl http://localhost:8088 `
  -ProjectId PRJ-DEMO-001 `
  -NodeId NODE-001
```

## Output

The script writes files under `samples/regression/runs/<timestamp>/`:

- `status.json`
- `smoke.json`
- `node-regression-snapshot.json` when `ProjectId` and `NodeId` are provided
