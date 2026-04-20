<template>
  <div class="overview-page">
    <el-row :gutter="16">
      <el-col :span="16">
        <el-card shadow="never">
          <div
            slot="header"
            class="card-header"
          >
            <span>Foundation Overview</span>
            <el-button
              size="mini"
              @click="loadStatus"
            >
              Refresh
            </el-button>
          </div>
          <el-descriptions
            v-if="status"
            :column="2"
            border
          >
            <el-descriptions-item label="Module">
              {{ status.moduleName }}
            </el-descriptions-item>
            <el-descriptions-item label="Version">
              {{ status.version }}
            </el-descriptions-item>
            <el-descriptions-item label="Frontend">
              {{ status.frontendStack }}
            </el-descriptions-item>
            <el-descriptions-item label="Backend">
              {{ status.backendStack }}
            </el-descriptions-item>
            <el-descriptions-item label="3D">
              {{ status.renderStack }}
            </el-descriptions-item>
            <el-descriptions-item label="Stage">
              {{ status.stage }}
            </el-descriptions-item>
            <el-descriptions-item label="Persistence">
              {{ status.persistenceMode }}
            </el-descriptions-item>
          </el-descriptions>
          <el-empty
            v-else
            description="Status is not loaded"
          />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div
            slot="header"
            class="card-header"
          >
            <span>Current Scope</span>
          </div>
          <ul class="scope-list">
            <li>Project and node management</li>
            <li>Data preparation and survey point editing</li>
            <li>Tower assignment and leg editing</li>
            <li>Foundation calculation with preview/export</li>
            <li>Three.js node scene viewer</li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
    <el-row
      :gutter="16"
      class="smoke-row"
    >
      <el-col :span="24">
        <el-card shadow="never">
          <div
            slot="header"
            class="card-header"
          >
            <span>Database Smoke Snapshot</span>
            <el-button
              size="mini"
              @click="loadSmoke"
            >
              Reload Smoke
            </el-button>
          </div>
          <el-alert
            v-if="smoke && smoke.persistenceMode !== 'db'"
            title="Persistence mode is not db. The snapshot may not reflect the legacy MySQL chain."
            type="warning"
            :closable="false"
            show-icon
            class="smoke-alert"
          />
          <el-alert
            v-else-if="smoke && smoke.projectCount === 0"
            title="Smoke endpoint returned zero projects. Check datasource settings and legacy MySQL availability."
            type="warning"
            :closable="false"
            show-icon
            class="smoke-alert"
          />
          <el-row
            v-if="smoke"
            :gutter="12"
          >
            <el-col :span="6">
              <div class="metric-card">
                <span class="metric-label">Persistence</span>
                <strong class="metric-value">{{ smoke.persistenceMode }}</strong>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="metric-card">
                <span class="metric-label">Projects</span>
                <strong class="metric-value">{{ smoke.projectCount }}</strong>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="metric-card">
                <span class="metric-label">Nodes</span>
                <strong class="metric-value">{{ smoke.nodeCount }}</strong>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="metric-card">
                <span class="metric-label">Ready Scenes</span>
                <strong class="metric-value">{{ smoke.readySceneCount }}</strong>
              </div>
            </el-col>
          </el-row>
          <el-descriptions
            v-if="smoke"
            :column="2"
            border
            class="smoke-summary"
          >
            <el-descriptions-item label="Blocked Scenes">
              {{ smoke.blockedSceneCount }}
            </el-descriptions-item>
            <el-descriptions-item label="Regression Guide">
              See /docs/testing/REGRESSION_PLAN.md
            </el-descriptions-item>
          </el-descriptions>
          <ul
            v-if="smoke"
            class="scope-list smoke-notes"
          >
            <li
              v-for="note in smoke.notes"
              :key="note"
            >
              {{ note }}
            </li>
          </ul>
          <el-empty
            v-else
            description="Smoke snapshot is not loaded"
          />
        </el-card>
      </el-col>
    </el-row>
    <el-card
      class="module-card"
      shadow="never"
    >
      <div
        slot="header"
        class="card-header"
      >
        <span>Module Status</span>
      </div>
      <el-table
        v-if="status"
        :data="status.modules"
        size="small"
        border
      >
        <el-table-column
          prop="module"
          label="Module"
          min-width="120"
        />
        <el-table-column
          prop="status"
          label="Status"
          width="120"
        />
        <el-table-column
          prop="nextMilestone"
          label="Next Milestone"
          min-width="280"
        />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { fetchFoundationSmoke, fetchFoundationStatus } from '@/api/foundation/status'

export default {
  name: 'FoundationOverviewPage',
  data() {
    return {
      status: null,
      smoke: null
    }
  },
  created() {
    this.loadStatus()
    this.loadSmoke()
  },
  methods: {
    async loadStatus() {
      const response = await fetchFoundationStatus()
      this.status = response.data.data
    },
    async loadSmoke() {
      const response = await fetchFoundationSmoke()
      this.smoke = response.data.data
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

.scope-list {
  margin: 0;
  padding-left: 18px;
  line-height: 1.8;
}

.smoke-row,
.module-card {
  margin-top: 16px;
}

.smoke-alert,
.smoke-summary,
.smoke-notes {
  margin-top: 16px;
}

.metric-card {
  padding: 14px 16px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background: #f8fbff;
}

.metric-label {
  display: block;
  color: #909399;
  font-size: 12px;
  margin-bottom: 8px;
}

.metric-value {
  font-size: 24px;
  color: #303133;
}
</style>
