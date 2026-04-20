# caloffound 软件翻新实施基线

## 目标

- 将旧 `caloffoundClient` 从高耦合桌面系统翻新为 BS 架构
- 与 `PowerGrid` 的技术路线、菜单、权限、接口风格保持一致
- 保留基础计算、数据准备、塔型腿配、2D/3D 场景等核心业务语义

## 技术路线

- 前端：Vue2 + Vue Router + Vuex + Element UI + Three.js
- 后端：Spring Boot 3 + Spring Security + JWT + MyBatis
- 数据库：沿用现有 MySQL，先逆向梳理
- 文件链路：浏览器上传 + 服务端缓存 + 批处理导入

## 业务域

1. 项目与节点管理
2. 数据准备
3. 塔型与腿配
4. 基础计算
5. 2D 渲染
6. 3D 场景
7. 并轨 PowerGrid 的系统管理项

## 实施顺序

1. 文档与架构冻结
2. 后端骨架
3. 前端骨架
4. 项目与节点
5. 数据准备
6. 塔型与腿配
7. 基础计算
8. 2D
9. 3D
10. 并轨清理与回归验收
