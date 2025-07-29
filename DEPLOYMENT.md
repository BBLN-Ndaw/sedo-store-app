# Guide de Déploiement - Système de Gestion de Magasin

## 🚀 Déploiement Local

### Prérequis
- Java 17+
- Docker & Docker Compose
- Git

### Étapes de Déploiement

1. **Cloner le projet**
   ```bash
   git clone <repository-url>
   cd Jwt_auth_app
   ```

2. **Démarrer MongoDB**
   ```bash
   docker-compose -f docker-compose.dev.yml up -d
   ```

3. **Construire et lancer l'application**
   ```bash
   ./gradlew bootRun
   ```

4. **Vérifier le déploiement**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

### URLs d'accès
- **API**: http://localhost:8080/api
- **Health Check**: http://localhost:8080/actuator/health
- **MongoDB**: localhost:27017

## 🐳 Déploiement Docker

### Option 1: Docker Compose (Recommandé)
```bash
docker-compose up -d
```

### Option 2: Docker Build Manuel
```bash
# Construire l'image
docker build -t store-management-system .

# Lancer le conteneur
docker run -d \
  --name store-app \
  -p 8080:8080 \
  --link mongodb:mongodb \
  store-management-system
```

## ☁️ Déploiement Cloud

### Configuration Environnement
Créer un fichier `application-prod.yml` :

```yaml
spring:
  data:
    mongodb:
      uri: ${MONGODB_URI:mongodb://localhost:27017/jwtauthdb}
  
jwt:
  secret: ${JWT_SECRET:your-super-secret-key-here}
  expiration: ${JWT_EXPIRATION:86400000}

server:
  port: ${PORT:8080}

logging:
  level:
    com.sedo.jwtauth: INFO
    org.springframework: WARN
```

### Variables d'Environnement
```bash
export MONGODB_URI="mongodb://user:pass@host:port/database"
export JWT_SECRET="your-production-secret-key"
export JWT_EXPIRATION="86400000"
export PORT="8080"
```

### Heroku
```bash
# Installer Heroku CLI
heroku create store-management-app

# Configurer les variables
heroku config:set MONGODB_URI="your-mongodb-uri"
heroku config:set JWT_SECRET="your-secret"

# Déployer
git push heroku main
```

### AWS EC2
```bash
# Sur l'instance EC2
sudo apt update
sudo apt install openjdk-17-jdk

# Transférer le JAR
scp build/libs/Jwt_auth_app-0.0.1-SNAPSHOT.jar ec2-user@your-ec2-ip:~/

# Lancer l'application
nohup java -jar Jwt_auth_app-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod &
```

## 🔧 Configuration Avancée

### SSL/HTTPS
Ajouter dans `application-prod.yml` :
```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: password
    key-store-type: PKCS12
```

### Monitoring avec Actuator
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
```

### Load Balancer
Configuration Nginx :
```nginx
upstream store-api {
    server localhost:8080;
    server localhost:8081;
}

server {
    listen 80;
    server_name your-domain.com;
    
    location /api/ {
        proxy_pass http://store-api;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 📊 Surveillance et Maintenance

### Logs
```bash
# Voir les logs en temps réel
tail -f logs/jwt-auth-app.log

# Rechercher les erreurs
grep "ERROR" logs/jwt-auth-app.log
```

### Métriques
- **Health Check**: `/actuator/health`
- **Métriques**: `/actuator/metrics`
- **Info**: `/actuator/info`

### Backup MongoDB
```bash
# Backup
mongodump --host localhost:27017 --db jwtauthdb --out backup/

# Restore
mongorestore --host localhost:27017 --db jwtauthdb backup/jwtauthdb/
```

## 🔐 Sécurité Production

### Checklist Sécurité
- [ ] Changer le secret JWT par défaut
- [ ] Utiliser HTTPS uniquement
- [ ] Configurer les CORS correctement
- [ ] Activer les logs d'audit
- [ ] Mettre à jour les dépendances
- [ ] Configurer un pare-feu
- [ ] Chiffrer la base de données
- [ ] Utiliser des secrets managers

### Configuration CORS Production
```kotlin
@Bean
fun corsConfigurer(): WebMvcConfigurer {
    return object : WebMvcConfigurer {
        override fun addCorsMappings(registry: CorsRegistry) {
            registry.addMapping("/**")
                .allowedOrigins("https://your-frontend-domain.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowedHeaders("*")
                .allowCredentials(true)
        }
    }
}
```

## 🚨 Dépannage

### Problèmes Courants

1. **Port déjà utilisé**
   ```bash
   sudo lsof -i :8080
   sudo kill -9 <PID>
   ```

2. **MongoDB non accessible**
   ```bash
   docker ps | grep mongo
   docker logs jwt-auth-mongodb
   ```

3. **Erreur de mémoire**
   ```bash
   export JAVA_OPTS="-Xmx512m -Xms256m"
   java $JAVA_OPTS -jar app.jar
   ```

4. **Problème de permissions**
   ```bash
   chmod +x gradlew
   ```

### Vérifications Post-Déploiement
```bash
# Test de l'API
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "password"}'

# Test de santé
curl http://localhost:8080/actuator/health
```
