# Système de Gestion de Magasin - Kotlin + Spring Boot + MongoDB

## Que fait cette application ?

- **Système de Gestion Complet** : Gère tous les aspects d'un magasin (produits, stocks, ventes, commandes)
- **E-commerce Intégré** : Permet aux clients de passer des commandes en ligne avec retrait en magasin
- **Point de Vente (POS)** : Interface de caisse pour les ventes directes
- **Gestion Multi-Rôles** : Support pour ADMIN, MANAGER et CLIENT avec permissions granulaires
- **Traçabilité Complète** : Audit trail de toutes les actions pour conformité
- **Gestion des Stocks** : Suivi en temps réel avec alertes de rupture
- **Gestion des Fournisseurs** : Base de données fournisseurs complète
- **Rapports & Analytics** : Tableaux de bord et statistiques de vente

## Technologies utilisées

- **Kotlin** (langage principal)
- **Spring Boot** 3.2.x (REST API, Security)
- **Spring Security** (authentification JWT)
- **Spring Data MongoDB** (accès base de données)
- **MongoDB** 7.x (conteneur Docker)
- **Gradle** (build Kotlin DSL)
- **Docker Compose** (MongoDB local)
- **Spring Cache** (mise en cache)
- **Spring Actuator** (monitoring)

## Architecture des Entités

### Entités Principales
- **User** : Utilisateurs avec rôles hiérarchiques
- **Category** : Catégories et sous-catégories produits
- **Supplier** : Fournisseurs avec coordonnées complètes
- **Product** : Produits avec prix, stock, images, dates expiration
- **Order** : Commandes clients avec statuts et workflow
- **Sale** : Ventes point de vente avec calculs automatiques
- **StockMovement** : Mouvements de stock tracés
- **AuditLog** : Logs d'audit pour traçabilité complète

### Fonctionnalités Spéciales
- **Gestion Alimentaire** : Dates d'expiration, unités de mesure
- **Gestion Électronique** : Codes produits, garanties
- **Soft Delete** : Suppression logique pour préserver l'historique
- **Calculs Automatiques** : TVA, totaux, monnaie rendue
- **Alertes Intelligentes** : Stock bas, expiration proche

---

Pour démarrage rapide et utilisation, voir le README principal ou README-DOCKER.md.
