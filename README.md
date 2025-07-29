# Système de Gestion de Magasin (Store Management System)

Une application Spring Boot complète pour la gestion d'un magasin avec e-commerce intégré, développée en Kotlin avec MongoDB.

## 🚀 Fonctionnalités

### 🔐 Authentification & Sécurité
- **JWT Authentication**: Système d'authentification sécurisé par token
- **Gestion des Rôles**: Support pour ADMIN, MANAGER et CLIENT (USER)
- **Audit Trail**: Traçabilité complète de toutes les actions utilisateurs
- **Chiffrement des Mots de Passe**: Hachage BCrypt

### 📦 Gestion des Produits
- **Catalogue Produits**: Gestion complète des produits (alimentaire, électronique, etc.)
- **Catégories Hiérarchiques**: Organisation en catégories et sous-catégories
- **Codes SKU**: Système de codes produits uniques
- **Images Produits**: Support pour multiple images par produit
- **Gestion des Prix**: Prix d'achat et de vente séparés

### 📊 Gestion des Stocks
- **Suivi en Temps Réel**: Quantités actualisées automatiquement
- **Alertes Stock Bas**: Notifications pour les produits en rupture
- **Historique des Mouvements**: Traçabilité complète des entrées/sorties
- **Gestion des Dates d'Expiration**: Spécial produits alimentaires

### 🛒 E-Commerce & Commandes
- **Commandes en Ligne**: Système de commande pour les clients
- **Retrait en Magasin**: Gestion des retraits uniquement
- **Statuts de Commande**: Suivi complet du processus
- **Modes de Paiement**: Espèces, carte bancaire, PayPal (futur)

### 💰 Point de Vente (POS)
- **Ventes Directes**: Interface de caisse pour ventes immédiates
- **Gestion des Remises**: Application de réductions
- **Calcul Automatique**: TVA et totaux calculés automatiquement
- **Reçus de Vente**: Génération de factures

### 👥 Gestion Administrative
- **Gestion des Fournisseurs**: Base de données fournisseurs
- **Rapports de Ventes**: Statistiques et analyses
- **Audit Complet**: Logs détaillés de toutes les actions
- **Dashboard**: Tableaux de bord pour ADMIN et MANAGER

## 🛠 Tech Stack

- **Language**: Kotlin
- **Framework**: Spring Boot 3.2.0
- **Security**: Spring Security with JWT
- **Database**: MongoDB
- **Build Tool**: Gradle (Kotlin DSL)
- **JWT Library**: JJWT 0.11.5
- **Validation**: Jakarta Validation
- **Cache**: Spring Cache
- **Monitoring**: Spring Actuator

## 🏗 Architecture

```
src/main/kotlin/com/sedo/jwtauth/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── model/
│   ├── dto/        # Data Transfer Objects
│   └── entity/     # MongoDB entities
├── repository/      # Data repositories
├── service/        # Business logic services
├── util/           # Utility classes
├── filter/         # Security filters
├── exception/      # Custom exceptions
└── constants/      # Application constants
```

## 📚 Entities

### Core Entities
- **User**: Utilisateurs avec rôles (ADMIN, MANAGER, CLIENT)
- **Category**: Catégories et sous-catégories de produits
- **Supplier**: Fournisseurs avec informations de contact
- **Product**: Produits avec prix, stock, images
- **Order**: Commandes clients avec statuts
- **Sale**: Ventes point de vente
- **StockMovement**: Mouvements de stock avec traçabilité
- **AuditLog**: Logs d'audit pour traçabilité

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- MongoDB (local or Docker)
- Gradle (or use the included Gradle wrapper)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd Jwt_auth_app
   ```

2. **Start MongoDB** (using Docker Compose)
   ```bash
   docker-compose -f docker-compose.dev.yml up -d
   ```

3. **Configure application properties**
   
   Les paramètres MongoDB sont dans `src/main/resources/application.yml`:
   ```yaml
   spring:
     data:
       mongodb:
         host: localhost
         port: 27017
         database: jwtauthdb
         username: admin
         password: password123
   ```

4. **Build and run the application**
   ```bash
   ./gradlew bootRun
   ```

   L'application démarre sur `http://localhost:8080`

### 🐳 Docker Deployment

1. **Using Docker Compose**
   ```bash
   docker-compose up -d
   ```

## 📡 API Endpoints

### Authentication

