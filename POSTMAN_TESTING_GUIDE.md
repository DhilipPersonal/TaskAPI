# 📮 TaskAPI Postman Testing Guide

## 🚀 Quick Setup

### 1. Import Collection and Environment
1. **Open Postman**
2. **Import Collection**: 
   - Click "Import" → Select `TaskAPI.postman_collection.json`
3. **Import Environment**: 
   - Click "Import" → Select `TaskAPI.postman_environment.json`
4. **Select Environment**: 
   - Choose "TaskAPI Environment" from the environment dropdown

### 2. Verify Base URL
- Ensure `base_url` is set to `http://localhost:8081` in your environment
- Make sure your TaskAPI application is running

---

## 🔐 Authentication Flow Testing

### Step 1: Register a New User
```
POST /api/v1/auth/register
```
**Request Body:**
```json
{
  "username": "testuser",
  "email": "testuser@example.com",
  "password": "Password123!",
  "firstName": "Test",
  "lastName": "User"
}
```
**Expected Response:** `201 Created`

### Step 2: Login and Get JWT Token
```
POST /api/v1/auth/login
```
**Request Body:**
```json
{
  "username": "testuser",
  "password": "Password123!"
}
```
**Expected Response:** `200 OK` with `accessToken` and `refreshToken`

**✨ Auto-Token Extraction:** The login request automatically saves the tokens to environment variables!

### Step 3: Test Protected Endpoints
Now you can test any protected endpoint. The collection is configured to automatically use the saved JWT token.

### Step 4: Test Token Blacklisting
```
POST /api/v1/auth/logout
```
After logout, try any protected endpoint - you should get `401 Unauthorized`.

---

## 📋 Complete Testing Workflow

### 🔄 Recommended Testing Order:

1. **Health Check** → Test public endpoint
2. **Register User** → Create new account
3. **Login** → Get JWT tokens (auto-saved)
4. **Get All Tasks** → Test protected endpoint
5. **Create Task** → Test POST with authentication
6. **Get Task by ID** → Test specific resource access
7. **Update Task** → Test PUT operation
8. **Upload File** → Test file attachment
9. **Get Analytics** → Test cached endpoints
10. **Logout** → Test token blacklisting
11. **Try Protected Endpoint** → Verify 401 response

---

## 🎯 Key Testing Scenarios

### Authentication Testing
- ✅ **Registration**: New user creation
- ✅ **Login**: JWT token generation
- ✅ **Token Usage**: Automatic bearer token authentication
- ✅ **Token Refresh**: Refresh expired tokens
- ✅ **Logout**: Token blacklisting verification

### API Functionality Testing
- ✅ **CRUD Operations**: Create, Read, Update, Delete tasks
- ✅ **File Uploads**: Attachment system testing
- ✅ **Analytics**: Cached endpoint responses
- ✅ **Error Handling**: Invalid requests and responses

### Security Testing
- ✅ **Rate Limiting**: Test endpoint limits
- ✅ **Authorization**: Role-based access control
- ✅ **Token Validation**: Expired/invalid token handling
- ✅ **CORS**: Cross-origin request handling

---

## 🔧 Environment Variables

The collection uses these environment variables:

| Variable | Description | Auto-Set |
|----------|-------------|----------|
| `base_url` | API base URL | Manual |
| `access_token` | JWT access token | ✅ Auto |
| `refresh_token` | JWT refresh token | ✅ Auto |
| `task_id` | Task ID for testing | Manual |
| `user_id` | User ID for testing | Manual |

---

## 🧪 Advanced Testing Features

### Pre-request Scripts
- **Auto-token refresh** when tokens expire
- **Dynamic data generation** for unique test data

### Test Scripts
- **Automatic token extraction** from login response
- **Response validation** for expected status codes
- **Environment variable updates** for chained requests

### Collection Variables
- **Shared configuration** across all requests
- **Dynamic base URL** for different environments
- **Token management** for authentication flow

---

## 🚨 Rate Limiting Testing

### Test Rate Limits:
1. **Auth Endpoints**: 100 requests/hour
2. **API Endpoints**: 1000 requests/hour
3. **Admin Endpoints**: 500 requests/hour

### How to Test:
1. Run multiple requests quickly
2. Watch for `429 Too Many Requests` response
3. Check `X-RateLimit-Remaining` header

---

## 📊 Response Validation

### Expected Status Codes:
- **200**: Successful GET/PUT requests
- **201**: Successful POST requests (creation)
- **204**: Successful DELETE requests
- **400**: Bad request (validation errors)
- **401**: Unauthorized (missing/invalid token)
- **403**: Forbidden (insufficient permissions)
- **404**: Not found
- **429**: Rate limit exceeded

### Response Headers to Check:
- `Content-Type: application/json`
- `X-RateLimit-Remaining: <number>`
- `Cache-Control: <cache-settings>`

---

## 🔄 Testing Different Environments

### Local Development
```json
{
  "base_url": "http://localhost:8081"
}
```

### Docker Environment
```json
{
  "base_url": "http://localhost:8080"
}
```

### Production Environment
```json
{
  "base_url": "https://your-production-domain.com"
}
```

---

## 🐛 Troubleshooting

### Common Issues:

1. **401 Unauthorized**
   - Check if you're logged in
   - Verify token is saved in environment
   - Try refreshing the token

2. **404 Not Found**
   - Verify the base URL is correct
   - Check if the application is running
   - Ensure endpoint paths are correct

3. **429 Rate Limited**
   - Wait for rate limit to reset
   - Check rate limit headers
   - Use different endpoints

4. **500 Internal Server Error**
   - Check application logs
   - Verify request body format
   - Check database connectivity

---

## 📈 Performance Testing

### Load Testing with Postman:
1. Use **Collection Runner**
2. Set **iterations** and **delay**
3. Monitor **response times**
4. Check **error rates**

### Metrics to Monitor:
- **Response Time**: < 200ms for most endpoints
- **Success Rate**: > 99%
- **Rate Limit Headers**: Proper counting
- **Cache Hit Rate**: For analytics endpoints

---

## 🎉 Success Criteria

### ✅ All Tests Should Pass:
- [ ] User registration successful
- [ ] Login returns valid JWT tokens
- [ ] Protected endpoints accessible with token
- [ ] CRUD operations work correctly
- [ ] File uploads function properly
- [ ] Analytics endpoints return cached data
- [ ] Rate limiting enforced correctly
- [ ] Token blacklisting works on logout
- [ ] Error responses are properly formatted
- [ ] Health checks return positive status

---

## 📞 Support

If you encounter issues:
1. Check the application logs
2. Verify environment variables
3. Test with Swagger UI for comparison
4. Review the API documentation

---

**🎯 Happy Testing! Your TaskAPI is ready for comprehensive testing with Postman!**
