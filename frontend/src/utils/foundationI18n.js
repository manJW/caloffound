const dictionary = {
  active: '已启用',
  planned: '规划中',
  ready: '就绪',
  blocked: '阻塞',
  Pending: '待完善',
  Ready: '就绪',
  Missing: '缺失',
  Yes: '是',
  No: '否',

  memory: '内存模式',
  db: '数据库模式',

  project: '项目',
  'data-prep': '数据准备',
  tower: '塔型腿配',
  calc: '基础计算',
  scene: '三维场景',

  'database-backed-project-and-node-management': '数据库驱动的项目与节点管理',
  'survey-import-and-database-write-chain': '测量数据导入与数据库写入链路',
  'tower-body-and-leg-database-write-chain': '塔身与塔腿数据库写入链路',
  'schema-driven-calculation-and-export': 'Schema 驱动计算与导出',
  'threejs-scene-payload': 'Three.js 场景数据',
  'renovation-in-progress': '翻新迁移中',

  'Tower section': '塔基断面',
  Lidar: '激光雷达',
  'Other survey': '其他测量',
  'fallback-terrain': '兜底地形',
  'survey-points': '测量点',
  'tif-blob': 'TIF 数据',
  missing: '缺失',

  terrain: '地形',
  towerLayer: '塔身',
  legsLayer: '塔腿',
  helpers: '辅助线',
  'Terrain points': '地形点',
  'Tower body': '塔身',
  'Tower legs': '塔腿',
  'Tower footprint': '塔位轮廓',
  points: '点',
  marker: '标记',
  polyline: '折线',

  slab: '板式基础',
  pile: '桩基础',
  rock: '岩石基础',
  calculate: '计算',
  verify: '校核',
  single: '单节点',
  series: '批量',

  'Core Geometry': '核心几何',
  'Load & Material': '荷载与材料',
  'Soil & Boundary': '地质与边界',
  Advanced: '高级参数',
  Special: '专项参数',
  Geometry: '几何',
  Bearing: '承载',
  Material: '材料',
  Validation: '校验',
  Derived: '派生指标',
  Inputs: '输入参数',
  'Result Graphics': '结果图形',
  'Raw JSON': '原始 JSON',

  'Column width b0': '立柱宽度 b0',
  'Foundation width B': '基础宽度 B',
  'Buried depth z': '埋深 z',
  'Exposed height h0': '露高 h0',
  'Total height h': '总高 h',
  'Concrete grade': '混凝土等级',
  'Rebar grade': '钢筋等级',
  'Slope ratio': '边坡比',
  'Foundation type': '基础类型',
  'Preview geometry': '预览几何',
  '2D preview is generated from b0, B, h0 and h.': '二维预览由 b0、B、h0 和 h 生成。',
  'The special section changes with the selected foundation type.': '专项参数会随所选基础类型变化。',
  'Series mode keeps one execution history record per node.': '批量模式会为每个节点保留一条执行历史。',

  'Smoke snapshot is computed from live project and node queries.': '烟测快照来自实时项目与节点查询。',
  'Use this endpoint after changing datasource settings to confirm the BS chain still reads the legacy MySQL schema.': '修改数据源后可使用该接口确认 BS 链路仍能读取旧 MySQL 结构。'
}

export function zh(value) {
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  return dictionary[value] || value
}

export function zhBool(value) {
  return value ? '是' : '否'
}
