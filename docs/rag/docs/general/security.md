# Sécurité

## Authentification

L'application utilise une authentification stateless.

Principe :

Utilisateur
    ↓
Login
    ↓
Access Token JWT
    ↓
API


Les refresh tokens permettent de renouveler les access tokens.


## Règles

Ne jamais :
- stocker un mot de passe en clair
- exposer une Entity directement
- mettre une logique d'autorisation dans un Controller


La sécurité technique est gérée par Spring Security.

Les règles métier restent dans les services.