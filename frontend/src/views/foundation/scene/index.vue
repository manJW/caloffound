<template>
  <el-row :gutter="16">
    <el-col :span="8">
      <el-card shadow="never">
        <div
          slot="header"
          class="card-header"
        >
          <span>3D Scene Nodes</span>
          <span class="subtle">{{ projectContext ? projectContext.projectName : 'Select a project first' }}</span>
        </div>
        <template v-if="projectContext">
          <el-table
            v-loading="loading"
            :data="sceneNodes"
            border
            highlight-current-row
            @current-change="handleNodeChange"
          >
            <el-table-column
              prop="towerNo"
              label="Tower No."
              width="90"
            />
            <el-table-column
              prop="stake"
              label="Stake"
              width="100"
            />
            <el-table-column
              prop="terrainSource"
              label="Terrain"
              min-width="120"
            />
            <el-table-column
              prop="terrainPointCount"
              label="Points"
              width="70"
            />
            <el-table-column
              prop="legCount"
              label="Legs"
              width="70"
            />
            <el-table-column
              label="Launch"
              width="90"
            >
              <template slot-scope="{ row }">
                <el-tag
                  size="mini"
                  :type="row.launchable ? 'success' : 'warning'"
                >
                  {{ row.launchable ? 'Ready' : 'Blocked' }}
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
    <el-col :span="16">
      <el-card shadow="never">
        <div
          slot="header"
          class="card-header"
        >
          <span>Three.js Scene</span>
          <div class="toolbar-actions">
            <el-switch
              v-model="showTerrain"
              active-text="Terrain"
            />
            <el-switch
              v-model="showTower"
              active-text="Tower"
            />
            <el-switch
              v-model="showLegs"
              active-text="Legs"
            />
            <el-button
              size="mini"
              :disabled="!scenePayload"
              @click="resetCamera"
            >
              Reset Camera
            </el-button>
          </div>
        </div>

        <div
          ref="sceneCanvas"
          class="scene-canvas"
        />

        <el-descriptions
          v-if="scenePayload"
          :column="2"
          border
          size="small"
          class="scene-info"
        >
          <el-descriptions-item label="Tower / Stake">
            {{ scenePayload.towerNo }} / {{ scenePayload.stake }}
          </el-descriptions-item>
          <el-descriptions-item label="Survey Type">
            {{ scenePayload.surveyType }}
          </el-descriptions-item>
          <el-descriptions-item label="Terrain Source">
            {{ scenePayload.terrainSource }}
          </el-descriptions-item>
          <el-descriptions-item label="Scene Status">
            {{ scenePayload.sceneStatus }}
          </el-descriptions-item>
          <el-descriptions-item label="Recommended View">
            {{ scenePayload.recommendedView }}
          </el-descriptions-item>
          <el-descriptions-item label="Layer Count">
            {{ sceneLayers.length }}
          </el-descriptions-item>
          <el-descriptions-item label="Terrain Points">
            {{ scenePayload.terrainPointCount }}
          </el-descriptions-item>
          <el-descriptions-item label="Leg Count">
            {{ scenePayload.legCount }}
          </el-descriptions-item>
        </el-descriptions>

        <el-alert
          v-if="scenePayload"
          :title="scenePayload.launchable ? 'Scene is ready for browser rendering.' : 'Scene is blocked. Resolve blockers below.'"
          :type="scenePayload.launchable ? 'success' : 'warning'"
          :closable="false"
          show-icon
          class="scene-banner"
        />

        <el-alert
          v-for="blocker in blockers"
          :key="blocker"
          :title="blocker"
          type="warning"
          :closable="false"
          show-icon
          class="scene-blocker"
        />

        <el-table
          v-if="sceneLayers.length"
          :data="sceneLayers"
          size="mini"
          border
          class="scene-layers"
        >
          <el-table-column
            prop="title"
            label="Layer"
            min-width="160"
          />
          <el-table-column
            prop="type"
            label="Type"
            width="110"
          />
          <el-table-column
            prop="pointCount"
            label="Points"
            width="90"
          />
          <el-table-column
            label="Color"
            width="90"
          >
            <template slot-scope="{ row }">
              <span
                class="layer-color"
                :style="{ backgroundColor: row.color }"
              />
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-col>
  </el-row>
