<template>
  <div class="admin-layout">
    <header class="admin-header">
      <div class="logo">
        <h1>Book Management</h1>
      </div>
      <nav class="admin-menu">
        <router-link to="/admin/dashboard">Dashboard</router-link>
        <router-link to="/admin/borrowers">Borrowers</router-link>
        <router-link to="/admin/documents">Documents</router-link>
        <router-link to="/admin/items">Items</router-link>
        <router-link to="/admin/loans">Loans</router-link>
        <router-link to="/admin/settings">Settings</router-link>
        <a href="#" @click.prevent="handleLogout" class="logout-link">Logout</a>
      </nav>
    </header>
    <main class="admin-content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

async function handleLogout() {
  await authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  border-bottom: 1px solid var(--border);
  background: var(--bg);
}

.logo h1 {
  font-size: 20px;
  margin: 0;
}

.admin-menu {
  display: flex;
  gap: 16px;
  align-items: center;
}

.admin-menu a {
  color: var(--text);
  text-decoration: none;
  font-size: 14px;
  padding: 6px 10px;
  border-radius: 4px;
  transition: background 0.2s;
}

.admin-menu a:hover {
  background: var(--accent-bg);
  color: var(--accent);
}

.logout-link {
  color: #ef4444 !important;
  font-weight: 500;
}

.logout-link:hover {
  background: rgba(239, 68, 68, 0.1) !important;
  color: #ef4444 !important;
}

.admin-content {
  flex: 1;
  padding: 24px;
}
</style>
