@DESCRIPTION
- Scaffolder le frontend VueJS 3 avec Composition API
- Mettre en place l'arborescence du projet
- Configurer le routing avec Vue Router
- Définir les layouts (PublicLayout, AdminLayout)
- Créer les squelettes des pages (sans contenu métier)
- Mettre en place les guards de routing (simulés)
- Ne pas implémenter les composants des pages

@CONTEXT
Le backend est suffisamment avancé avec les Use Cases Spring Boot.
Il est maintenant temps de commencer le frontend pour consommer les APIs REST.

Le choix technique est VueJS 3 avec Composition API (script setup).

L'objectif de ce ticket est de geler l'arborescence et le routing
avant d'écrire le moindre composant de page — équivalent frontend
de définir l'API REST avant d'implémenter les contrôleurs.

-------------------------------------------------------------------------------

Architecture

Single Page Application (SPA) unique.

Pas deux applications séparées.

Une seule application Vue avec deux espaces fonctionnels :

/
├── public (/login, /register, /activate-account, /forgot-password)
└── admin (/dashboard, /borrowers, /documents, /items, /loans, /history, /settings)

Le Router décide si l'utilisateur est authentifié.

Les URLs publiques n'ont PAS de préfixe /public.
Le "public" est une propriété des routes (layout + guards), pas un segment URL.

-------------------------------------------------------------------------------

Arborescence du projet

src/
├── api/
│   ├── borrowerApi.js
│   ├── documentApi.js
│   ├── itemApi.js
│   └── loanApi.js
│
├── router/
│   └── index.js
│
├── layouts/
│   ├── PublicLayout.vue
│   └── AdminLayout.vue
│
├── pages/
│   ├── public/
│   │   ├── LoginPage.vue
│   │   ├── RegisterPage.vue
│   │   ├── ActivateAccountPage.vue
│   │   └── ForgotPasswordPage.vue
│   │
│   └── admin/
│       ├── DashboardPage.vue
│       ├── borrowers/
│       │   ├── BorrowerListPage.vue
│       │   ├── BorrowerCreatePage.vue
│       │   └── BorrowerEditPage.vue
│       ├── documents/
│       │   ├── DocumentListPage.vue
│       │   └── DocumentCreatePage.vue
│       ├── items/
│       │   └── ItemListPage.vue
│       ├── loans/
│       │   ├── LoanListPage.vue
│       │   ├── LoanHistoryPage.vue
│       │   └── LoanCreatePage.vue
│       └── settings/
│           └── SettingsPage.vue
│
├── components/
│   ├── borrowers/
│   ├── documents/
│   ├── items/
│   └── loans/
│
├── composables/
│   ├── useBorrowers.js
│   ├── useDocuments.js
│   ├── useItems.js
│   └── useLoans.js
│
├── stores/
│
└── assets/

-------------------------------------------------------------------------------

Routes

Public routes (no auth required):

Route           | Page                    | Layout
----------------|-------------------------|---------------
/               | Redirect to /login      | PublicLayout
/login          | LoginPage               | PublicLayout
/register       | RegisterPage            | PublicLayout
/activate-account | ActivateAccountPage   | PublicLayout
/forgot-password | ForgotPasswordPage     | PublicLayout

Admin routes (auth required):

Route           | Page                    | Layout
----------------|-------------------------|---------------
/admin          | Redirect to /admin/dashboard | AdminLayout
/admin/dashboard | DashboardPage           | AdminLayout
/admin/borrowers | BorrowerListPage        | AdminLayout
/admin/borrowers/create | BorrowerCreatePage | AdminLayout
/admin/borrowers/:id | BorrowerEditPage    | AdminLayout
/admin/documents | DocumentListPage        | AdminLayout
/admin/documents/create | DocumentCreatePage | AdminLayout
/admin/items    | ItemListPage             | AdminLayout
/admin/loans    | LoanListPage             | AdminLayout
/admin/loans/history | LoanHistoryPage     | AdminLayout
/admin/loans/create | LoanCreatePage     | AdminLayout
/admin/settings | SettingsPage            | AdminLayout

