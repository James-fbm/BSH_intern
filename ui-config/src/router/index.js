import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter)

const routes = [
  {
    path: '/templateDemo1',
    name: 'templateDemo1',
    component: () => import('../components/templateDemo1'),
  },
  {
    path: '/templateDemo2/:templateId',
    name: 'templateDemo2',
    component: () => import('../components/templateDemo2'),
  },
  
]

const router = new VueRouter({
  mode: "history",
  routes
})

export default router
