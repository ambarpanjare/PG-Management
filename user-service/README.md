# User Service — PG Management System

## Overview
Handles user profile management. Authentication is delegated to the Auth Service.
JWT tokens issued by Auth Service are validated here using the shared secret.

## Port
`8082`

## Base URL
`http://localhost:8082`

---

## Prerequisites
- MySQL running on port 3306
- Auth Service running on port 8081
- Discovery Server (Eureka) running on port 8761

---

## API Endpoints

### 1. Create Profile
**POST** `/api/users`
- Auth: Bearer token required
- Body:
```json
{
  "authUserId": 1,
  "firstName": "Rahul",
  "lastName": "Sharma",
  "email": "rahul@example.com",
  "mobile": "9876543210",
  "gender": "MALE",
  "dateOfBirth": "1998-05-15",
  "address": "123 Main Street",
  "city": "Pune",
  "state": "Maharashtra",
  "country": "India",
  "pincode": "411001",
  "emergencyContactName": "Suresh Sharma",
  "emergencyContactNumber": "9123456780",
  "occupation": "Software Engineer",
  "aadhaarNumber": "234567890123",
  "panNumber": "ABCDE1234F"
}
```

---

### 2. Get Profile by ID
**GET** `/api/users/{id}`
- Auth: Bearer token required

---

### 3. Get My Profile
**GET** `/api/users/me`
- Auth: Bearer token required
- Resolves profile from JWT email claim

---

### 4. Update Profile
**PUT** `/api/users/{id}`
- Auth: Bearer token required (must be profile owner)
- Body: any subset of updatable fields
```json
{
  "city": "Mumbai",
  "occupation": "Senior Developer",
  "pincode": "400001"
}
```

---

### 5. Delete Profile
**DELETE** `/api/users/{id}`
- Auth: Bearer token required (must be profile owner)

---

### 6. Search / List Users (Paginated)
**GET** `/api/users?name=Rahul&city=Pune&occupation=Engineer&isVerified=false&page=0&size=10&sortBy=createdAt&sortDir=desc`
- Auth: Bearer token required
- All query params are optional

---

## Standard Response Format
```json
{
  "success": true,
  "message": "User profile created successfully",
  "data": { ... },
  "timestamp": "2024-01-01T10:00:00"
}
```

---

## Swagger UI
`http://localhost:8082/swagger-ui.html`

---

## Postman Setup

### Environment Variables
| Variable | Value |
|---|---|
| `base_url` | `http://localhost:8082` |
| `accessToken` | *(paste from Auth Service login)* |

### Auth Header for all requests
```
Authorization: Bearer {{accessToken}}
```

### Auto-capture token script (run after Auth Service login)
In Postman Login request → Scripts → Post-response:
```javascript
const res = pm.response.json();
pm.environment.set("accessToken", res.data.accessToken);
```

---

## Validation Rules
| Field | Rule |
|---|---|
| email | Valid email format |
| mobile | Indian mobile (6-9 start, 10 digits) |
| pincode | 6-digit Indian pincode |
| aadhaarNumber | 12-digit number |
| panNumber | Format: ABCDE1234F |
| dateOfBirth | Must be in the past |

---

## Error Responses
| Status | Scenario |
|---|---|
| 400 | Validation failure |
| 401 | Missing or invalid JWT |
| 403 | Not profile owner |
| 404 | Profile not found |
| 409 | Email/mobile/authUserId already exists |
| 500 | Internal server error |