</template>

<script>
import * as THREE from 'three'
import { listSceneNodes, loadNodeScene } from '@/api/foundation/scene'

export default {
  name: 'FoundationScenePage',
  data() {
    return {
      loading: false,
      sceneNodes: [],
      scenePayload: null,
      renderer: null,
      camera: null,
      scene: null,
      showTerrain: true,
      showTower: true,
      showLegs: true,
      dynamicObjects: []
    }
  },
  computed: {
    projectContext() {
      return this.$store.state.foundation.projectContext
    },
    blockers() {
      return this.scenePayload ? this.scenePayload.blockers || [] : []
    },
    sceneLayers() {
      if (!this.scenePayload || !this.scenePayload.layers) {
        return []
      }
      return this.scenePayload.layers.map(layer => ({
        ...layer,
        pointCount: (layer.points || []).length
      }))
    }
  },
  watch: {
    projectContext: {
      immediate: true,
      handler(value) {
        if (value && value.projectId) {
          this.restoreLayerVisibility()
          this.loadSceneNodes()
        } else {
          this.sceneNodes = []
          this.scenePayload = null
          this.disposeRenderer()
        }
      }
    },
    showTerrain() {
      this.persistLayerVisibility()
      this.renderScene()
    },
    showTower() {
      this.persistLayerVisibility()
      this.renderScene()
    },
    showLegs() {
      this.persistLayerVisibility()
      this.renderScene()
    }
  },
  mounted() {
    this.restoreLayerVisibility()
    this.initRenderer()
  },
  beforeDestroy() {
    this.disposeRenderer()
  },
  methods: {
    async loadSceneNodes() {
      this.loading = true
      try {
        const response = await listSceneNodes(this.projectContext.projectId)
        this.sceneNodes = response.data.data || []
        if (this.sceneNodes.length > 0) {
          await this.showScene(this.sceneNodes[0])
        }
      } finally {
        this.loading = false
      }
    },
    async handleNodeChange(row) {
      if (!row) return
      await this.showScene(row)
    },
    async showScene(row) {
      const response = await loadNodeScene(this.projectContext.projectId, row.nodeId)
      this.scenePayload = response.data.data
      this.renderScene()
    },
    initRenderer() {
      const container = this.$refs.sceneCanvas
      if (!container) return
      const width = container.clientWidth || 720
      const height = 420
      this.scene = new THREE.Scene()
      this.scene.background = new THREE.Color('#f5f7fa')
      this.camera = new THREE.PerspectiveCamera(60, width / height, 0.1, 1000)
      this.resetCamera()
      this.renderer = new THREE.WebGLRenderer({ antialias: true })
      this.renderer.setSize(width, height)
      container.innerHTML = ''
      container.appendChild(this.renderer.domElement)
      const light = new THREE.DirectionalLight(0xffffff, 1.0)
      light.position.set(20, 20, 30)
      this.scene.add(light)
      this.scene.add(new THREE.AmbientLight(0xffffff, 0.65))
      this.renderer.render(this.scene, this.camera)
    },
    layerStorageKey() {
      const projectId = this.projectContext ? this.projectContext.projectId : 'default'
      return `foundation-scene-layers:${projectId}`
    },
    restoreLayerVisibility() {
      try {
        const raw = window.localStorage.getItem(this.layerStorageKey())
        if (!raw) return
        const parsed = JSON.parse(raw)
        this.showTerrain = parsed.showTerrain !== false
        this.showTower = parsed.showTower !== false
        this.showLegs = parsed.showLegs !== false
      } catch (error) {
        // Ignore invalid local storage content.
      }
    },
    persistLayerVisibility() {
      try {
        window.localStorage.setItem(this.layerStorageKey(), JSON.stringify({
          showTerrain: this.showTerrain,
          showTower: this.showTower,
          showLegs: this.showLegs
        }))
      } catch (error) {
        // Ignore storage failures.
      }
    },
    clearScene() {
      this.dynamicObjects.forEach(obj => this.scene.remove(obj))
      this.dynamicObjects = []
    },
    remember(obj) {
      this.dynamicObjects.push(obj)
      this.scene.add(obj)
    },
    renderScene() {
      if (!this.scene || !this.renderer || !this.camera || !this.scenePayload) {
        return
      }
      this.clearScene()

      const grid = new THREE.GridHelper(30, 10, 0x999999, 0xdddddd)
      this.remember(grid)

      const terrainLayer = this.findLayer('terrain')
      const towerLayer = this.findLayer('tower')
      const legLayer = this.findLayer('legs')
      const helperLayer = this.findLayer('helpers')

      if (this.showTerrain) {
        const terrainMaterial = new THREE.MeshStandardMaterial({
          color: 0xb9d6a3,
          roughness: 0.9,
          metalness: 0.0
        })
        for (const point of (terrainLayer ? terrainLayer.points : this.scenePayload.terrainPoints)) {
          const geometry = new THREE.BoxGeometry(0.5, 0.5, 0.5)
          const mesh = new THREE.Mesh(geometry, terrainMaterial)
          mesh.position.set(point.x, point.y, point.z / 10)
          this.remember(mesh)
        }
      }

      if (this.showTower) {
        const towerGeometry = new THREE.CylinderGeometry(0.35, 0.5, 8, 10)
        const towerMaterial = new THREE.MeshStandardMaterial({ color: 0x3a6ea5 })
        const tower = new THREE.Mesh(towerGeometry, towerMaterial)
        const towerPoint = towerLayer && towerLayer.points.length ? towerLayer.points[0] : this.scenePayload.towerPoint
        tower.position.set(towerPoint.x, towerPoint.y, 4)
        this.remember(tower)
      }

      if (this.showLegs) {
        const legMaterial = new THREE.MeshStandardMaterial({ color: 0xa95d2d })
        for (const leg of (legLayer ? legLayer.points : this.scenePayload.legPoints)) {
          const geometry = new THREE.CylinderGeometry(0.15, 0.2, 4, 8)
          const mesh = new THREE.Mesh(geometry, legMaterial)
          mesh.position.set(leg.x, leg.y, 2)
          this.remember(mesh)
        }
      }

      if (helperLayer && helperLayer.points && helperLayer.points.length > 1) {
        const helperPoints = helperLayer.points.map(point => new THREE.Vector3(point.x, point.y, point.z / 10))
        const helperGeometry = new THREE.BufferGeometry().setFromPoints(helperPoints)
        const helperMaterial = new THREE.LineBasicMaterial({ color: 0x9ca3af })
        const helperLine = new THREE.Line(helperGeometry, helperMaterial)
        this.remember(helperLine)
      }

      this.renderer.render(this.scene, this.camera)
    },
    resetCamera() {
      if (!this.camera) return
      const position = this.scenePayload && this.scenePayload.cameraPosition
        ? this.scenePayload.cameraPosition
        : { x: 18, y: 18, z: 18 }
      const target = this.scenePayload && this.scenePayload.cameraTarget
        ? this.scenePayload.cameraTarget
        : { x: 0, y: 0, z: 0 }
      this.camera.position.set(position.x, position.y, position.z)
      this.camera.lookAt(target.x, target.y, target.z)
      if (this.renderer && this.scene) {
        this.renderer.render(this.scene, this.camera)
      }
    },
    findLayer(layerKey) {
      return this.scenePayload && this.scenePayload.layers
        ? this.scenePayload.layers.find(layer => layer.layerKey === layerKey)
        : null
    },
    disposeRenderer() {
      if (this.renderer) {
        this.renderer.dispose()
        this.renderer = null
      }
      this.dynamicObjects = []
      this.scene = null
      this.camera = null
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
  align-items: center;
  gap: 12px;
}

.subtle {
  color: #909399;
  font-size: 12px;
}

.scene-canvas {
  width: 100%;
  height: 420px;
  background: #eef2f6;
  border: 1px solid #dcdfe6;
}

.scene-info,
.scene-banner {
  margin-top: 16px;
}

.scene-blocker {
  margin-top: 12px;
}

.scene-layers {
  margin-top: 16px;
}

.layer-color {
  display: inline-block;
  width: 18px;
  height: 18px;
  border: 1px solid #d1d5db;
  border-radius: 4px;
}
</style>
