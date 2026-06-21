# Structure du projet

Organisation par domaine métier privilégiée.

Exemple :

com.books

book/
    controller/
    service/
    repository/
    model/
    dto/


auth/
    controller/
    service/
    security/


Chaque module métier doit limiter ses dépendances externes.


Un module ne doit pas accéder directement aux classes internes d'un autre module.