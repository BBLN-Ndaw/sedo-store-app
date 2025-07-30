# API Documentation - Système de Gestion de Magasin

## Base URL
```
http://localhost:8080/api
```

## Authentication
Toutes les requêtes (sauf `/api/login`) nécessitent un token JWT dans le header :
```
Authorization: Bearer <jwt_token>
```

## Endpoints

### 🔐 Authentication

#### Login
#### Login Examples
```bash
# Propriétaire
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username": "owner", "password": "password"}'

# Employé/Gestionnaire  
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username": "employee", "password": "password"}'

# Client
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username": "client", "password": "password"}'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 👥 User Management

#### Get All Users
```http
GET /api/users
Authorization: Bearer <token>
```
*Requires: ADMIN role*

#### Get User by ID
```http
GET /api/users/{id}
Authorization: Bearer <token>
```
*Requires: ADMIN role*

#### Create User
```http
POST /api/users
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "newclient",
  "password": "securepass",
  "roles": ["CLIENT"]
}
```
*Requires: ADMIN role*

#### Update User
```http
PUT /api/users/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "username": "updateduser",
  "password": "newpass",
  "roles": ["EMPLOYEE"]
}
```
*Requires: ADMIN role*

#### Delete User
```http
DELETE /api/users/{id}
Authorization: Bearer <token>
```
*Requires: ADMIN role*

### 📂 Category Management

#### Get All Categories
```http
GET /api/categories
Authorization: Bearer <token>
```
*Requires: Any authenticated user*

#### Get Category by ID
```http
GET /api/categories/{id}
Authorization: Bearer <token>
```
*Requires: Any authenticated user*

#### Get Main Categories
```http
GET /api/categories/main
Authorization: Bearer <token>
```
*Requires: Any authenticated user*

#### Get Subcategories
```http
GET /api/categories/{parentId}/subcategories
Authorization: Bearer <token>
```
*Requires: Any authenticated user*

#### Search Categories
```http
GET /api/categories/search?query=alimentaire
Authorization: Bearer <token>
```
*Requires: Any authenticated user*

#### Create Category
```http
POST /api/categories
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Nouvelle Catégorie",
  "description": "Description de la catégorie",
  "parentCategoryId": null,
  "isActive": true
}
```
*Requires: ADMIN or EMPLOYEE role*

#### Update Category
```http
PUT /api/categories/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "name": "Catégorie Modifiée",
  "description": "Nouvelle description",
  "parentCategoryId": "64a1b2c3d4e5f6789abc1234",
  "isActive": true
}
```
*Requires: ADMIN or EMPLOYEE role*

#### Delete Category
```http
DELETE /api/categories/{id}
Authorization: Bearer <token>
```
*Requires: ADMIN role*

## Error Responses

### Standard Error Format
```json
{
  "code": "ERROR_CODE",
  "message": "Human readable error message",
  "timestamp": 1674567890123
}
```

### Validation Error Format
```json
{
  "code": "VALIDATION_ERROR",
  "message": "Validation failed",
  "errors": [
    {
      "field": "name",
      "message": "Category name is required"
    }
  ],
  "timestamp": 1674567890123
}
```

### Common Error Codes
- `USER_NOT_FOUND` - Utilisateur non trouvé
- `RESOURCE_NOT_FOUND` - Ressource non trouvée
- `INVALID_CREDENTIALS` - Identifiants invalides
- `INVALID_TOKEN` - Token JWT invalide
- `VALIDATION_ERROR` - Erreur de validation
- `INSUFFICIENT_STOCK` - Stock insuffisant
- `INVALID_OPERATION` - Opération invalide
- `INTERNAL_ERROR` - Erreur serveur interne

## HTTP Status Codes
- `200 OK` - Succès
- `201 Created` - Ressource créée
- `204 No Content` - Suppression réussie
- `400 Bad Request` - Requête invalide
- `401 Unauthorized` - Non authentifié
- `403 Forbidden` - Accès interdit
- `404 Not Found` - Ressource non trouvée
- `500 Internal Server Error` - Erreur serveur

## Examples with cURL

### Login
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password"}'
```

### Get Categories
```bash
curl -X GET http://localhost:8080/api/categories \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Create Category
```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Nouvelle Catégorie",
    "description": "Description",
    "isActive": true
  }'
```
