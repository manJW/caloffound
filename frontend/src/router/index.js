import Vue from 'vue'
import Router from 'vue-router'
import Layout from '@/layout'

Vue.use(Router)

const routes = [
  {
    path: '/',
    component: Layout,
    redirect: '/foundation/overview',
    children: [
      {
        path: '/foundation/overview',
        component: () => import('@/views/foundation/overview/index'),
        name: 'FoundationOverview',
        meta: { title: '总览', icon: 'data-analysis' }
      },
      {
        path: '/foundation/project',
        component: () => import('@/views/foundation/project/index'),
        name: 'FoundationProject',
        meta: { title: '项目节点', icon: 'list' }
      },
      {
        path: '/foundation/data-prep',
        component: () => import('@/views/foundation/data-prep/index'),
        name: 'FoundationDataPrep',
        meta: { title: '数据准备', icon: 'upload' }
      },
      {
        path: '/foundation/tower',
        component: () => import('@/views/foundation/tower/index'),
        name: 'FoundationTower',
        meta: { title: '塔型腿配', icon: 'operation' }
      },
      {
        path: '/foundation/calc',
        component: () => import('@/views/foundation/calc/index'),
        name: 'FoundationCalc',
        meta: { title: '基础计算', icon: 'edit-outline' }
      },
      {
        path: '/foundation/scene',
        component: () => import('@/views/foundation/scene/index'),
        name: 'FoundationScene',
        meta: { title: '三维场景', icon: 'picture-outline' }
      }
    ]
  }
]

export default new Router({
  mode: 'hash',
  routes
})
