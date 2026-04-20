<template>
  <el-row :gutter="16">
    <el-col :span="8">
      <el-card shadow="never">
        <div
          slot="header"
          class="card-header"
        >
          <span>Tower Types</span>
          <span class="subtle">{{ projectContext ? projectContext.projectName : 'Select a project first' }}</span>
        </div>
        <template v-if="projectContext">
          <el-table
            v-loading="loading"
            :data="towerTypes"
            border
            highlight-current-row
            @current-change="handleTowerTypeChange"
          >
            <el-table-column
              prop="towerType"
              label="Tower Type"
              min-width="120"
            />
            <el-table-column
              prop="nodeCount"
              label="Nodes"
              width="70"
            />
            <el-table-column
              prop="configuredCount"
              label="Configured"
              width="90"
            />
            <el-table-column
              label="Status"
              width="90"
            >
              <template slot-scope="{ row }">
                <el-tag
                  size="mini"
                  :type="row.ready ? 'success' : 'warning'"
                >
                  {{ row.ready ? 'Ready' : 'Pending' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </template>
        <el-empty
          v-else
          description="Project context is not selected"
        />
      </el-card>
    </el-col>

    <el-col :span="8">
      <el-card shadow="never">
        <div
          slot="header"
          class="card-header"
        >
          <span>Assignments</span>
          <div class="toolbar-actions">
            <el-tag
              v-if="defaultSuggestion"
              size="mini"
              :type="defaultSuggestion.available ? 'success' : 'info'"
            >
              {{ defaultSuggestion.available ? defaultSuggestion.towerBodyId : 'No default body' }}
            </el-tag>
            <el-button
              size="mini"
              :disabled="!activeTowerType || !defaultSuggestion || !defaultSuggestion.available"
              @click="applyDefaultBody"
            >
              Apply default
            </el-button>
            <el-button
              type="primary"
              size="mini"
              :disabled="!activeAssignment"
              @click="saveAssignment"
            >
              Save
            </el-button>
          </div>
        </div>
        <template v-if="activeTowerType">
          <el-table
            :data="assignments"
            border
            highlight-current-row
            @current-change="handleAssignmentChange"
          >
            <el-table-column
              prop="towerNo"
              label="Tower No."
              width="90"
            />
            <el-table-column
              prop="stake"
              label="Stake"
              width="110"
            />
            <el-table-column
              label="Tower Body"
              min-width="140"
            >
              <template slot-scope="{ row }">
                <el-input
                  v-model="row.towerBodyId"
                  size="mini"
                />
              </template>
            </el-table-column>
            <el-table-column
              label="Pos Adjust"
              width="120"
            >
              <template slot-scope="{ row }">
                <el-input-number
                  v-model="row.positionAdjust"
                  :precision="2"
                  :step="0.1"
                  size="mini"
                />
              </template>
            </el-table-column>
          </el-table>
        </template>
        <el-empty
          v-else
          description="Select a tower type"
        />
      </el-card>
    </el-col>

    <el-col :span="8">
      <el-card shadow="never">
        <div
          slot="header"
          class="card-header"
        >
          <span>Legs</span>
          <div class="toolbar-actions">
            <el-button
              size="mini"
              :disabled="!activeAssignment"
              @click="autoFillLegs"
            >
              Auto Fill
            </el-button>
            <el-button
              type="primary"
              size="mini"
              :disabled="!activeAssignment"
              @click="saveLegs"
            >
              Save Legs
            </el-button>
          </div>
        </div>
        <template v-if="activeAssignment">
          <el-table
            :data="legs"
            border
            size="mini"
          >
            <el-table-column
              prop="leg"
              label="Leg"
              width="60"
            />
            <el-table-column
              label="Reduce"
              min-width="100"
            >
              <template slot-scope="{ row }">
                <el-input
                  v-model="row.reduce"
                  size="mini"
                />
              </template>
            </el-table-column>
            <el-table-column
              label="Length"
              width="90"
            >
              <template slot-scope="{ row }">
                <el-input-number
                  v-model="row.length"
                  :precision="2"
                  :step="0.1"
                  size="mini"
                />
              </template>
            </el-table-column>
            <el-table-column
              label="Exposed"
              width="90"
            >
              <template slot-scope="{ row }">
                <el-input-number
                  v-model="row.exposed"
                  :precision="2"
                  :step="0.1"
                  size="mini"
                />
              </template>
            </el-table-column>
          </el-table>
        </template>
        <el-empty
          v-else
          description="Select an assignment"
        />
      </el-card>
      <el-card
        class="rule-card"
        shadow="never"
      >
        <div
          slot="header"
          class="card-header"
        >
          <span>Leg Rules</span>
          <el-button
            type="primary"
            size="mini"
            :disabled="!projectContext"
            @click="saveLegRule"
          >
            Save Rule
          </el-button>
        </div>
        <template v-if="projectContext">
          <el-form
            :model="legRule"
            label-width="120px"
            size="mini"
          >
            <el-form-item label="Extract Height">
              <el-input-number
                v-model="legRule.extractHeight"
                :precision="2"
                :step="0.1"
              />
            </el-form-item>
            <el-form-item label="Height Allow">
              <el-input-number
                v-model="legRule.heightAllow"
                :precision="2"
                :step="0.1"
              />
            </el-form-item>
            <el-form-item label="Height Step">
              <el-input-number
                v-model="legRule.heightStep"
                :precision="2"
                :step="0.1"
              />
            </el-form-item>
            <el-form-item label="Min LZKT">
              <el-input-number
                v-model="legRule.minLzkt"
                :precision="2"
                :step="0.1"
              />
            </el-form-item>
            <el-form-item label="Max LZKT">
              <el-input-number
                v-model="legRule.maxLzkt"
                :precision="2"
                :step="0.1"
              />
            </el-form-item>
            <el-form-item label="Dado Mode">
              <el-input-number
                v-model="legRule.dado"
                :min="0"
                :step="1"
              />
            </el-form-item>
          </el-form>
        </template>
        <el-empty
          v-else
          description="Select a project first"
        />
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import {
  applyDefaultTowerBody,
  autoFillTowerLegs,
  getDefaultTowerBodySuggestion,
  getLegRuleConfig,
  listTowerAssignments,
  listTowerLegs,
  listTowerLibrary,
  replaceTowerLegs,
  updateLegRuleConfig,
  updateTowerAssignment
} from '@/api/foundation/tower'

export default {
  name: 'FoundationTowerPage',
  data() {
    return {
      loading: false,
      towerTypes: [],
      assignments: [],
      legs: [],
      activeTowerType: null,
      activeAssignment: null,
      defaultSuggestion: null,
      legRule: {
        extractHeight: 6,
        heightAllow: 0.3,
        heightStep: 0.5,
        minLzkt: 0.5,
        maxLzkt: 3,
        dado: 0
      }
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
          this.loadTowerTypes()
          this.loadLegRule()
        } else {
          this.towerTypes = []
          this.assignments = []
          this.legs = []
          this.activeTowerType = null
          this.activeAssignment = null
          this.defaultSuggestion = null
          this.resetLegRule()
        }
      }
    }
  },
  methods: {
    resetLegRule() {
      this.legRule = {
        extractHeight: 6,
        heightAllow: 0.3,
        heightStep: 0.5,
        minLzkt: 0.5,
        maxLzkt: 3,
        dado: 0
      }
    },
    async loadTowerTypes() {
      this.loading = true
      try {
        const response = await listTowerLibrary(this.projectContext.projectId)
        this.towerTypes = response.data.data || []
        if (this.towerTypes.length > 0) {
          await this.loadAssignments(this.towerTypes[0])
        }
      } finally {
        this.loading = false
      }
    },
    async loadLegRule() {
      if (!this.projectContext) return
      const response = await getLegRuleConfig(this.projectContext.projectId)
      this.legRule = response.data.data || this.legRule
    },
    async loadAssignments(towerTypeRow) {
      this.activeTowerType = towerTypeRow
      const [assignmentResponse, suggestionResponse] = await Promise.all([
        listTowerAssignments(this.projectContext.projectId, towerTypeRow.towerType),
        getDefaultTowerBodySuggestion(this.projectContext.projectId, towerTypeRow.towerType)
      ])
      this.assignments = assignmentResponse.data.data || []
      this.defaultSuggestion = suggestionResponse.data.data
      if (this.assignments.length > 0) {
        await this.loadLegs(this.assignments[0])
      } else {
        this.activeAssignment = null
        this.legs = []
      }
    },
    async loadLegs(assignmentRow) {
      this.activeAssignment = assignmentRow
      const response = await listTowerLegs(this.projectContext.projectId, assignmentRow.nodeId)
      this.legs = response.data.data || []
    },
    async handleTowerTypeChange(row) {
      if (!row) return
      await this.loadAssignments(row)
    },
    async handleAssignmentChange(row) {
      if (!row) return
      await this.loadLegs(row)
    },
    async applyDefaultBody() {
      if (!this.activeTowerType) return
      const response = await applyDefaultTowerBody(this.projectContext.projectId, this.activeTowerType.towerType)
      this.assignments = response.data.data || []
      this.$message.success('Default tower body applied')
      if (this.assignments.length > 0) {
        await this.loadLegs(this.assignments[0])
      }
    },
    async saveAssignment() {
      if (!this.activeAssignment) return
      await updateTowerAssignment(this.projectContext.projectId, this.activeAssignment.nodeId, {
        towerBodyId: this.activeAssignment.towerBodyId,
        positionAdjust: this.activeAssignment.positionAdjust
      })
      this.$message.success('Tower assignment saved')
      await this.loadAssignments(this.activeTowerType)
    },
    async saveLegs() {
      if (!this.activeAssignment) return
      await replaceTowerLegs(this.projectContext.projectId, this.activeAssignment.nodeId, this.legs)
      this.$message.success('Tower legs saved')
      await this.loadLegs(this.activeAssignment)
    },
    async autoFillLegs() {
      if (!this.activeAssignment) return
      const response = await autoFillTowerLegs(this.projectContext.projectId, this.activeAssignment.nodeId)
      this.legs = response.data.data || []
      this.$message.success('Tower legs auto-filled from project rule')
    },
    async saveLegRule() {
      if (!this.projectContext) return
      const response = await updateLegRuleConfig(this.projectContext.projectId, this.legRule)
      this.legRule = response.data.data || this.legRule
      this.$message.success('Leg rule saved')
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

.rule-card {
  margin-top: 16px;
}
</style>
