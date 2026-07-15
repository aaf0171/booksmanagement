<template>
  <div class="login-page">
    <div class="login-form">
      <h1>Connexion</h1>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username">Identifiant</label>
          <input
            id="username"
            v-model="credentials.username"
            type="text"
            placeholder="Identifiant"
            required
            :disabled="loading"
          />
        </div>
        <div class="form-group">
          <label for="password">Mot de passe</label>
          <input
            id="password"
            v-model="credentials.password"
            type="password"
            placeholder="Mot de passe"
            required
            :disabled="loading"
          />
        </div>
        <div v-if="error" class="error-message">{{ error }}</div>
        <button type="submit" :disabled="loading" class="btn-primary">
          {{ loading ? 'Connexion en cours...' : 'Se connecter' }}
        </button>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const credentials = reactive({
  username: '',
  password: ''
})

const loading = ref(false)
const error = ref('')

async function handleLogin() {
  loading.value = true
  error.value = ''

  try {
    await authStore.login({
      username: credentials.username,
      password: credentials.password
    })

    await authStore.fetchUser()
    router.push('/admin/dashboard')
  } catch (err) {
    if (err.response?.status === 401) {
      error.value = 'Identifiant ou mot de passe incorrect.'
    } else {
      error.value = 'Une erreur est survenue. Veuillez réessayer.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 60vh;
  padding: 20px;
}

.login-form {
  width: 100%;
  max-width: 400px;
}

.form-group {
  margin-bottom: 20px;
  text-align: left;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: var(--text-h);
}

.form-group input {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid var(--border);
  border-radius: 6px;
  font-size: 16px;
  background: var(--bg);
  color: var(--text);
  transition: border-color 0.2s;
  box-sizing: border-box;
}

.form-group input:focus {
  outline: none;
  border-color: var(--accent);
}

.form-group input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.error-message {
  color: #ef4444;
  margin-bottom: 16px;
  padding: 8px 12px;
  background: rgba(239, 68, 68, 0.1);
  border-radius: 6px;
  font-size: 14px;
}

.btn-primary {
  width: 100%;
  padding: 12px 16px;
  background: var(--accent);
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-primary:hover:not(:disabled) {
  opacity: 0.9;
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
