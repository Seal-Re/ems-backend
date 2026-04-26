import Vue from 'vue'
import VueRouter from 'vue-router'
import MainLayout from '../layout/MainLayout.vue'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    component: MainLayout,
    redirect: '/affair',
    children: [
      {
        path: 'affair',
        name: 'Affair',
        component: () => import('../views/AffairView.vue'),
        meta: { title: 'Affair Center', icon: 'el-icon-message' }
      },
      {
        path: 'subscribe',
        name: 'Subscribe',
        component: () => import('../views/SubscribeView.vue'),
        meta: { title: 'Subscribe Center', icon: 'el-icon-bell' }
      }
    ]
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
