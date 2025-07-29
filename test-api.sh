#!/bin/bash

# Script de test pour l'API du système de gestion de magasin
# Ce script teste les fonctionnalités principales de l'API

BASE_URL="http://localhost:8080/api"
TOKEN=""

echo "🧪 Test de l'API - Système de Gestion de Magasin"
echo "================================================"

# Fonction pour afficher les résultats
show_result() {
    local status=$1
    local response=$2
    if [ $status -eq 0 ]; then
        echo "✅ Succès"
        echo "Response: $response"
    else
        echo "❌ Échec"
        echo "Error: $response"
    fi
    echo "---"
}

# Test 1: Login
echo "1. Test de connexion..."
RESPONSE=$(curl -s -X POST "$BASE_URL/login" \
    -H "Content-Type: application/json" \
    -d '{"username": "admin", "password": "password"}')

if echo "$RESPONSE" | grep -q "token"; then
    TOKEN=$(echo "$RESPONSE" | grep -o '"token":"[^"]*"' | cut -d'"' -f4)
    show_result 0 "Token obtenu"
else
    show_result 1 "$RESPONSE"
    exit 1
fi

# Test 2: Get all users (Admin only)
echo "2. Test de récupération des utilisateurs..."
RESPONSE=$(curl -s -X GET "$BASE_URL/users" \
    -H "Authorization: Bearer $TOKEN")
show_result $? "$RESPONSE"

# Test 3: Get all categories
echo "3. Test de récupération des catégories..."
RESPONSE=$(curl -s -X GET "$BASE_URL/categories" \
    -H "Authorization: Bearer $TOKEN")
show_result $? "$RESPONSE"

# Test 4: Get main categories
echo "4. Test de récupération des catégories principales..."
RESPONSE=$(curl -s -X GET "$BASE_URL/categories/main" \
    -H "Authorization: Bearer $TOKEN")
show_result $? "$RESPONSE"

# Test 5: Create a new category
echo "5. Test de création d'une nouvelle catégorie..."
RESPONSE=$(curl -s -X POST "$BASE_URL/categories" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "name": "Test Category",
        "description": "Catégorie créée pour test",
        "isActive": true
    }')
show_result $? "$RESPONSE"

# Test 6: Search categories
echo "6. Test de recherche de catégories..."
RESPONSE=$(curl -s -X GET "$BASE_URL/categories/search?query=Test" \
    -H "Authorization: Bearer $TOKEN")
show_result $? "$RESPONSE"

# Test 7: Test unauthorized access
echo "7. Test d'accès non autorisé..."
RESPONSE=$(curl -s -X GET "$BASE_URL/categories")
if echo "$RESPONSE" | grep -q "error\|unauthorized\|forbidden"; then
    show_result 0 "Accès correctement bloqué"
else
    show_result 1 "Sécurité défaillante"
fi

echo "🎉 Tests terminés !"
