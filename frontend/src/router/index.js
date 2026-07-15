import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

import PublicLayout from '@/layouts/PublicLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

import LoginPage from '@/pages/public/LoginPage.vue'
import RegisterPage from '@/pages/public/RegisterPage.vue'
import ActivateAccountPage from '@/pages/public/ActivateAccountPage.vue'
import ForgotPasswordPage from '@/pages/public/ForgotPasswordPage.vue'

import DashboardPage from '@/pages/admin/DashboardPage.vue'
import BorrowerListPage from '@/pages/admin/borrowers/BorrowerListPage.vue'
import BorrowerCreatePage from '@/pages/admin/borrowers/BorrowerCreatePage.vue'
import BorrowerEditPage from '@/pages/admin/borrowers/BorrowerEditPage.vue'
import DocumentListPage from '@/pages/admin/documents/DocumentListPage.vue'
import DocumentCreatePage from '@/pages/admin/documents/DocumentCreatePage.vue'
import ItemListPage from '@/pages/admin/items/ItemListPage.vue'
import LoanListPage from '@/pages/admin/loans/LoanListPage.vue'
import LoanHistoryPage from '@/pages/admin/loans/LoanHistoryPage.vue'
import LoanCreatePage from '@/pages/admin/loans/LoanCreatePage.vue'
import SettingsPage from '@/pages/admin/settings/SettingsPage.vue'

const routes = [
  {
    path: '/',
    layout: PublicLayout,
    redirect: '/login'
  },
  {
    path: '/login',
    layout: PublicLayout,
    component: LoginPage
  },
  {
    path: '/register',
    layout: PublicLayout,
    component: RegisterPage
  },
  {
    path: '/activate-account',
    layout: PublicLayout,
    component: ActivateAccountPage
  },
  {
    path: '/forgot-password',
    layout: PublicLayout,
    component: ForgotPasswordPage
  },
  {
    path: '/admin',
    layout: AdminLayout,
    children: [
      {
        path: '',
        redirect: '/admin/dashboard'
      },
      {
        path: 'dashboard',
        component: DashboardPage,
        meta: { requiresAuth: true }
      },
      {
        path: 'borrowers',
        component: BorrowerListPage,
        meta: { requiresAuth: true }
      },
      {
        path: 'borrowers/create',
        component: BorrowerCreatePage,
        meta: { requiresAuth: true }
      },
      {
        path: 'borrowers/:id',
        component: BorrowerEditPage,
        meta: { requiresAuth: true }
      },
      {
        path: 'documents',
        component: DocumentListPage,
        meta: { requiresAuth: true }
      },
      {
        path: 'documents/create',
        component: DocumentCreatePage,
        meta: { requiresAuth: true }
      },
      {
        path: 'items',
        component: ItemListPage,
        meta: { requiresAuth: true }
      },
      {
        path: 'loans',
        component: LoanListPage,
        meta: { requiresAuth: true }
      },
      {
        path: 'loans/history',
        component: LoanHistoryPage,
        meta: { requiresAuth: true }
      },
      {
        path: 'loans/create',
        component: LoanCreatePage,
        meta: { requiresAuth: true }
      },
      {
        path: 'settings',
        component: SettingsPage,
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from) => {
  const authStore = useAuthStore()
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)

  if (requiresAuth && !authStore.isAuthenticated) {
    return '/login'
  }

  if (to.path === '/login' && authStore.isAuthenticated) {
    return '/admin/dashboard'
  }
})

export default router
