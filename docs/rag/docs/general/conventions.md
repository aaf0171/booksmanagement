# Conventions Java

## Version

Java 21 minimum.

## Lombok

Utiliser Lombok pour réduire le code répétitif.

Préférer :

@Getter
@Setter
@NoArgsConstructor

Éviter @Data sur les Entity JPA.

Raison :
equals/hashCode/toString peuvent provoquer des problèmes Hibernate.


## Entités

Les Entity JPA représentent la persistance.

Exemple :

Book.java

Une Entity ne doit pas être exposée directement par l'API.


## DTO

Les DTO représentent les contrats API.

Un DTO :
- ne contient pas de logique métier
- ne contient pas d'accès aux données


## Mapping

La transformation Entity <-> DTO est réalisée dans la couche service ou via un mapper dédié.


## Injection

Utiliser l'injection par constructeur.

Préférer :

private final BookRepository repository;

public BookService(BookRepository repository) {
    this.repository = repository;
}


Éviter l'injection par champ :

@Autowired
private BookRepository repository;