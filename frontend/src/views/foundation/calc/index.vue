<template>
  <div class="calc-page">
    <el-row :gutter="16">
      <el-col :span="15">
        <el-card shadow="never">
          <div
            slot="header"
            class="card-header"
          >
            <span>基础计算</span>
            <div>
              <el-button
                size="mini"
                @click="reloadSchema"
              >
                重载参数
              </el-button>
              <el-button
                size="mini"
                :disabled="!projectContext"
                @click="runBatchCalc"
              >
                批量执行
              </el-button>
              <el-button
                type="primary"
                size="mini"
                :disabled="!readyToRun"
                @click="runCalc"
              >
                执行计算
              </el-button>
            </div>
          </div>
          <el-form label-width="120px">
            <el-row :gutter="12">
              <el-col :span="8">
                <el-form-item label="基础类型">
                  <el-select
                    v-model="foundationType"
                    @change="reloadSchema"
                  >
                    <el-option
                      label="板式基础"
                      value="slab"
                    />
                    <el-option
                      label="桩基础"
                      value="pile"
                    />
                    <el-option
                      label="岩石基础"
                      value="rock"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="操作">
                  <el-select
                    v-model="operation"
                    @change="reloadSchema"
                  >
                    <el-option
                      label="计算"
                      value="calculate"
                    />
                    <el-option
                      label="校核"
                      value="verify"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="执行模式">
                  <el-select
                    v-model="iterationMode"
                    @change="reloadSchema"
                  >
                    <el-option
                      label="单节点"
                      value="single"
                    />
                    <el-option
                      label="批量"
                      value="series"
                    />
                  </el-select>
                </el-form-item>
              </el-col>
            </el-row>

            <el-collapse v-model="activeSections">
              <el-collapse-item
                v-for="section in sectionNames"
                :key="section"
                :title="zh(section)"
                :name="section"
              >
                <el-row :gutter="12">
                  <el-col
                    v-for="field in sectionFields(section)"
                    :key="field.fieldKey"
                    :span="field.section === 'Advanced' ? 24 : 12"
                  >
                    <el-form-item
                      :label="zh(field.caption)"
                      :required="field.required"
                    >
                      <el-select
                        v-if="field.controlType === 'select'"
                        v-model="formValues[field.fieldKey]"
                      >
                        <el-option
                          v-for="option in field.options"
                          :key="option"
                          :label="zh(option)"
                          :value="option"
                        />
                      </el-select>
                      <el-input
                        v-else
                        v-model="formValues[field.fieldKey]"
                        :placeholder="zh(field.caption)"
                      />
                    </el-form-item>
                  </el-col>
                </el-row>
              </el-collapse-item>
            </el-collapse>

            <el-divider />

            <el-form-item label="批量节点 ID">
              <el-input
                v-model="batchNodeText"
                type="textarea"
                :rows="4"
                placeholder="每行一个 nodeId，也可用逗号或分号分隔"
              />
              <div class="batch-hint">
                批量执行会复用当前表单参数，并为每个节点写入一条历史记录。
              </div>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
      <el-col :span="9">
        <el-card shadow="never">
          <div
            slot="header"
            class="card-header"
          >
            <span>执行摘要</span>
            <span class="subtle">{{ contextLabel }}</span>
          </div>

          <div class="preview-figure">
            <svg
              viewBox="0 0 420 260"
              class="preview-svg"
            >
              <g
                v-for="shape in previewShapes"
                :key="shape.label"
              >
                <polygon
                  v-if="shape.type === 'polygon'"
                  :points="shape.points"
                  :stroke="shape.stroke"
                  :fill="shape.fill"
                  stroke-width="2"
                />
                <polyline
                  v-else
                  :points="shape.points"
                  :stroke="shape.stroke"
                  fill="none"
                  stroke-width="2"
                />
              </g>
            </svg>
          </div>

          <p class="summary-line">
            <strong>缺失字段：</strong>
            {{ preview.missingFields.length ? preview.missingFields.map(zh).join(', ') : '无' }}
          </p>
          <p class="summary-line">
            <strong>预览提示：</strong>
          </p>
          <ul class="hint-list">
            <li
              v-for="hint in preview.geometryHints"
              :key="hint"
            >
              {{ zh(hint) }}
            </li>
          </ul>

          <el-divider />

          <div
            v-if="batchResult"
            class="batch-panel"
          >
            <h4>批量结果</h4>
            <el-alert
              :title="`已执行 ${batchResult.successCount}/${batchResult.totalCount}，失败 ${batchResult.failedCount}`"
              type="success"
              :closable="false"
              show-icon
            />
            <el-table
              :data="batchResult.records"
              size="mini"
              border
              class="result-table"
            >
              <el-table-column
                prop="recordId"
                label="记录 ID"
                min-width="120"
              />
              <el-table-column
                prop="nodeId"
                label="节点"
                min-width="180"
              />
              <el-table-column
                prop="title"
                label="标题"
                min-width="180"
              />
            </el-table>
            <ul
              v-if="batchResult.messages && batchResult.messages.length"
              class="hint-list"
            >
              <li
                v-for="message in batchResult.messages"
                :key="message"
              >
                {{ message }}
              </li>
            </ul>
            <el-divider />
          </div>

          <h4>结果历史</h4>
          <el-table
            :data="records"
            size="mini"
            border
            highlight-current-row
            @current-change="handleRecordChange"
          >
            <el-table-column
              prop="title"
              label="记录"
              min-width="180"
            />
            <el-table-column
              prop="operation"
              label="模式"
              width="100"
            >
              <template slot-scope="{ row }">
                {{ zh(row.operation) }}
              </template>
            </el-table-column>
          </el-table>

          <el-descriptions
            v-if="activeRecord"
            :column="1"
            border
            size="small"
            class="record-panel"
          >
            <el-descriptions-item label="记录 ID">
              {{ activeRecord.recordId }}
            </el-descriptions-item>
            <el-descriptions-item label="创建时间">
              {{ activeRecord.createdAt }}
            </el-descriptions-item>
          </el-descriptions>
          <div
            v-if="activeRecord"
            class="export-actions"
          >
            <el-button
              size="mini"
              @click="downloadRecord('json')"
            >
              导出 JSON
            </el-button>
            <el-button
              size="mini"
              @click="downloadRecord('csv')"
            >
              导出 CSV
            </el-button>
          </div>

          <el-tabs
            v-if="activeRecord"
            v-model="activeResultSection"
            class="result-tabs"
          >
            <el-tab-pane
              v-for="section in resultSections"
              :key="section.name"
              :label="zh(section.name)"
              :name="section.name"
            >
              <el-table
                :data="section.items"
                size="mini"
                border
                class="result-table"
              >
                <el-table-column
                  prop="label"
                  label="项目"
                  min-width="140"
                >
                  <template slot-scope="{ row }">
                    {{ zh(row.label) }}
                  </template>
                </el-table-column>
                <el-table-column
                  prop="value"
                  label="值"
                  min-width="120"
                />
              </el-table>
            </el-tab-pane>
            <el-tab-pane
              v-for="section in derivedSections"
              :key="`derived-${section.name}`"
              :label="`${zh(section.name)}明细`"
              :name="`derived-${section.name}`"
            >
              <el-table
                :data="section.items"
                size="mini"
                border
                class="result-table"
              >
                <el-table-column
                  prop="label"
                  label="项目"
                  min-width="140"
                >
                  <template slot-scope="{ row }">
                    {{ zh(row.label) }}
                  </template>
                </el-table-column>
                <el-table-column
                  prop="value"
                  label="值"
                  min-width="120"
                />
              </el-table>
            </el-tab-pane>
            <el-tab-pane
              v-if="activeRecordDetail"
              label="输入参数"
              name="Inputs"
            >
              <el-table
                :data="inputValueRows"
                size="mini"
                border
                class="result-table"
              >
                <el-table-column
                  prop="label"
                  label="字段"
                  min-width="140"
                >
                  <template slot-scope="{ row }">
                    {{ zh(row.label) }}
                  </template>
                </el-table-column>
                <el-table-column
                  prop="value"
                  label="值"
                  min-width="120"
                />
              </el-table>
            </el-tab-pane>
            <el-tab-pane
              v-if="activeRecordDetail"
              label="结果图形"
              name="Result Graphics"
            >
              <div class="preview-figure result-graphics">
                <svg
                  viewBox="0 0 420 260"
                  class="preview-svg"
                >
                  <g
                    v-for="shape in detailGraphicShapes"
                    :key="shape.layerKey"
                  >
                    <polygon
                      v-if="shape.type === 'polygon'"
                      :points="shape.points"
                      :stroke="shape.stroke"
                      :fill="shape.fill"
                      stroke-width="2"
                    />
                    <polyline
                      v-else
                      :points="shape.points"
                      :stroke="shape.stroke"
                      fill="none"
                      stroke-width="2"
                    />
                  </g>
                </svg>
              </div>
              <el-table
                :data="graphicRows"
                size="mini"
                border
                class="result-table"
              >
                <el-table-column
                  prop="title"
                  label="图层"
                  min-width="140"
                >
                  <template slot-scope="{ row }">
                    {{ zh(row.title) }}
                  </template>
                </el-table-column>
                <el-table-column
                  prop="type"
                  label="类型"
                  width="120"
                >
                  <template slot-scope="{ row }">
                    {{ zh(row.type) }}
                  </template>
                </el-table-column>
                <el-table-column
                  prop="pointCount"
                  label="点数"
                  width="90"
                />
              </el-table>
            </el-tab-pane>
            <el-tab-pane
              v-if="activeRecordDetail"
              label="原始 JSON"
              name="Raw JSON"
            >
              <el-input
                type="textarea"
                :rows="12"
                :value="activeRecordDetail.rawJson"
                readonly
              />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import {
  executeCalc,
  executeCalcBatch,
  exportCalcRecord,
  getCalcRecord,
  getCalcRecordDetail,
  listCalcRecords,
  loadCalcSchema,
  loadCalcTemplate,
  previewCalc
} from '@/api/foundation/calc'
import { zh } from '@/utils/foundationI18n'

