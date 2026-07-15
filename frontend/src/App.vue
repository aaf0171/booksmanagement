<script setup>
import { useRoute } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()

const layoutComponent = computed(() => {
  return route.matched?.[0]?.components?.default || null
})
</script>

<template>
  <component :is="layoutComponent">
    <router-view />
  </component>
</template>
