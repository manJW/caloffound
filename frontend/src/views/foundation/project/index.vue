<template>
  <div class="project-page">
    <el-row :gutter="16">
      <el-col :span="14">
        <el-card shadow="never">
          <div
            slot="header"
            class="card-header"
          >
            <span>Projects</span>
            <div>
              <el-button
                type="primary"
                size="mini"
                @click="openProjectCreateDialog"
              >
                New project
              </el-button>
              <el-button
                size="mini"
                @click="loadProjects"
              >
                Refresh
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
              label="Project"
              min-width="180"
            />
            <el-table-column
              prop="projectCode"
              label="Code"
              min-width="120"
            />
            <el-table-column
              prop="startAddress"
              label="Start"
              min-width="140"
            />
            <el-table-column
              prop="endAddress"
              label="End"
              min-width="140"
            />
            <el-table-column
              prop="nodeCount"
              label="Nodes"
              width="80"
            />
            <el-table-column
              label="Actions"
              width="160"
              fixed="right"
            >
              <template slot-scope="{ row }">
                <el-button
                  type="text"
                  size="mini"
                  @click.stop="openProjectEditDialog(row)"
                >
                  Edit
                </el-button>
                <el-button
                  type="text"
                  size="mini"
                  @click.stop="removeProject(row)"
                >
                  Delete
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
            <span>Nodes</span>
            <div>
              <span class="subtle">{{ activeProject ? activeProject.projectName : 'No project selected' }}</span>
              <el-button
                v-if="activeProject"
                type="primary"
                size="mini"
                class="inline-action"
                @click="openNodeCreateDialog"
              >
                New node
              </el-button>
            </div>
          </div>

          <template v-if="activeProject">
            <p class="summary-line">
              <strong>Project code:</strong> {{ activeProject.projectCode }}
            </p>
            <p class="summary-line">
              <strong>Scope:</strong> {{ activeProject.startAddress || '-' }} ~ {{ activeProject.endAddress || '-' }}
            </p>
            <p class="summary-line">
              <strong>Remarks:</strong> {{ activeProject.remarks || '-' }}
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
                label="Tower No."
                width="100"
              />
              <el-table-column
                prop="stake"
                label="Stake"
                width="110"
              />
              <el-table-column
                prop="surveyType"
                label="Survey"
                min-width="110"
              />
              <el-table-column
                prop="towerType"
                label="Tower Type"
                min-width="110"
              />
              <el-table-column
                prop="sceneStatus"
                label="Scene"
                width="90"
              />
              <el-table-column
                label="Actions"
                width="130"
                fixed="right"
              >
                <template slot-scope="{ row }">
                  <el-button
                    type="text"
                    size="mini"
                    @click.stop="openNodeEditDialog(row)"
                  >
                    Edit
                  </el-button>
                  <el-button
                    type="text"
                    size="mini"
                    @click.stop="removeNode(row)"
                  >
                    Delete
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
              <el-descriptions-item label="Node ID">
                {{ activeNode.nodeId }}
              </el-descriptions-item>
              <el-descriptions-item label="Tower No.">
                {{ activeNode.towerNo }}
              </el-descriptions-item>
              <el-descriptions-item label="Stake">
                {{ activeNode.stake }}
              </el-descriptions-item>
              <el-descriptions-item label="Survey Type">
                {{ activeNode.surveyType || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="Tower Type">
                {{ activeNode.towerType || '-' }}
              </el-descriptions-item>
              <el-descriptions-item label="Scene Status">
                {{ activeNode.sceneStatus }}
              </el-descriptions-item>
              <el-descriptions-item label="Position">
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
                Load regression snapshot
              </el-button>
              <el-button
                size="mini"
                type="primary"
                :disabled="!regressionSnapshot"
                @click="downloadRegressionSnapshot"
              >
                Export regression JSON
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
                <span>Regression Snapshot</span>
                <span class="subtle">{{ regressionSnapshot.persistenceMode }}</span>
              </div>
              <el-descriptions
                :column="2"
                border
                size="small"
              >
                <el-descriptions-item label="Tower Body">
                  {{ regressionSnapshot.towerAssignment ? regressionSnapshot.towerAssignment.towerBodyId || '-' : '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="Leg Count">
                  {{ regressionSnapshot.towerLegs ? regressionSnapshot.towerLegs.length : 0 }}
                </el-descriptions-item>
                <el-descriptions-item label="Terrain Source">
                  {{ regressionSnapshot.sceneSummary ? regressionSnapshot.sceneSummary.terrainSource : '-' }}
                </el-descriptions-item>
                <el-descriptions-item label="Launchable">
                  {{ regressionSnapshot.sceneSummary && regressionSnapshot.sceneSummary.launchable ? 'Yes' : 'No' }}
                </el-descriptions-item>
                <el-descriptions-item label="Survey Lines">
                  {{ regressionSnapshot.surveyDocument ? regressionSnapshot.surveyDocument.lines.length : 0 }}
                </el-descriptions-item>
                <el-descriptions-item label="Import Events">
                  {{ regressionSnapshot.recentImportHistory ? regressionSnapshot.recentImportHistory.length : 0 }}
                </el-descriptions-item>
              </el-descriptions>
            </el-card>
          </template>

          <el-empty
            v-else
            description="Select a project to manage nodes"
          />
        </el-card>
      </el-col>
    </el-row>

    <el-dialog
      :title="editingProjectId ? 'Edit project' : 'New project'"
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
          label="Project"
          prop="projectName"
        >
          <el-input v-model="projectForm.projectName" />
        </el-form-item>
        <el-form-item
          label="Code"
          prop="projectCode"
        >
          <el-input v-model="projectForm.projectCode" />
        </el-form-item>
        <el-form-item label="Start">
          <el-input v-model="projectForm.startAddress" />
        </el-form-item>
        <el-form-item label="End">
          <el-input v-model="projectForm.endAddress" />
        </el-form-item>
        <el-form-item label="Remarks">
          <el-input
            v-model="projectForm.remarks"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="projectDialogVisible = false">Cancel</el-button>
        <el-button
          type="primary"
          :loading="savingProject"
          @click="submitProjectForm"
        >Save</el-button>
      </span>
    </el-dialog>

    <el-dialog
      :title="editingNodeId ? 'Edit node' : 'New node'"
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
          label="Tower No."
          prop="towerNo"
        >
          <el-input v-model="nodeForm.towerNo" />
        </el-form-item>
        <el-form-item
          label="Stake"
          prop="stake"
        >
          <el-input v-model="nodeForm.stake" />
        </el-form-item>
        <el-form-item label="Order">
          <el-input-number
            v-model="nodeForm.nodeOrder"
            :min="1"
            :step="1"
            controls-position="right"
          />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="nodeDialogVisible = false">Cancel</el-button>
        <el-button
          type="primary"
          :loading="savingNode"
          @click="submitNodeForm"
        >Save</el-button>
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
        projectName: [{ required: true, message: 'Enter a project name', trigger: 'blur' }],
        projectCode: [{ required: true, message: 'Enter a project code', trigger: 'blur' }]
      },
      nodeRules: {
        towerNo: [{ required: true, message: 'Enter a tower number', trigger: 'blur' }],
        stake: [{ required: true, message: 'Enter a stake', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.loadProjects()
  },
  methods: {
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
      this.$message.success('Regression snapshot loaded')
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
            this.$message.success('Project updated')
          } else {
            await createFoundationProject(this.projectForm)
            this.$message.success('Project created')
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
            this.$message.success('Node updated')
          } else {
            await createFoundationNode(this.activeProject.projectId, this.nodeForm)
            this.$message.success('Node created')
          }
          this.nodeDialogVisible = false
          await this.loadProjectDetail(this.activeProject.projectId)
        } finally {
          this.savingNode = false
        }
      })
    },
    removeProject(row) {
      this.$confirm(`Delete project "${row.projectName}"?`, 'Delete confirmation', {
        type: 'warning'
      }).then(async () => {
        await deleteFoundationProject(row.projectId)
        this.$message.success('Project deleted')
        await this.loadProjects()
      }).catch(() => {})
    },
    removeNode(row) {
      if (!this.activeProject) return
      this.$confirm(`Delete node "${row.towerNo}"?`, 'Delete confirmation', {
        type: 'warning'
      }).then(async () => {
        await deleteFoundationNode(this.activeProject.projectId, row.nodeId)
        this.$message.success('Node deleted')
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
