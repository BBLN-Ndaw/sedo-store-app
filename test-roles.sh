#!/bin/bash

# Script de test pour les nouveaux rôles
# Test des permissions selon le contexte métier

BASE_URL="http://localhost:8080/api"

echo "🧪 Test des Nouveaux Rôles - Système de Gestion de Magasin"
echo "============================================================"

# Test des connexions
echo "1. Test des connexions avec les nouveaux rôles..."

echo "👑 Test connexion ADMIN:"
ADMIN_TOKEN=$(curl -s -X POST "$BASE_URL/login" \
    -H "Content-Type: application/json" \
    -d '{"username": "admin", "password": "password"}' | \
    grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$ADMIN_TOKEN" ]; then
    echo "✅ Owner connecté"
else
    echo "❌ Échec connexion Owner"
fi

echo "👷 Test connexion EMPLOYEE:"
EMPLOYEE_TOKEN=$(curl -s -X POST "$BASE_URL/login" \
    -H "Content-Type: application/json" \
    -d '{"username": "employee", "password": "password"}' | \
    grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$EMPLOYEE_TOKEN" ]; then
    echo "✅ Employee connecté"
else
    echo "❌ Échec connexion Employee"
fi

echo "👤 Test connexion CLIENT:"
CLIENT_TOKEN=$(curl -s -X POST "$BASE_URL/login" \
    -H "Content-Type: application/json" \
    -d '{"username": "client", "password": "password"}' | \
    grep -o '"token":"[^"]*"' | cut -d'"' -f4)

if [ -n "$CLIENT_TOKEN" ]; then
    echo "✅ Client connecté"
else
    echo "❌ Échec connexion Client"
fi

echo ""

# Test des permissions
echo "2. Test des permissions par rôle..."

echo "🔒 Test accès users (seul ADMIN autorisé):"
echo "Owner:" 
curl -s -X GET "$BASE_URL/users" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -w " Status: %{http_code}\n" | head -1

echo "Employee:"
curl -s -X GET "$BASE_URL/users" \
    -H "Authorization: Bearer $EMPLOYEE_TOKEN" \
    -w " Status: %{http_code}\n" | head -1

echo "Client:"
curl -s -X GET "$BASE_URL/users" \
    -H "Authorization: Bearer $CLIENT_TOKEN" \
    -w " Status: %{http_code}\n" | head -1

echo ""

echo "📂 Test accès categories (tous autorisés en lecture):"
echo "Owner:"
curl -s -X GET "$BASE_URL/categories" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -w " Status: %{http_code}\n" | head -1

echo "Employee:"
curl -s -X GET "$BASE_URL/categories" \
    -H "Authorization: Bearer $EMPLOYEE_TOKEN" \
    -w " Status: %{http_code}\n" | head -1

echo "Client:"
curl -s -X GET "$BASE_URL/categories" \
    -H "Authorization: Bearer $CLIENT_TOKEN" \
    -w " Status: %{http_code}\n" | head -1

echo ""

echo "➕ Test création category (ADMIN et EMPLOYEE autorisés):"
echo "Owner:"
curl -s -X POST "$BASE_URL/categories" \
    -H "Authorization: Bearer $ADMIN_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name": "Test Owner", "description": "Créé par admin"}' \
    -w " Status: %{http_code}\n" | head -1

echo "Employee:"
curl -s -X POST "$BASE_URL/categories" \
    -H "Authorization: Bearer $EMPLOYEE_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name": "Test Employee", "description": "Créé par employee"}' \
    -w " Status: %{http_code}\n" | head -1

echo "Client (devrait échouer):"
curl -s -X POST "$BASE_URL/categories" \
    -H "Authorization: Bearer $CLIENT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{"name": "Test Client", "description": "Créé par client"}' \
    -w " Status: %{http_code}\n" | head -1

echo ""
echo "🎉 Tests des rôles terminés !"