| Method | Endpoint | Description | Rôle Requis |
|--------|----------|-------------|-------------|
| POST | `/api/login` | Connexion utilisateur | Public |

### Gestion des Utilisateurs

| Method | Endpoint | Description | Rôle Requis |
|--------|----------|-------------|-------------|
| GET | `/api/users` | Liste tous les utilisateurs | ADMIN |
| GET | `/api/users/{id}` | Utilisateur par ID | ADMIN/MANAGER |
| POST | `/api/users` | Créer un utilisateur | ADMIN/MANAGER |
| PUT | `/api/users/{id}` | Modifier un utilisateur | ADMIN |
| DELETE | `/api/users/{id}` | Supprimer un utilisateur | ADMIN |

### Gestion des Catégories

| Method | Endpoint | Description | Rôle Requis |
|--------|----------|-------------|-------------|
| GET | `/api/categories` | Liste toutes les catégories | Tous |
| GET | `/api/categories/{id}` | Catégorie par ID | Tous |
| GET | `/api/categories/main` | Catégories principales | Tous |
| GET | `/api/categories/{id}/subcategories` | Sous-catégories | Tous |
| POST | `/api/categories` | Créer une catégorie | ADMIN/MANAGER |
| PUT | `/api/categories/{id}` | Modifier une catégorie | ADMIN/MANAGER |
| DELETE | `/api/categories/{id}` | Supprimer une catégorie | ADMIN |
| GET | `/api/categories/search` | Rechercher des catégories | Tous |

### Gestion des Produits (à implémenter)

| Method | Endpoint | Description | Rôle Requis |
|--------|----------|-------------|-------------|
| GET | `/api/products` | Liste tous les produits | Tous |
| GET | `/api/products/{id}` | Produit par ID | Tous |
| POST | `/api/products` | Créer un produit | ADMIN/MANAGER |
| PUT | `/api/products/{id}` | Modifier un produit | ADMIN/MANAGER |
| DELETE | `/api/products/{id}` | Supprimer un produit | ADMIN |
| GET | `/api/products/low-stock` | Produits en rupture | MANAGER/ADMIN |

### Gestion des Commandes (à implémenter)

| Method | Endpoint | Description | Rôle Requis |
|--------|----------|-------------|-------------|
| GET | `/api/orders` | Liste des commandes | ADMIN/MANAGER |
| GET | `/api/orders/{id}` | Commande par ID | Propriétaire/MANAGER/ADMIN |
| POST | `/api/orders` | Créer une commande | CLIENT |
| PUT | `/api/orders/{id}/status` | Changer le statut | MANAGER/ADMIN |

## 📝 Request/Response Examples

### Login Request
```json
{
  "username": "admin",
  "password": "password"
}
```

### Login Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Create Category Request
```json
{
  "name": "Nouvelle Catégorie",
  "description": "Description de la catégorie",
  "parentCategoryId": null,
  "isActive": true
}
```

### Create User Request
```json
{
  "username": "newuser",
  "password": "securepassword",
  "roles": ["USER"]
}
```

### Product Example
```json
{
  "sku": "PROD001",
  "name": "iPhone 15",
  "description": "Dernier iPhone Apple",
  "categoryId": "64a1b2c3d4e5f6789abc1234",
  "supplierId": "64a1b2c3d4e5f6789abc5678",
  "purchasePrice": 800.00,
  "sellingPrice": 1200.00,
  "stockQuantity": 50,
  "minimumStock": 10,
  "unit": "pièce",
  "tags": ["smartphone", "apple", "tech"]
}
```

## 👤 Utilisateurs par Défaut

L'application crée des utilisateurs par défaut au démarrage :

| Username | Password | Rôle | Description |
|----------|----------|------|-------------|
| owner | password | OWNER | Propriétaire - contrôle total du système |
| employee | password | EMPLOYEE | Employé/Gestionnaire - gestion quotidienne |
| client | password | CLIENT | Client - peut passer des commandes |

### 🔐 Permissions par Rôle

#### OWNER (Propriétaire)
- ✅ Accès complet à tous les modules
- ✅ Gestion des utilisateurs
- ✅ Rapports et audit complets
- ✅ Configuration système
- ✅ Suppression de données

#### EMPLOYEE (Gestionnaire/Employé)
- ✅ Gestion des produits et stocks
- ✅ Traitement des commandes
- ✅ Point de vente (POS)
- ✅ Gestion des fournisseurs
- ✅ Rapports de vente
- ❌ Gestion des utilisateurs
- ❌ Audit logs complets

