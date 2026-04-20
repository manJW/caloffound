<template>
  <el-row :gutter="16">
    <el-col :span="14">
      <el-card shadow="never">
        <div
          slot="header"
          class="card-header"
        >
          <span>数据准备</span>
          <div class="toolbar-actions">
            <span class="subtle">{{ projectContext ? projectContext.projectName : '请先选择项目' }}</span>
            <input
              ref="batchFileInput"
              type="file"
              class="hidden-input"
              accept=".txt,.csv"
              multiple
              @change="importSurveyBatchFiles"
            >
            <el-button
              size="mini"
              :disabled="!projectContext"
              @click="openBatchImportDialog"
            >
              批量导入
            </el-button>
          </div>
        </div>
        <template v-if="projectContext">
          <el-alert
            v-if="batchReport"
            :title="batchReport"
            type="success"
            :closable="true"
            class="batch-report"
          />
          <el-table
            v-loading="loading"
            :data="resources"
            border
            highlight-current-row
            @current-change="handleNodeChange"
          >
            <el-table-column
              prop="towerNo"
              label="杆塔号"
              width="90"
            />
            <el-table-column
              prop="stake"
              label="桩号"
              width="110"
            />
            <el-table-column
              label="测量类型"
              min-width="160"
            >
              <template slot-scope="{ row }">
                <el-select
                  v-model="row.surveyType"
                  size="mini"
                  placeholder="测量类型"
                  @change="saveSurveyType(row)"
                >
                  <el-option
                    label="塔基断面"
                    value="Tower section"
                  />
                  <el-option
                    label="激光雷达"
                    value="Lidar"
                  />
                  <el-option
                    label="其他测量"
                    value="Other survey"
                  />
                </el-select>
              </template>
            </el-table-column>
            <el-table-column
              label="点数"
              width="90"
            >
              <template slot-scope="{ row }">
                {{ row.surveyPoints ? row.surveyPoints.length : 0 }}
              </template>
            </el-table-column>
            <el-table-column
              label="TIF"
              width="80"
            >
              <template slot-scope="{ row }">
                <el-tag
                  size="mini"
                  :type="row.hasTifBlob ? 'success' : 'info'"
                >
                  {{ row.hasTifBlob ? '就绪' : '缺失' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column
              prop="sceneStatus"
              label="场景状态"
              width="120"
            >
              <template slot-scope="{ row }">
                {{ zh(row.sceneStatus) }}
              </template>
            </el-table-column>
          </el-table>
        </template>
        <el-empty
          v-else
          description="未选择项目"
        />
      </el-card>
    </el-col>
    <el-col :span="10">
      <el-card shadow="never">
        <div
          slot="header"
          class="card-header"
        >
          <span>测量点编辑</span>
          <div class="toolbar-actions">
            <input
              ref="fileInput"
              type="file"
              class="hidden-input"
              accept=".txt,.csv"
              @change="importSurveyFile"
            >
            <input
              ref="tifInput"
              type="file"
              class="hidden-input"
              accept=".tif,.tiff"
              @change="importTifFile"
            >
            <el-button
              size="mini"
              :disabled="!activeNode"
              @click="openImportDialog"
            >
              导入文件
            </el-button>
            <el-button
              size="mini"
              :disabled="!activeNode"
              @click="downloadSurveyText"
            >
              导出文本
            </el-button>
            <el-button
              size="mini"
              :disabled="!activeNode"
              @click="openTifDialog"
            >
              上传 TIF
            </el-button>
            <el-button
              size="mini"
              :disabled="!activeNode || !activeNode.hasTifBlob"
              @click="removeTif"
            >
              清除 TIF
            </el-button>
            <el-button
              type="primary"
              size="mini"
              :disabled="!activeNode || saving"
              :loading="saving"
              @click="saveSurveyPoints"
            >
              保存测量点
            </el-button>
          </div>
        </div>
        <template v-if="activeNode">
          <p class="summary-line">
            <strong>节点：</strong> {{ activeNode.towerNo }} / {{ activeNode.stake }}
          </p>
          <p class="summary-line">
            <strong>测量类型：</strong> {{ zh(activeNode.surveyType) }}
          </p>
          <el-input
            v-model="pointsText"
            type="textarea"
            :rows="16"
            placeholder="每行一个测量点，格式：x,y,z"
          />
        </template>
        <el-empty
          v-else
          description="请选择节点后编辑测量点"
        />
      </el-card>
      <el-card
        class="history-card"
        shadow="never"
      >
        <div
          slot="header"
          class="card-header"
        >
          <span>导入历史</span>
        </div>
        <el-table
          v-if="importHistory.length"
          :data="importHistory"
          size="mini"
          border
          max-height="260"
        >
          <el-table-column
            prop="occurredAt"
            label="时间"
            min-width="170"
          />
          <el-table-column
            prop="eventType"
            label="事件"
            width="110"
          />
          <el-table-column
            prop="status"
            label="状态"
            width="80"
          />
          <el-table-column
            prop="target"
            label="目标"
            width="100"
          />
          <el-table-column
            prop="message"
            label="消息"
            min-width="180"
          />
        </el-table>
        <el-empty
          v-else
          description="暂无导入历史"
        />
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import {
  clearNodeTif,
  getSurveyPoints,
  importSurveyBatch,
  listImportHistory,
  listTerrainResources,
  replaceSurveyPoints,
  uploadNodeTif,
  updateSurveyType
} from '@/api/foundation/dataPrep'
import { zh } from '@/utils/foundationI18n'

export default {
  name: 'FoundationDataPrepPage',
  data() {
    return {
      loading: false,
      saving: false,
      resources: [],
      activeNode: null,
      pointsText: '',
      batchReport: '',
      importHistory: []
    }
  },
  computed: {
    projectContext() {
      return this.$store.state.foundation.projectContext
    }
  },
  watch: {
    projectContext: {
      immediate: true,
      handler(value) {
        if (value && value.projectId) {
          this.loadResources()
          this.loadImportHistory()
        } else {
          this.resources = []
          this.activeNode = null
          this.pointsText = ''
          this.batchReport = ''
          this.importHistory = []
        }
      }
    }
  },
  methods: {
    zh,
    async loadResources() {
      if (!this.projectContext) return
      this.loading = true
      try {
        const response = await listTerrainResources(this.projectContext.projectId)
        this.resources = response.data.data || []
        if (this.resources.length > 0) {
          await this.loadNodeDocument(this.resources[0])
        } else {
          this.activeNode = null
          this.pointsText = ''
        }
      } finally {
        this.loading = false
      }
    },
    async loadImportHistory() {
      if (!this.projectContext) return
      const response = await listImportHistory(this.projectContext.projectId)
      this.importHistory = response.data.data || []
    },
    async loadNodeDocument(row) {
      this.activeNode = row
      const response = await getSurveyPoints(this.projectContext.projectId, row.nodeId)
      const document = response.data.data
      this.pointsText = (document.lines || []).join('\n')
      this.$store.dispatch('foundation/setNodeContext', {
        nodeId: row.nodeId,
        towerNo: row.towerNo,
        stake: row.stake,
        surveyType: row.surveyType
      })
    },
    async handleNodeChange(row) {
      if (!row) return
      await this.loadNodeDocument(row)
    },
    async saveSurveyType(row) {
      await updateSurveyType(this.projectContext.projectId, row.nodeId, row.surveyType)
      this.$message.success('测量类型已更新')
      await this.loadImportHistory()
      if (this.activeNode && this.activeNode.nodeId === row.nodeId) {
        this.activeNode = { ...row }
      }
    },
    async saveSurveyPoints() {
      if (!this.activeNode) return
      this.saving = true
      try {
        await replaceSurveyPoints(this.projectContext.projectId, this.activeNode.nodeId, {
          surveyType: this.activeNode.surveyType || 'Tower section',
          lines: this.pointsText.split('\n')
        })
        this.$message.success('测量点已保存')
        await this.loadResources()
        await this.loadImportHistory()
      } finally {
        this.saving = false
      }
    },
    openImportDialog() {
      if (!this.activeNode) return
      this.$refs.fileInput.click()
    },
    openTifDialog() {
      if (!this.activeNode) return
      this.$refs.tifInput.click()
    },
    async importSurveyFile(event) {
      const [file] = event.target.files || []
      if (!file) return
      const text = await file.text()
      this.pointsText = text.replace(/\r\n/g, '\n').trim()
      this.$message.success(`已加载 ${file.name}`)
      event.target.value = ''
    },
    downloadSurveyText() {
      if (!this.activeNode) return
      const blob = new Blob([this.pointsText || ''], { type: 'text/plain;charset=utf-8' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${this.activeNode.stake || this.activeNode.nodeId}-survey.txt`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    },
    async importTifFile(event) {
      const [file] = event.target.files || []
      if (!file || !this.activeNode) return
      const contentBase64 = await this.readFileAsBase64(file)
      await uploadNodeTif(this.projectContext.projectId, this.activeNode.nodeId, {
        fileName: file.name,
        contentBase64
      })
      this.$message.success(`已上传 ${file.name}`)
      await this.loadResources()
      await this.loadImportHistory()
      await this.loadNodeDocument(this.resources.find(item => item.nodeId === this.activeNode.nodeId) || this.activeNode)
      event.target.value = ''
    },
    async removeTif() {
      if (!this.activeNode) return
      await clearNodeTif(this.projectContext.projectId, this.activeNode.nodeId)
      this.$message.success('TIF 已清除')
      await this.loadResources()
      await this.loadImportHistory()
      await this.loadNodeDocument(this.resources.find(item => item.nodeId === this.activeNode.nodeId) || this.activeNode)
    },
    openBatchImportDialog() {
      if (!this.projectContext) return
      this.$refs.batchFileInput.click()
    },
    async importSurveyBatchFiles(event) {
      const files = Array.from(event.target.files || [])
      if (!files.length || !this.projectContext) return
      const items = []
      for (const file of files) {
        const key = file.name.replace(/\.[^.]+$/, '').toLowerCase()
        const row = this.resources.find(item => {
          return [item.nodeId, item.towerNo, item.stake]
            .filter(Boolean)
            .map(value => value.toLowerCase().replace(/[+]/g, ''))
            .includes(key.replace(/[+]/g, ''))
        })
        if (!row) {
          continue
        }
        const text = await file.text()
        items.push({
          nodeId: row.nodeId,
          surveyType: row.surveyType || 'Tower section',
          sourceName: file.name,
          lines: text.replace(/\r\n/g, '\n').split('\n')
        })
      }
      if (!items.length) {
        this.$message.warning('没有文件匹配当前项目节点')
        event.target.value = ''
        return
      }
      const response = await importSurveyBatch(this.projectContext.projectId, { items })
      const result = response.data.data
      this.batchReport = `已导入 ${result.importedCount} 个，跳过 ${result.skippedCount} 个`
      this.$message.success(this.batchReport)
      await this.loadResources()
      await this.loadImportHistory()
      event.target.value = ''
    },
    readFileAsBase64(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.onload = () => {
          const result = typeof reader.result === 'string' ? reader.result : ''
          resolve(result.split(',').pop() || '')
        }
        reader.onerror = () => reject(reader.error)
        reader.readAsDataURL(file)
      })
    }
  }
}
</script>

<style scoped>
.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.subtle {
  color: #909399;
  font-size: 12px;
}

.summary-line {
  margin: 0 0 8px;
}

.hidden-input {
  display: none;
}

.batch-report {
  margin-bottom: 12px;
}

.history-card {
  margin-top: 16px;
}
</style>
