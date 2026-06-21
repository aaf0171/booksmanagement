# Principes métier

Le code métier doit être compréhensible sans connaître Spring.

Les règles métier :
- doivent être dans les services
- doivent être testables facilement
- doivent éviter les dépendances techniques


Préférer :

une méthode métier claire

plutôt que :

une méthode qui mélange :
- SQL
- HTTP
- transformation JSON
- règles métier


Les commentaires doivent expliquer l'intention métier,
pas répéter le code.