#### CLIENT
- ✅ Consultation du catalogue
- ✅ Passage de commandes
- ✅ Suivi de ses commandes
- ❌ Accès aux données de gestion
- ❌ Modification des produits

## 📊 Données d'Exemple

### Catégories créées automatiquement :
- **Alimentaire**
  - Fruits & Légumes
  - Viandes & Poissons
  - Produits Laitiers
  - Boissons
- **Électronique**
  - Smartphones & Tablettes
  - Ordinateurs
  - Électroménager
- **Hygiène & Beauté**
- **Maison & Jardin**

## ⚙️ Configuration

### JWT Configuration

Configure JWT settings in `application.yml`:

```yaml
jwt:
  secret: myDefaultSecretKeyForJwtTokenGeneration1234567890
  expiration: 86400000  # 24 hours in milliseconds
```

### MongoDB Configuration

```yaml
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: jwtauthdb
      username: admin
      password: password123
      authentication-database: admin
```

### Logging Configuration

Logging is configured in `src/main/resources/logback-spring.xml`:
- Development profile: Console and file logging
- Production profile: File logging only with log rotation

## 🏗 Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/sedo/jwtauth/
│   │       ├── config/          # Configuration classes
│   │       ├── controller/      # REST controllers
│   │       ├── model/
│   │       │   ├── dto/         # Data Transfer Objects
│   │       │   └── entity/      # MongoDB entities
│   │       ├── repository/      # Data repositories
│   │       ├── service/         # Business logic
│   │       ├── util/            # Utility classes
│   │       ├── filter/          # Security filters
│   │       ├── exception/       # Custom exceptions
│   │       └── constants/       # Application constants
│   └── resources/
│       ├── application.yml      # Application configuration
│       ├── application-dev.yml  # Development configuration
│       └── logback-spring.xml   # Logging configuration
└── test/                        # Test files
```

## 🔒 Security & Features

- **Password Hashing**: Tous les mots de passe sont chiffrés avec BCrypt
- **JWT Security**: Tokens signés avec l'algorithme HS256
- **Role-Based Access**: Endpoints protégés selon les rôles utilisateur
- **Input Validation**: Validation complète des entrées utilisateur
- **Security Headers**: CORS et autres headers de sécurité configurés
- **Audit Trail**: Traçabilité complète des actions pour conformité
- **Soft Delete**: Suppression logique pour préserver l'historique

## 🧪 Testing

Run tests using:
```bash
./gradlew test
```

## 🚀 Building for Production

1. **Build the JAR file**
   ```bash
   ./gradlew bootJar
   ```

2. **Run the JAR**
   ```bash
   java -jar build/libs/Jwt_auth_app-0.0.1-SNAPSHOT.jar
   ```

## 🎨 Design & UI

### Palette de Couleurs
- **Primaire**: Bleu professionnel (#2563EB)
- **Secondaire**: Vert succès (#10B981)
- **Accent**: Orange énergique (#F59E0B)
- **Neutre**: Gris moderne (#6B7280)
- **Arrière-plan**: Blanc/Gris clair (#F9FAFB)

### Style Design
- Design Material moderne avec cards et shadows subtiles
- Navigation sidebar pour les modules
- Dashboard avec widgets interactifs
- Tables avec pagination et filtres avancés
- Formulaires structurés avec validation

## 🔄 Statut du Développement

### ✅ Implémenté
- [x] Authentification JWT
- [x] Gestion des utilisateurs avec rôles
- [x] Gestion des catégories complète
- [x] Audit trail
- [x] Configuration Docker

### 🚧 En Cours
- [ ] Gestion des fournisseurs
- [ ] Gestion des produits
- [ ] Gestion des stocks
- [ ] Système de commandes
- [ ] Point de vente (POS)
- [ ] Dashboard administratif
- [ ] Rapports et statistiques

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 📞 Contact

Pour questions ou support, contactez [sedo-sebe@example.com]

## 🚀 Prochaines Étapes

1. **Compléter les modules** : Produits, Fournisseurs, Stocks
2. **Interface Angular** : Développer le frontend
3. **Rapports avancés** : Analytics et business intelligence
4. **API Mobile** : Support pour applications mobiles
5. **Intégrations** : Systèmes de paiement, comptabilité

---

**Développé avec ❤️ en Kotlin & Spring Boot**
