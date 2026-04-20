<template>
  <div class="overview-page">
    <el-row :gutter="16">
      <el-col :span="16">
        <el-card shadow="never">
          <div
            slot="header"
            class="card-header"
          >
            <span>基础系统总览</span>
            <el-button
              size="mini"
              @click="loadStatus"
            >
              刷新
            </el-button>
          </div>
          <el-descriptions
            v-if="status"
            :column="2"
            border
          >
            <el-descriptions-item label="模块">
              {{ status.moduleName }}
            </el-descriptions-item>
            <el-descriptions-item label="版本">
              {{ status.version }}
            </el-descriptions-item>
            <el-descriptions-item label="前端">
              {{ status.frontendStack }}
            </el-descriptions-item>
            <el-descriptions-item label="后端">
              {{ status.backendStack }}
            </el-descriptions-item>
            <el-descriptions-item label="三维">
              {{ status.renderStack }}
            </el-descriptions-item>
            <el-descriptions-item label="阶段">
              {{ zh(status.stage) }}
            </el-descriptions-item>
            <el-descriptions-item label="持久化">
              {{ zh(status.persistenceMode) }}
            </el-descriptions-item>
          </el-descriptions>
          <el-empty
            v-else
            description="状态尚未加载"
          />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <div
            slot="header"
            class="card-header"
          >
            <span>当前范围</span>
          </div>
          <ul class="scope-list">
            <li>项目与节点管理</li>
            <li>数据准备与测量点编辑</li>
            <li>塔身分配与四腿编辑</li>
            <li>基础计算、预览与导出</li>
            <li>Three.js 节点级三维场景</li>
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
            <span>数据库烟测快照</span>
            <el-button
              size="mini"
              @click="loadSmoke"
            >
              重新烟测
            </el-button>
          </div>
          <el-alert
            v-if="smoke && smoke.persistenceMode !== 'db'"
            title="当前持久化模式不是数据库模式，快照可能无法反映旧 MySQL 链路。"
            type="warning"
            :closable="false"
            show-icon
            class="smoke-alert"
          />
          <el-alert
            v-else-if="smoke && smoke.projectCount === 0"
            title="烟测接口返回 0 个项目，请检查数据源配置和旧 MySQL 可用性。"
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
                <span class="metric-label">持久化</span>
                <strong class="metric-value">{{ zh(smoke.persistenceMode) }}</strong>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="metric-card">
                <span class="metric-label">项目数</span>
                <strong class="metric-value">{{ smoke.projectCount }}</strong>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="metric-card">
                <span class="metric-label">节点数</span>
                <strong class="metric-value">{{ smoke.nodeCount }}</strong>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="metric-card">
                <span class="metric-label">可用三维场景</span>
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
            <el-descriptions-item label="阻塞场景">
              {{ smoke.blockedSceneCount }}
            </el-descriptions-item>
            <el-descriptions-item label="回归指南">
              查看 /docs/testing/REGRESSION_PLAN.md
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
              {{ zh(note) }}
            </li>
          </ul>
          <el-empty
            v-else
            description="烟测快照尚未加载"
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
        <span>模块状态</span>
      </div>
      <el-table
        v-if="status"
        :data="status.modules"
        size="small"
        border
      >
        <el-table-column
          prop="module"
          label="模块"
          min-width="120"
        >
          <template slot-scope="{ row }">
            {{ zh(row.module) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="status"
          label="状态"
          width="120"
        >
          <template slot-scope="{ row }">
            {{ zh(row.status) }}
          </template>
        </el-table-column>
        <el-table-column
          prop="nextMilestone"
          label="下一步"
          min-width="280"
        >
          <template slot-scope="{ row }">
            {{ zh(row.nextMilestone) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { fetchFoundationSmoke, fetchFoundationStatus } from '@/api/foundation/status'
import { zh } from '@/utils/foundationI18n'

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
    zh,
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
