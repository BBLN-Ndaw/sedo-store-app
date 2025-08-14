// MongoDB initialization script
// This script runs automatically when the container starts for the first time with docker-compose up.

db = db.getSiblingDB('jwtauthdb');

// Create default users for testing
db.users.insertOne({
    userName: "testuser",
    password: "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.", // password: "password"
    email: "testuser@test.com",
    firstName: "Test",
    lastName: "User",
    isActive: true,
    roles: ["CUSTOMER"],
    createdAt: new Date()
});

db.users.insertOne({
    userName: "admin",
    password: "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.", // password: "password"
    email: "admin@test.com",
    firstName: "Admin",
    lastName: "User",
    isActive: true,
    roles: ["ADMIN"],
    createdAt: new Date()
});

db.users.insertOne({
    userName: "employee",
    password: "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.", // password: "password"
    email: "employee@test.com",
    firstName: "Employee",
    lastName: "User",
    isActive: true,
    roles: ["EMPLOYEE"],
    createdAt: new Date()
});