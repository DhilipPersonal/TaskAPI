# 🔐 TaskAPI - Swagger UI JWT Testing Guide

## 📍 **Swagger UI Access Link**
```
🌐 http://localhost:8081/swagger-ui/index.html
```

**Application Status**: ✅ **RUNNING** on port 8081

---

## 🚀 **Complete JWT Testing Steps**

### **Step 1: Access Swagger UI**
1. Open your browser and navigate to: **http://localhost:8081/swagger-ui/index.html**
2. You should see the "Task Management API 1.0.0" documentation
3. All endpoints are visible with different categories (Authentication, user-controller, etc.)

### **Step 2: Register a New User**
1. **Find the Authentication section** in Swagger UI
2. **Locate POST `/api/v1/users/register`** endpoint
3. **Click "Try it out"**
4. **Enter the following JSON payload**:
```json
{
  "username": "testuser",
  "email": "test@example.com",
  "password": "password123",
  "firstName": "Test",
  "lastName": "User"
}
```
5. **Click "Execute"**
6. **Expected Response**: `201 Created` with user profile data

### **Step 3: Login to Get JWT Token**
1. **Find POST `/api/v1/auth/login`** endpoint
2. **Click "Try it out"**
3. **Enter login credentials**:
```json
{
  "username": "testuser",
  "password": "password123"
}
```
4. **Click "Execute"**
5. **Expected Response**: `200 OK` with JWT tokens
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzMzNDg4NDAwLCJleHAiOjE3MzM1NzQ4MDB9.example",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInR5cGUiOiJSRUZSRVNIIiwiaWF0IjoxNzMzNDg4NDAwLCJleHAiOjE3MzQwOTMyMDB9.example",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```
6. **📋 Copy the `accessToken` value** (you'll need this for authentication)

### **Step 4: Authorize in Swagger UI**
1. **Look for the "Authorize" button** at the top right of Swagger UI (🔒 icon)
2. **Click "Authorize"**
3. **In the "bearerAuth" field, enter**:
```
Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzMzNDg4NDAwLCJleHAiOjE3MzM1NzQ4MDB9.your-actual-token-here
```
**⚠️ Important**: Replace with your actual token from Step 3, and include "Bearer " prefix
4. **Click "Authorize"**
5. **Click "Close"**
6. **✅ You should now see a lock icon (🔒) next to protected endpoints**

### **Step 5: Test Protected Endpoints**
Now you can test any protected endpoint. Here are some examples:

#### **5.1 Get Current User Profile**
1. **Find GET `/api/v1/users/me`**
2. **Click "Try it out"**
3. **Click "Execute"**
4. **Expected Response**: `200 OK` with your user profile

#### **5.2 Create a Task**
1. **Find POST `/api/v1/tasks`**
2. **Click "Try it out"**
3. **Enter task data**:
```json
{
  "title": "Test Task",
  "description": "This is a test task created via Swagger",
  "priority": "MEDIUM",
  "status": "TODO"
}
```
4. **Click "Execute"**
5. **Expected Response**: `201 Created` with task details

#### **5.3 Get All Tasks**
1. **Find GET `/api/v1/tasks`**
2. **Click "Try it out"**
3. **Click "Execute"**
4. **Expected Response**: `200 OK` with list of tasks

#### **5.4 Test Analytics (Cached Endpoint)**
1. **Find GET `/api/v1/analytics/productivity-metrics`**
2. **Click "Try it out"**
3. **Click "Execute"**
4. **Expected Response**: `200 OK` with analytics data
5. **🔄 Test again** - should be faster due to caching

### **Step 6: Test Admin Endpoints (Role-Based Access)**
#### **6.1 Try Admin Endpoint with User Role**
1. **Find GET `/api/v1/admin/users`**
2. **Click "Try it out"**
3. **Click "Execute"**
4. **Expected Response**: `403 Forbidden` (Access Denied - insufficient privileges)

#### **6.2 Create Admin User (if needed)**
1. **Register a new user** following Step 2
2. **Use different credentials** (e.g., username: "admin", email: "admin@example.com")
3. **Login with admin credentials**
4. **Get new JWT token**
5. **Update authorization** with admin token

### **Step 7: Test Token Refresh**
1. **Find POST `/api/v1/auth/refresh`**
2. **Click "Try it out"**
3. **Enter refresh token from Step 3**:
```json
{
  "refreshToken": "your-refresh-token-here"
}
```
4. **Click "Execute"**
5. **Expected Response**: `200 OK` with new access token

### **Step 8: Test Logout (Token Blacklisting)**
1. **Find POST `/api/v1/auth/logout`**
2. **Click "Try it out"**
3. **Click "Execute"** (uses current JWT token)
4. **Expected Response**: `200 OK` with logout confirmation
5. **🧪 Test blacklisting**: Try using the same token again on any protected endpoint
6. **Expected Result**: `401 Unauthorized` (token is blacklisted)

---

## 🔧 **Advanced Testing Scenarios**

### **Rate Limiting Test**
1. **Find any authentication endpoint** (e.g., `/api/v1/auth/login`)
2. **Make multiple rapid requests** (try 10+ times quickly)
3. **Expected Result**: After 100 requests, you'll get `429 Too Many Requests`
4. **Check response headers** for rate limit information:
   - `X-RateLimit-Remaining`
   - `X-RateLimit-Reset`

### **File Upload Test**
1. **Find POST `/api/v1/tasks/{id}/attachments`** (if available)
2. **Create a task first** (Step 5.2)
3. **Upload a file** to the task
4. **Test file download** endpoints

### **Error Handling Test**
1. **Try invalid JSON** in request bodies
2. **Use invalid IDs** in path parameters
3. **Send requests without authorization**
4. **Expected Result**: Consistent error response format with proper HTTP status codes

---

## 📊 **Testing Checklist**

### ✅ **Authentication Flow**
- [ ] User registration works
- [ ] Login returns JWT tokens
- [ ] Authorization header accepted
- [ ] Protected endpoints accessible with valid token
- [ ] Token refresh works
- [ ] Logout blacklists tokens

### ✅ **Authorization (RBAC)**
- [ ] User role can access user endpoints
- [ ] Admin role can access admin endpoints
- [ ] Users cannot access admin endpoints
- [ ] Proper 403 responses for insufficient privileges

### ✅ **API Functionality**
- [ ] CRUD operations work for all entities
- [ ] Validation errors return proper messages
- [ ] Caching works for analytics endpoints
- [ ] Rate limiting enforced
- [ ] File upload/download works

### ✅ **Error Handling**
- [ ] Consistent error response format
- [ ] Proper HTTP status codes
- [ ] User-friendly error messages
- [ ] No sensitive information in errors

---

## 🚨 **Common Issues & Solutions**

### **Issue 1: "401 Unauthorized" on protected endpoints**
**Solution**: 
- Ensure you've clicked "Authorize" in Swagger UI
- Check that token includes "Bearer " prefix
- Verify token hasn't expired (24-hour expiration)

### **Issue 2: "403 Forbidden" on admin endpoints**
**Solution**:
- User role cannot access admin endpoints
- Need to create admin user or update user role
- Check role-based access control configuration

### **Issue 3: "429 Too Many Requests"**
**Solution**:
- Rate limiting is working correctly
- Wait for rate limit reset (check X-RateLimit-Reset header)
- This protects the API from abuse

### **Issue 4: Token expired**
**Solution**:
- Use refresh token to get new access token
- Re-login if refresh token also expired
- Update authorization with new token

---

## 🎯 **Demo Tips for Manager Presentation**

### **Quick Demo Flow (5 minutes)**
1. **Show Swagger UI** - "This is our interactive API documentation"
2. **Register & Login** - "Secure user authentication with JWT"
3. **Authorize & Test** - "Role-based access control in action"
4. **Create Task** - "Core business functionality"
5. **Show Analytics** - "Performance optimization with caching"
6. **Test Admin Endpoint** - "Enterprise security with role permissions"

### **Key Points to Highlight**
- **Professional Documentation**: Interactive API docs with Swagger
- **Enterprise Security**: JWT authentication, role-based access, token blacklisting
- **Performance Features**: Redis caching, rate limiting
- **Production Ready**: Comprehensive error handling, validation
- **Developer Friendly**: Easy testing and integration

---

## 📱 **Quick Access URLs**

- **🌐 Swagger UI**: http://localhost:8081/swagger-ui/index.html
- **📊 API Docs JSON**: http://localhost:8081/v3/api-docs
- **🔍 H2 Console**: http://localhost:8081/h2-console (for database inspection)

---

**🎉 Your TaskAPI is now ready for comprehensive JWT testing via Swagger UI!**

*This guide covers all authentication scenarios, role-based access control, and advanced features like caching and rate limiting.*
