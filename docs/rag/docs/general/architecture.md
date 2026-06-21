# Architecture générale

## Objectif

L'application est conçue comme un monolithe modulaire.
La priorité est la simplicité opérationnelle :
- un seul artefact déployable
- un seul runtime
- un pipeline CI/CD simple
- une maintenance facilitée

La séparation fonctionnelle est réalisée par modules métier et non par multiplication des applications.

## Principes

- Le métier ne dépend pas de la technique.
- Les dépendances vont vers les abstractions.
- Les détails d'infrastructure sont remplaçables.
- Chaque module doit rester autonome.

## Couches

### Controller

Responsabilités :
- recevoir les requêtes HTTP
- valider les entrées simples
- appeler les services métier
- retourner les DTO

Interdits :
- logique métier
- accès base de données
- transformation complexe


### Service

Responsabilités :
- contient les règles métier
- orchestre les appels aux repositories
- transforme Entity vers DTO
- applique les règles de sécurité métier

Le service représente le coeur applicatif.


### Repository

Responsabilités :
- abstraction de persistance
- récupération et sauvegarde des données

Le métier ne doit pas connaître :
- SQL
- JPA
- fichiers
- API externes


### Infrastructure

Contient les détails techniques :
- JPA
- fichiers
- clients HTTP
- configuration Spring


## Dépendances

Autorisé :

Controller
    ↓
Service
    ↓
Repository
    ↓
Infrastructure


Interdit :

Repository → Service

Entity → Controller

Controller → Database