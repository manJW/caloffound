# 模块地图

## 仓库结构

```text
backend/
  powergrid-foundation-admin
  powergrid-foundation-common
  powergrid-foundation-core
frontend/
docs/
```

## 后端模块

- `powergrid-foundation-admin`
  - Web 启动入口
  - 基础健康检查接口
  - 后续承接认证接入、菜单、上传、基础模块装配
- `powergrid-foundation-common`
  - 通用返回体
  - 统一常量与枚举
  - 后续承接异常、分页、任务状态等公共模型
- `powergrid-foundation-core`
  - foundation 业务域
  - 后续承接项目、节点、数据准备、塔型、计算、2D/3D 数据接口

## 前端模块

- `src/router`
  - foundation 路由入口
- `src/store`
  - 基础状态管理
- `src/views/foundation/project`
- `src/views/foundation/data-prep`
- `src/views/foundation/tower`
- `src/views/foundation/calc`
- `src/views/foundation/scene`

## 并轨规则

- Java 包前缀统一：`com.powergrid.foundation`
- 前端路由统一：`/foundation/*`
- 权限码统一：`foundation:*:*`
