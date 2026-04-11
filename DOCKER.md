# Démarrage avec Docker

## Prérequis

- Docker Desktop 24.0+
- Docker Compose 2.20+

## Installation

```bash
# Cloner le projet
git clone git@github.com:ASSONDJI/WeatherEvent-Mashup.git
cd WeatherEvent-Mashup

# Configurer les clés API (éditer le fichier .env)
cp .env.example .env
nano .env

# Démarrer tous les services
docker-compose up -d

# Vérifier que tout tourne
docker-compose ps
```

## Services disponibles
* Service	URL	Description
* Backend API	http://localhost:8080	Spring Boot avec Java 17
* Swagger UI	http://localhost:8080/swagger-ui.html	Documentation API
* Frontend	http://localhost:5173	Interface React
* Redis Commander	http://localhost:8081	Interface Redis
* PostgreSQL	localhost:5432	Base de données
## Commandes utiles

* Voir les logs :
**docker-compose logs -f backend**
* Redémarrer un service :
**docker-compose restart backend**
* Arrêter tous les services :
**docker-compose down**
* Arrêter et supprimer les volumes :
**docker-compose down -v**

## Structure
* Java 17 dans le conteneur backend (pas besoin de l'installer localement)
* PostgreSQL 15 pour les données
* Redis 7 pour le cache
* Nginx pour servir le frontend