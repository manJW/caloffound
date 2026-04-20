<template>
  <div class="project-page">
    <el-row :gutter="16">
      <el-col :span="14">
        <el-card shadow="never">
          <div
            slot="header"
            class="card-header"
          >
            <span>项目列表</span>
            <div>
              <el-button
                type="primary"
                size="mini"
                @click="openProjectCreateDialog"
              >
                新建项目
              </el-button>
              <el-button
                size="mini"
                @click="loadProjects"
              >
                刷新
              </el-button>
            </div>
          </div>
          <el-table
            v-loading="loading"
            :data="projects"
            border
            highlight-current-row
            @current-change="handleProjectChange"
          >
            <el-table-column
              prop="projectName"
              label="项目"
              min-width="180"
            />
            <el-table-column
              prop="projectCode"
              label="编号"
              min-width="120"
            />
            <el-table-column
              prop="startAddress"
              label="起点"
              min-width="140"
            />
            <el-table-column
              prop="endAddress"
              label="终点"
              min-width="140"
            />
            <el-table-column
              prop="nodeCount"
              label="节点"
              width="80"
            />
            <el-table-column
              label="操作"
              width="160"
              fixed="right"
            >
              <template slot-scope="{ row }">
                <el-button
                  type="text"
                  size="mini"
                  @click.stop="openProjectEditDialog(row)"
                >
                  编辑
                </el-button>
                <el-button
                  type="text"
                  size="mini"
                  @click.stop="removeProject(row)"
                >
                  删除
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>

      <el-col :span="10">
        <el-card shadow="never">
          <div
            slot="header"
            class="card-header"
          >
            <span>节点列表</span>
            <div>
              <span class="subtle">{{ activeProject ? activeProject.projectName : '未选择项目' }}</span>
              <el-button
                v-if="activeProject"
                type="primary"
                size="mini"
                class="inline-action"
                @click="openNodeCreateDialog"
              >
                新建节点
              </el-button>
            </div>
          </div>

          <template v-if="activeProject">
            <p class="summary-line">
              <strong>项目编号：</strong> {{ activeProject.projectCode }}
            </p>
            <p class="summary-line">
              <strong>范围：</strong> {{ activeProject.startAddress || '-' }} ~ {{ activeProject.endAddress || '-' }}
            </p>
            <p class="summary-line">
              <strong>备注：</strong> {{ activeProject.remarks || '-' }}
            </p>

            <el-table
              :data="activeProject.nodes"
              size="mini"
              border
              highlight-current-row
              @current-change="handleNodeChange"
            >
              <el-table-column
                prop="towerNo"
                label="杆塔号"
                width="100"
              />
              <el-table-column
                prop="stake"
                label="桩号"
                width="110"
              />
              <el-table-column
                prop="surveyType"
                label="测量类型"
                min-width="110"
              >
                <template slot-scope="{ row }">
                  {{ zh(row.surveyType) }}
                </template>
              </el-table-column>
              <el-table-column
                prop="towerType"
                label="塔型"
                min-width="110"
              />
              <el-table-column
                prop="sceneStatus"
                label="场景"
                width="90"
              >
                <template slot-scope="{ row }">
                  {{ zh(row.sceneStatus) }}
                </template>
              </el-table-column>
              <el-table-column
                label="操作"
                width="130"
                fixed="right"
              >
                <template slot-scope="{ row }">
                  <el-button
                    type="text"
                    size="mini"
                    @click.stop="openNodeEditDialog(row)"
                  >
                    编辑
                  </el-button>
                  <el-button
                    type="text"
                    size="mini"
                    @click.stop="removeNode(row)"
                  >
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>

            <el-descriptions
              v-if="activeNode"
              :column="1"
              border
              size="small"
              class="node-panel"
            >
              <el-descriptions-item label="节点 ID">
                {{ activeNode.nodeId }}
              </el-descriptions-item>
              <el-descriptions-item label="杆塔号">
                {{ activeNode.towerNo }}
              </el-descriptions-item>
              <el-descriptions-item label="桩号">
                {{ activeNode.stake }}
              </el-descriptions-item>
              <el-descriptions-item label="测量类型">
                {{ zh(activeNode.surveyType) }}
              </el-descriptions-item>
              <el-descriptions-item label="塔型">
                {{ activeNode.towerType || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="场景状态">
                {{ zh(activeNode.sceneStatus) }}
              </el-descriptions-item>
              <el-descriptions-item label="位置">
                {{ activeNode.positionDescription }}
              </el-descriptions-item>
            </el-descriptions>

            <div
              v-if="activeNode"
              class="node-actions"
            >
              <el-button
                size="mini"
                @click="loadRegressionSnapshot"
              >
                加载回归快照
              </el-button>
              <el-button
                size="mini"
                type="primary"
                :disabled="!regressionSnapshot"
                @click="downloadRegressionSnapshot"
              >
                导出回归 JSON
              </el-button>
            </div>

            <el-card
              v-if="regressionSnapshot"
              shadow="never"
              class="regression-card"
            >
              <div
                slot="header"
                class="card-header"
              >
                <span>回归快照</span>
                <span class="subtle">{{ regressionSnapshot.persistenceMode }}</span>
              </div>
              <el-descriptions
                :column="2"
                border
                size="small"
              >
                <el-descriptions-item label="塔身">
                  {{ regressionSnapshot.towerAssignment ? regressionSnapshot.towerAssignment.towerBodyId || '-' : '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="腿数">
                  {{ regressionSnapshot.towerLegs ? regressionSnapshot.towerLegs.length : 0 }}
                </el-descriptions-item>
                <el-descriptions-item label="地形来源">
                  {{ regressionSnapshot.sceneSummary ? zh(regressionSnapshot.sceneSummary.terrainSource) : '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="可启动">
                  {{ zhBool(regressionSnapshot.sceneSummary && regressionSnapshot.sceneSummary.launchable) }}
                </el-descriptions-item>
                <el-descriptions-item label="测量行数">
                  {{ regressionSnapshot.surveyDocument ? regressionSnapshot.surveyDocument.lines.length : 0 }}
                </el-descriptions-item>
                <el-descriptions-item label="导入事件">
                  {{ regressionSnapshot.recentImportHistory ? regressionSnapshot.recentImportHistory.length : 0 }}
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </template>

          <el-empty
            v-else
            description="请选择项目后管理节点"
          />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      :title="editingProjectId ? '编辑项目' : '新建项目'"
      :visible.sync="projectDialogVisible"
      width="520px"
      @close="resetProjectForm"
    >
      <el-form
        ref="projectForm"
        :model="projectForm"
        :rules="projectRules"
        label-width="96px"
      >
        <el-form-item
          label="项目"
          prop="projectName"
        >
          <el-input v-model="projectForm.projectName" />
        </el-form-item>
        <el-form-item
          label="编号"
          prop="projectCode"
        >
          <el-input v-model="projectForm.projectCode" />
        </el-form-item>
        <el-form-item label="起点">
          <el-input v-model="projectForm.startAddress" />
        </el-form-item>
        <el-form-item label="终点">
          <el-input v-model="projectForm.endAddress" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="projectForm.remarks"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="projectDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="savingProject"
          @click="submitProjectForm"
        >保存</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="editingNodeId ? '编辑节点' : '新建节点'"
      :visible.sync="nodeDialogVisible"
      width="420px"
      @close="resetNodeForm"
    >
      <el-form
        ref="nodeForm"
        :model="nodeForm"
        :rules="nodeRules"
        label-width="96px"
      >
        <el-form-item
          label="杆塔号"
          prop="towerNo"
        >
          <el-input v-model="nodeForm.towerNo" />
        </el-form-item>
        <el-form-item
          label="桩号"
          prop="stake"
        >
          <el-input v-model="nodeForm.stake" />
        </el-form-item>
        <el-form-item label="顺序">
          <el-input-number
            v-model="nodeForm.nodeOrder"
            :min="1"
            :step="1"
            controls-position="right"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="nodeDialogVisible = false">取消</el-button>
        <el-button
          type="primary"
          :loading="savingNode"
          @click="submitNodeForm"
        >保存</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  createFoundationNode,
  createFoundationProject,
  deleteFoundationNode,
  deleteFoundationProject,
  getFoundationNode,
  getFoundationProject,
  getFoundationRegressionSnapshot,
  listFoundationProjects,
  updateFoundationNode,
  updateFoundationProject
} from '@/api/foundation/project'
import { zh, zhBool } from '@/utils/foundationI18n'

export default {
  name: 'FoundationProjectPage',
  data() {
    return {
      loading: false,
      savingProject: false,
      savingNode: false,
      projectDialogVisible: false,
      nodeDialogVisible: false,
      editingProjectId: null,
      editingNodeId: null,
      projects: [],
      activeProject: null,
      activeNode: null,
      regressionSnapshot: null,
      projectForm: {
        projectName: '',
        projectCode: '',
        startAddress: '',
        endAddress: '',
        remarks: ''
      },
      nodeForm: {
        towerNo: '',
        stake: '',
        nodeOrder: 1
      },
      projectRules: {
        projectName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
        projectCode: [{ required: true, message: '请输入项目编号', trigger: 'blur' }]
      },
      nodeRules: {
        towerNo: [{ required: true, message: '请输入杆塔号', trigger: 'blur' }],
        stake: [{ required: true, message: '请输入桩号', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadProjects()
  },
  methods: {
    zh,
    zhBool,
    async loadProjects() {
      this.loading = true
      try {
        const response = await listFoundationProjects()
        this.projects = response.data.data || []
        if (this.projects.length > 0) {
          const currentProjectId = this.activeProject ? this.activeProject.projectId : this.projects[0].projectId
          const selected = this.projects.find(item => item.projectId === currentProjectId) || this.projects[0]
          await this.loadProjectDetail(selected.projectId)
        } else {
          this.activeProject = null
          this.activeNode = null
          this.regressionSnapshot = null
          this.$store.dispatch('foundation/setProjectContext', null)
          this.$store.dispatch('foundation/setNodeContext', null)
        }
      } finally {
        this.loading = false
      }
    },
    async loadProjectDetail(projectId) {
      const response = await getFoundationProject(projectId)
      this.activeProject = response.data.data
      this.$store.dispatch('foundation/setProjectContext', this.activeProject)
      if (this.activeProject.nodes && this.activeProject.nodes.length > 0) {
        const currentNodeId = this.activeNode ? this.activeNode.nodeId : this.activeProject.nodes[0].nodeId
        const selectedNode = this.activeProject.nodes.find(item => item.nodeId === currentNodeId) || this.activeProject.nodes[0]
        await this.loadNodeDetail(selectedNode.nodeId)
      } else {
        this.activeNode = null
        this.regressionSnapshot = null
        this.$store.dispatch('foundation/setNodeContext', null)
      }
    },
    async loadNodeDetail(nodeId) {
      if (!this.activeProject) return
      const response = await getFoundationNode(this.activeProject.projectId, nodeId)
      this.activeNode = response.data.data
      this.regressionSnapshot = null
      this.$store.dispatch('foundation/setNodeContext', this.activeNode)
    },
    async loadRegressionSnapshot() {
      if (!this.activeProject || !this.activeNode) return
      const response = await getFoundationRegressionSnapshot(this.activeProject.projectId, this.activeNode.nodeId)
      this.regressionSnapshot = response.data.data
      this.$message.success('回归快照已加载')
    },
    downloadRegressionSnapshot() {
      if (!this.regressionSnapshot || !this.activeNode) return
      const blob = new Blob([JSON.stringify(this.regressionSnapshot, null, 2)], { type: 'application/json' })
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${this.activeNode.nodeId}-regression-snapshot.json`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url)
    },
    async handleProjectChange(row) {
      if (!row) return
      await this.loadProjectDetail(row.projectId)
    },
    async handleNodeChange(row) {
      if (!row) return
      await this.loadNodeDetail(row.nodeId)
    },
    openProjectCreateDialog() {
      this.projectDialogVisible = true
      this.editingProjectId = null
      this.resetProjectForm()
    },
    openProjectEditDialog(row) {
      this.projectDialogVisible = true
      this.editingProjectId = row.projectId
      this.projectForm = {
        projectName: row.projectName,
        projectCode: row.projectCode,
        startAddress: row.startAddress,
        endAddress: row.endAddress,
        remarks: this.activeProject && this.activeProject.projectId === row.projectId ? this.activeProject.remarks || '' : ''
      }
    },
    openNodeCreateDialog() {
      this.nodeDialogVisible = true
      this.editingNodeId = null
      this.resetNodeForm()
      this.nodeForm.nodeOrder = (this.activeProject && this.activeProject.nodes ? this.activeProject.nodes.length + 1 : 1)
    },
    openNodeEditDialog(row) {
      this.nodeDialogVisible = true
      this.editingNodeId = row.nodeId
      this.nodeForm = {
        towerNo: row.towerNo,
        stake: row.stake,
        nodeOrder: this.resolveNodeOrder(row.nodeId)
      }
    },
    resolveNodeOrder(nodeId) {
      if (!this.activeProject || !this.activeProject.nodes) return 1
      const index = this.activeProject.nodes.findIndex(item => item.nodeId === nodeId)
      return index >= 0 ? index + 1 : this.activeProject.nodes.length + 1
    },
    resetProjectForm() {
      this.projectForm = {
        projectName: '',
        projectCode: '',
        startAddress: '',
        endAddress: '',
        remarks: ''
      }
      if (this.$refs.projectForm) {
        this.$refs.projectForm.clearValidate()
      }
    },
    resetNodeForm() {
      this.nodeForm = {
        towerNo: '',
        stake: '',
        nodeOrder: 1
      }
      if (this.$refs.nodeForm) {
        this.$refs.nodeForm.clearValidate()
      }
    },
    submitProjectForm() {
      this.$refs.projectForm.validate(async valid => {
        if (!valid) return
        this.savingProject = true
        try {
          if (this.editingProjectId) {
            await updateFoundationProject(this.editingProjectId, this.projectForm)
            this.$message.success('项目已更新')
          } else {
            await createFoundationProject(this.projectForm)
            this.$message.success('项目已创建')
          }
          this.projectDialogVisible = false
          await this.loadProjects()
        } finally {
          this.savingProject = false
        }
      })
    },
    submitNodeForm() {
      this.$refs.nodeForm.validate(async valid => {
        if (!valid || !this.activeProject) return
        this.savingNode = true
        try {
          if (this.editingNodeId) {
            await updateFoundationNode(this.activeProject.projectId, this.editingNodeId, this.nodeForm)
            this.$message.success('节点已更新')
          } else {
            await createFoundationNode(this.activeProject.projectId, this.nodeForm)
            this.$message.success('节点已创建')
          }
          this.nodeDialogVisible = false
          await this.loadProjectDetail(this.activeProject.projectId)
        } finally {
          this.savingNode = false
        }
      })
    },
    removeProject(row) {
      this.$confirm(`确认删除项目“${row.projectName}”？`, '删除确认', {
        type: 'warning'
      }).then(async () => {
        await deleteFoundationProject(row.projectId)
        this.$message.success('项目已删除')
        await this.loadProjects()
      }).catch(() => {})
    },
    removeNode(row) {
      if (!this.activeProject) return
      this.$confirm(`确认删除节点“${row.towerNo}”？`, '删除确认', {
        type: 'warning'
      }).then(async () => {
        await deleteFoundationNode(this.activeProject.projectId, row.nodeId)
        this.$message.success('节点已删除')
        await this.loadProjectDetail(this.activeProject.projectId)
      }).catch(() => {})
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

.inline-action {
  margin-left: 8px;
}

.subtle {
  color: #909399;
  font-size: 12px;
}

.summary-line {
  margin: 0 0 8px;
}

.node-panel {
  margin-top: 16px;
}

.node-actions,
.regression-card {
  margin-top: 16px;
}
</style>