export default {
  name: 'FoundationCalcPage',
  data() {
    return {
      foundationType: 'slab',
      operation: 'calculate',
      iterationMode: 'single',
      schema: { fields: [] },
      formValues: {},
      preview: {
        missingFields: [],
        geometryHints: [],
        shapes: []
      },
      batchNodeText: '',
      batchResult: null,
      records: [],
      activeRecord: null,
      activeRecordDetail: null,
      activeSections: ['Core Geometry', 'Load & Material', 'Special'],
      activeResultSection: 'Geometry'
    }
  },
  computed: {
    projectContext() {
      return this.$store.state.foundation.projectContext
    },
    nodeContext() {
      return this.$store.state.foundation.nodeContext
    },
    readyToRun() {
      return this.projectContext && this.nodeContext
    },
    contextLabel() {
      if (!this.projectContext || !this.nodeContext) {
        return '请先选择项目和节点'
      }
      return `${this.projectContext.projectName} / ${this.nodeContext.towerNo || this.nodeContext.nodeId}`
    },
    sectionNames() {
      const names = this.schema.fields.map(field => field.section)
      return [...new Set(names)]
    },
    previewShapes() {
      return this.normalizeShapePoints(this.preview.shapes || [])
    },
    resultSections() {
      if (!this.activeRecord || !this.activeRecord.items) {
        return []
      }
      const groups = {}
      for (const item of this.activeRecord.items) {
        if (!groups[item.section]) {
          groups[item.section] = []
        }
        groups[item.section].push(item)
      }
      return Object.keys(groups).map(name => ({ name, items: groups[name] }))
    },
    derivedSections() {
      if (!this.activeRecordDetail || !this.activeRecordDetail.derivedItems) {
        return []
      }
      const groups = {}
      for (const item of this.activeRecordDetail.derivedItems) {
        if (!groups[item.section]) {
          groups[item.section] = []
        }
        groups[item.section].push(item)
      }
      return Object.keys(groups).map(name => ({ name, items: groups[name] }))
    },
    inputValueRows() {
      if (!this.activeRecordDetail || !this.activeRecordDetail.inputValues) {
        return []
      }
      return Object.keys(this.activeRecordDetail.inputValues).map(key => ({
        label: key,
        value: this.activeRecordDetail.inputValues[key]
      }))
    },
    detailGraphicShapes() {
      return this.normalizeShapePoints(this.activeRecordDetail ? this.activeRecordDetail.graphics : [])
    },
    graphicRows() {
      if (!this.activeRecordDetail || !this.activeRecordDetail.graphics) {
        return []
      }
      return this.activeRecordDetail.graphics.map(item => ({
        title: item.title,
        type: item.type,
        pointCount: (item.points || []).length
      }))
    }
  },
  watch: {
    projectContext: {
      immediate: true,
      handler() {
        this.reloadHistory()
      }
    },
    nodeContext: {
      immediate: true,
      handler() {
        this.reloadHistory()
      }
    }
  },
  created() {
    this.reloadSchema()
  },
  methods: {
    zh,
    normalizeShapePoints(shapes) {
      const width = 420
      const height = 260
      const allPoints = (shapes || []).flatMap(shape => shape.points || [])
      if (!allPoints.length) {
        return []
      }
      const minX = Math.min(...allPoints.map(point => point.x))
      const maxX = Math.max(...allPoints.map(point => point.x))
      const minY = Math.min(...allPoints.map(point => point.y))
      const maxY = Math.max(...allPoints.map(point => point.y))
      const xSpan = Math.max(maxX - minX, 1)
      const ySpan = Math.max(maxY - minY, 1)
      const scale = Math.min((width - 40) / xSpan, (height - 40) / ySpan)

      return (shapes || []).map(shape => ({
        ...shape,
        points: (shape.points || [])
          .map(point => {
            const x = 20 + (point.x - minX) * scale
            const y = 20 + (point.y - minY) * scale
            return `${x.toFixed(2)},${(height - y).toFixed(2)}`
          })
          .join(' ')
      }))
    },
    async reloadSchema() {
      const [schemaResponse, templateResponse] = await Promise.all([
        loadCalcSchema({
          foundationType: this.foundationType,
          operation: this.operation,
          iterationMode: this.iterationMode
        }),
        loadCalcTemplate({
          foundationType: this.foundationType
        })
      ])
      this.schema = schemaResponse.data.data
      const template = templateResponse.data.data
      this.formValues = { ...(template.values || {}) }
      for (const field of this.schema.fields) {
        if (!(field.fieldKey in this.formValues)) {
          this.$set(this.formValues, field.fieldKey, field.defaultValue || '')
        }
      }
      await this.reloadPreview()
    },
    sectionFields(section) {
      return this.schema.fields.filter(field => field.section === section)
    },
    buildPayload() {
      return {
        projectId: this.projectContext ? this.projectContext.projectId : '',
        nodeId: this.nodeContext ? this.nodeContext.nodeId : '',
        foundationType: this.foundationType,
        operation: this.operation,
        iterationMode: this.iterationMode,
        values: this.formValues
      }
    },
    buildBatchPayload() {
      return {
        projectId: this.projectContext ? this.projectContext.projectId : '',
        nodeIds: this.batchNodeIds(),
        foundationType: this.foundationType,
        operation: this.operation,
        iterationMode: this.iterationMode,
        values: this.formValues
      }
    },
    batchNodeIds() {
      return (this.batchNodeText || '')
        .split(/[\n,;]+/)
        .map(item => item.trim())
        .filter(Boolean)
    },
    async reloadPreview() {
      if (!this.readyToRun) {
        this.preview = { missingFields: [], geometryHints: [], shapes: [] }
        return
      }
      const response = await previewCalc(this.buildPayload())
      this.preview = response.data.data
    },
    async runCalc() {
      const response = await executeCalc(this.buildPayload())
      this.$message.success('基础计算已执行')
      await this.reloadHistory()
      this.activeRecord = response.data.data
      const detailResponse = await getCalcRecordDetail(this.activeRecord.recordId)
      this.activeRecordDetail = detailResponse.data.data
      this.activeResultSection = this.resultSections[0] ? this.resultSections[0].name : 'Geometry'
      await this.reloadPreview()
    },
    async runBatchCalc() {
      const nodeIds = this.batchNodeIds()
      if (!this.projectContext || nodeIds.length === 0) {
        this.$message.warning('请提供项目和至少一个节点 ID 后再批量执行')
        return
      }
      const response = await executeCalcBatch(this.buildBatchPayload())
      this.batchResult = response.data.data
      this.$message.success(`批量执行完成 ${this.batchResult.successCount}/${this.batchResult.totalCount}`)
      await this.reloadHistory()
    },
    async reloadHistory() {
      if (!this.readyToRun) {
        this.records = []
        this.activeRecord = null
        this.activeRecordDetail = null
        return
      }
      const response = await listCalcRecords({
        projectId: this.projectContext.projectId,
        nodeId: this.nodeContext.nodeId
      })
      this.records = response.data.data || []
      if (this.records.length > 0) {
        const first = await getCalcRecord(this.records[0].recordId)
        this.activeRecord = first.data.data
        const detail = await getCalcRecordDetail(this.records[0].recordId)
        this.activeRecordDetail = detail.data.data
        this.activeResultSection = this.resultSections[0] ? this.resultSections[0].name : 'Geometry'
      } else {
        this.activeRecord = null
        this.activeRecordDetail = null
      }
    },
    async handleRecordChange(row) {
      if (!row) return
      const response = await getCalcRecord(row.recordId)
      this.activeRecord = response.data.data
      const detail = await getCalcRecordDetail(row.recordId)
      this.activeRecordDetail = detail.data.data
      this.activeResultSection = this.resultSections[0] ? this.resultSections[0].name : 'Geometry'
    },
    async downloadRecord(format) {
      if (!this.activeRecord) return
      const response = await exportCalcRecord(this.activeRecord.recordId, format)
      const exportDocument = response.data.data
      const blob = new Blob([exportDocument.content], { type: exportDocument.contentType })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = exportDocument.fileName
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    }
  }
}
</script>

<style scoped>
.calc-page {
  min-height: calc(100vh - 140px);
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.subtle {
  color: #909399;
  font-size: 12px;
}

.summary-line {
  margin: 0 0 8px;
}

.hint-list {
  margin: 0;
  padding-left: 18px;
}

.batch-hint {
  margin-top: 6px;
  color: #909399;
  font-size: 12px;
}

.preview-figure {
  margin-bottom: 12px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: linear-gradient(180deg, #f8fbff 0%, #fff9f0 100%);
  overflow: hidden;
}

.preview-svg {
  display: block;
  width: 100%;
  height: 220px;
}

.record-panel,
.export-actions,
.batch-panel,
.result-tabs,
.result-table {
  margin-top: 16px;
}
</style>
