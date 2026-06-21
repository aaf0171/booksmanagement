# Architecture (Monolithe modulaire)

Objectif :
- 1 application Spring Boot
- 1 artefact
- simplicité OPS

Couches :
Controller -> Service -> Repository -> Infrastructure

Règles :
- Controller sans logique métier
- Service = coeur métier
- Repository = abstraction persistence
