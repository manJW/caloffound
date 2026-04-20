# Legacy Database Notes

This document captures the first batch of legacy MySQL tables referenced by the
desktop client and mapped into the new BS backend.

## Core project tables

- `dced_prj_prjinfo`
  - project header
  - fields seen in desktop SQL:
    - `ZPDIPRJID`
    - `ZPDIPRJNCODE`
    - `ZPDIPRJNAME`
    - `ZPDISTARTADRESS`
    - `ZPDIENDADRESS`

- `dced_prj_linenode`
  - node / tower stake records inside a project
  - fields seen in desktop SQL:
    - `ZPDIPRJLINENODEID`
    - `ZPDIPRJID`
    - `ZPDITOWERNO`
    - `ZPDISTAKE`
    - `ZPDIORDER`

- `dced_prj_electric`
  - electric / tower assignment data bound to line nodes
  - fields seen in desktop SQL:
    - `ZPDIELCTRICID`
    - `ZPDIPRJLINENODEID`
    - `ZPDITOWERTYPE`
    - `ZPDITOWERBODYID`
    - `ZPDIPOSHEIGHT`
    - `ZPDIPOSHEIGHTADJUST`
    - `ZPDILON`
    - `ZPDILAT`

## Survey / terrain tables

- `dced_prj_survey`
  - survey header for a node
  - fields seen in desktop SQL:
    - `ZPDISURVERID`
    - `ZPDILINENIODEID`
    - `ZPDISURVERTYPE`
    - `ZPDITIFFile`
    - `ZPDIresolution`
    - `ZPDIMARK`

- `dced_prj_surveyposition`
  - survey point set under one survey header
  - fields seen in desktop SQL:
    - `ZPDIPOSITIONID`
    - `ZPDISURVERID`
    - `ZPDIPOSX`
    - `ZPDIPOSY`
    - `ZPDIPOSZ`

## Tower / leg tables

- `dced_prj_leginfo`
  - leg configuration per node
  - fields seen in desktop SQL:
    - `ZPDILEGID`
    - `ZPDILEGNAME`
    - `ZPDILEGLINENODEID`
    - `ZPDILZLTHEIGHT`
    - `ZPDIMARK`

- `dced_prj_legsetconfig`
  - project level leg configuration rules
  - fields seen in desktop SQL:
    - `ZPDILEGSETID`
    - `ZPDIPRJID`
    - `ZPDIEXTRACTHEIGHT`
    - `ZPDIHEIGHTALLOW`
    - `ZPDIHEIGHTSTEP`
    - `ZPDIMINLZKT`
    - `ZPDIMAXLZKT`
    - `ZPDIDADO`

## Reference / support tables

- `dced_project_towertype`
- `dced_project_towertype`
  - project scoped tower type metadata
  - fields seen in desktop SQL:
    - `ZPDITOWERTYPEID`
    - `ZPDITOWERTYPENAME`
    - `ZPDIPROJECTID`
    - `ZPDILEGSHOWTYPE`
- `dced_project_towerbody`
  - project scoped tower body reference rows
  - fields seen in desktop SQL:
    - `ZPDITOWERBODYID`
    - `ZPDITOWERETYPEID`
    - `ZPDICALLHEIGHT`
    - `ZPDIBODYHEIGHT`
- `dced_commodel_towertype`
- `dced_sys_log`
- `dced_sys_version`

## New BS-owned tables

- `foundation_calc_record`
  - BS calculation history and export payloads
  - created by the new backend when `foundation.persistence.mode=db`
  - DDL source: `docs/database/FOUNDATION_CALC_RECORD.sql`
  - stores:
    - calculation input values JSON
    - result summary JSON
    - derived result JSON
    - 2D graphics JSON
    - export JSON

## Migration baseline

The BS repository now has active MyBatis coverage for:

- project and node reads/writes
- survey type / survey point reads/writes
- node TIF upload / clear on `dced_prj_survey`
- tower assignment reads/writes
- tower leg reads/writes
- project leg rule reads/writes
- default tower body lookup against project reference tables
- calculation record persistence in `foundation_calc_record`

The `foundation.persistence.mode` switch still exists because the legacy-memory
fallback has not been completely retired yet.
