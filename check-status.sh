#!/bin/bash

echo "🚀 TaskAPI Status Check"
echo "======================="
echo ""

# Check if application is running
echo "📡 Checking application status..."
if curl -s http://localhost:8081/actuator/health > /dev/null 2>&1; then
    echo "✅ Application is running on http://localhost:8081"
    echo "✅ Health check: $(curl -s http://localhost:8081/actuator/health | jq -r '.status' 2>/dev/null || echo 'UP')"
else
    echo "❌ Application is not running on port 8081"
fi

echo ""

# Check Swagger UI
echo "📚 Checking Swagger UI..."
if curl -s http://localhost:8081/swagger-ui/index.html > /dev/null 2>&1; then
    echo "✅ Swagger UI is accessible at http://localhost:8081/swagger-ui/index.html"
else
    echo "❌ Swagger UI is not accessible"
fi

echo ""

# Check Java version
echo "☕ Java Environment:"
java -version 2>&1 | head -1

echo ""

# Test registration endpoint
echo "🔐 Testing registration endpoint..."
REGISTER_RESPONSE=$(curl -s -w "%{http_code}" -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser_'$(date +%s)'",
    "email": "test'$(date +%s)'@example.com",
    "password": "Password123!",
    "firstName": "Test",
    "lastName": "User"
  }' -o /dev/null)

if [ "$REGISTER_RESPONSE" = "201" ] || [ "$REGISTER_RESPONSE" = "200" ]; then
    echo "✅ Registration endpoint is working (HTTP $REGISTER_RESPONSE)"
else
    echo "❌ Registration endpoint failed (HTTP $REGISTER_RESPONSE)"
fi

echo ""

# Summary
echo "📋 Quick Test Instructions:"
echo "1. Open: http://localhost:8081/swagger-ui/index.html"
echo "2. Register a user with POST /api/v1/auth/register"
echo "3. Login with POST /api/v1/auth/login"
echo "4. Copy the accessToken from the response"
echo "5. Click 'Authorize' button and enter: Bearer <your_token>"
echo "6. Test any protected endpoint like GET /api/v1/tasks"
echo "7. Logout with POST /api/v1/auth/logout to test token blacklisting"

echo ""
echo "🎉 TaskAPI is ready for testing!"
