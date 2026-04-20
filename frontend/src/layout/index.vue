<template>
  <div class="app-shell">
    <aside class="sidebar">
      <h1>caloffound</h1>
      <el-menu
        :default-active="$route.path"
        class="nav-menu"
        router
      >
        <el-menu-item index="/foundation/overview">
          Overview
        </el-menu-item>
        <el-menu-item index="/foundation/project">
          Projects
        </el-menu-item>
        <el-menu-item index="/foundation/data-prep">
          Data Prep
        </el-menu-item>
        <el-menu-item index="/foundation/tower">
          Tower Library
        </el-menu-item>
        <el-menu-item index="/foundation/calc">
          Calculation
        </el-menu-item>
        <el-menu-item index="/foundation/scene">
          3D Scene
        </el-menu-item>
      </el-menu>
    </aside>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.app-shell {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
}

.sidebar {
  width: 240px;
  background: #1f2d3d;
  color: #fff;
  padding: 24px 16px;
  box-sizing: border-box;
}

.sidebar h1 {
  font-size: 20px;
  margin: 0 0 16px;
}

.nav-menu {
  border-right: none;
}

.main-content {
  flex: 1;
  padding: 24px;
  box-sizing: border-box;
}
</style>
