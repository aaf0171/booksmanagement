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
      </nav>
    </header>
    <main class="admin-content">
      <RouterView />
    </main>
  </div>
</template>

<script setup>
// TODO: implémenter le contenu du layout admin
</script>
