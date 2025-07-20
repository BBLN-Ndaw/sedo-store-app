// MongoDB initialization script
// This script runs automatically when the container starts for the first time with docker-compose up.

db = db.getSiblingDB('jwtauthdb');

// Create default users for testing
db.users.insertOne({
    username: "testuser",
    password: "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.", // password: "password"
    role: "USER"
});

db.users.insertOne({
    username: "admin",
    password: "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.", // password: "password"
    role: "ADMIN"
});