Projet universitaire de L3 – Application de recherche d’images par similarité (Java/Spring, Vue.js/TypeScript, PostgreSQL/pgvector) 
  ▪ Conception complète d’un système client-serveur de recherche d’images par descripteurs.
  ▪ Implémentation du serveur en Java avec Spring Boot, indexation des images dans PostgreSQL via
  pgvector, gestion CRUD avec API REST, calcul d’histogramme.
  ▪ Développement du client en TypeScript/Vue.js pour la visualisation, l’ajout, la suppression et la
  récupération d’images.
  ▪ Travail collaboratif avec intégration continue sur GitLab, tests, documentation et validation
  multiplateforme.

# client-serveur
Pour lancer le serveur : 

export DATABASE_NAME="nom_base_de_donnée"
export DATABASE_PASSWORD="votre_mot_de_passe"

mvn clean install
mvn clean compile ; mvn --projects backend spring-boot:run

Notre projet a était testé sur une machine Linux sous Ubuntu avec le moteur de recherche Firefox 136.0 (64-bit).