-------------------------------------------------------------------------------

Routing configuration

src/router/index.js

- Importer createRouter et createWebHistory de vue-router
- Créer le router avec les routes public et admin
- Les routes admin utilisent une meta: { requiresAuth: true }
- Implémenter un guard global avant les appels REST :
  - Pour les routes admin, vérifier une variable simulée :
    const isAuthenticated = ref(false);
  - Si non authentifié → redirection vers /login
  - Le vrai auth CAS/JWT sera branché dans un ticket ultérieur

-------------------------------------------------------------------------------

Layouts

PublicLayout.vue

Structure :
+--------------------------+
|          Logo            |
|--------------------------|
|                          |
|      <RouterView />      |
|                          |
+--------------------------+

- Logo de l'application
- <RouterView /> pour afficher les pages publiques

AdminLayout.vue

Structure :
+---------------------------------------------+
| Logo       Menu                             |
|---------------------------------------------+
|                                             |
|              <RouterView />                 |
|                                             |
+---------------------------------------------+

- Logo de l'application
- Menu de navigation (liens vers les pages admin)
- <RouterView /> pour afficher les pages admin

-------------------------------------------------------------------------------

Pages squelettes

Chaque page est un composant Vue en mode Composition API (script setup).

Aucune page n'a de contenu métier.

Chaque page contient uniquement :

<template>
  <h1>[Nom de la page]</h1>
</template>

<script setup>
// Squelette — contenu à implémenter ultérieurement
</script>

Exemple :

src/pages/admin/borrowers/BorrowerListPage.vue

<template>
  <h1>Borrowers</h1>
</template>

<script setup>
// TODO: implémenter la liste des borrowers
</script>

-------------------------------------------------------------------------------

Deliverables

- [x] Projet VueJS 3 initialisé avec Vite
- [x] Configuration Vue Router avec toutes les routes listées
- [x] PublicLayout.vue avec logo et RouterView
- [x] AdminLayout.vue avec logo, menu et RouterView
- [x] Pages squelettes (template + script setup vide) pour toutes les routes
- [x] Guards de routing simulés (isAuthenticated = false/true)
- [x] Arborescence de dossiers : api/, router/, layouts/, pages/, components/, composables/, stores/, assets/
- [x] Fichiers api/ squelettes (borrowerApi.js, documentApi.js, itemApi.js, loanApi.js)
- [x] Fichiers composables/ squelettes (useBorrowers.js, useDocuments.js, useItems.js, useLoans.js)
- [x] Fichiers stores/ squelettes
- [x] Redirect / → /login
- [x] Redirect /admin → /admin/dashboard

-------------------------------------------------------------------------------

Out of scope

Les éléments suivants NE SONT PAS dans ce ticket :

- Implémentation du contenu des pages
- Connexion au backend (appels REST)
- Authentification réelle (CAS / JWT)
- Design / styling des layouts
- Composants UI (tables, formulaires, etc.)
- Gestion d'état (Pinia stores)
- Tests frontend
- Internationalisation

-------------------------------------------------------------------------------

Technical constraints

- VueJS 3 avec Composition API (script setup)
- Vue Router 4 avec createWebHistory
- Vite comme tooling de build
- ESLint + Prettier configurés
- Pas de framework CSS (ou optionnel : Tailwind CSS si convenu)
- JavaScript (pas TypeScript dans ce ticket)

@EXECUTION
step1: initialiser projet VueJS 3 avec Vite
step2: installer Vue Router
step3: créer l'arborescence de dossiers (api/, router/, layouts/, pages/, components/, composables/, stores/, assets/)
step4: configurer router/index.js avec toutes les routes
step5: créer PublicLayout.vue avec logo et RouterView
step6: créer AdminLayout.vue avec logo, menu et RouterView
step7: créer les squelettes de toutes les pages publiques et admin
step8: implémenter les guards de routing simulés (isAuthenticated)
step9: configurer les redirects (/ → /login, /admin → /admin/dashboard)
step10: créer les fichiers squelettes pour api/, composables/, stores/
step11: vérifier que l'application démarre et que le routing fonctionne
max_iterations: 10
