# Backend Server Integration Guide

## Overview

This guide explains how to set up your backend server to work with the QDS AI Agent Android app.

## Minimum Requirements

Your backend server must have:
1. **Running on a network-accessible IP and port**
2. **Health check endpoint**: `GET /api/health`
3. **CORS enabled** (if running on different domain)
4. **Proper HTTP headers**

## Endpoint Specification

### Health Check Endpoint

**URL Format:**
```
GET http://{ip}:{port}/api/health
```

**Example:**
```
GET http://192.168.1.65:8080/api/health
```

**Expected Response:**
- **HTTP Status**: 200 (OK)
- **Content-Type**: application/json
- **Body**: Any valid response (app only checks status code)

**Example Response:**
```json
{
  "status": "ok",
  "timestamp": "2025-12-24T10:30:00Z",
  "uptime": 3600
}
```

**Minimal Response:**
```json
{}
```

## Implementation Examples

### Node.js / Express

```javascript
const express = require('express');
const cors = require('cors');
const app = express();

// Enable CORS
app.use(cors());

// Health check endpoint
app.get('/api/health', (req, res) => {
  res.json({
    status: 'ok',
    timestamp: new Date().toISOString(),
    service: 'QDS AI Agent Backend'
  });
});

// Start server
app.listen(8080, '0.0.0.0', () => {
  console.log('Server running on http://0.0.0.0:8080');
});
```

### Python / Flask

```python
from flask import Flask, jsonify
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

@app.route('/api/health', methods=['GET'])
def health_check():
    return jsonify({
        'status': 'ok',
        'timestamp': datetime.now().isoformat(),
        'service': 'QDS AI Agent Backend'
    }), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080, debug=True)
```

### Python / FastAPI

```python
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from datetime import datetime

app = FastAPI()

# Enable CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

@app.get('/api/health')
async def health_check():
    return {
        'status': 'ok',
        'timestamp': datetime.now().isoformat(),
        'service': 'QDS AI Agent Backend'
    }

# Run: uvicorn main:app --host 0.0.0.0 --port 8080
```

### Java / Spring Boot

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class QDSApplication {
    public static void main(String[] args) {
        SpringApplication.run(QDSApplication.class, args);
    }
}

@RestController
public class HealthController {
    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "ok",
            "timestamp", System.currentTimeMillis() + "",
            "service", "QDS AI Agent Backend"
        );
    }
}
```

### Go / Gin

```go
package main

import (
    "github.com/gin-gonic/gin"
    "time"
)

func main() {
    router := gin.Default()

    // CORS enabled by default in basic setup
    router.GET("/api/health", func(c *gin.Context) {
        c.JSON(200, gin.H{
            "status":    "ok",
            "timestamp": time.Now().String(),
            "service":   "QDS AI Agent Backend",
        })
    })

    router.Run(":8080")
}
```

### C# / ASP.NET Core

```csharp
using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;

var builder = WebApplication.CreateBuilder(args);

// Add CORS
builder.Services.AddCors(options => {
    options.AddDefaultPolicy(policy => {
        policy.AllowAnyOrigin()
              .AllowAnyMethod()
              .AllowAnyHeader();
    });
});

var app = builder.Build();
app.UseCors();

// Health check endpoint
app.MapGet("/api/health", () => new {
    status = "ok",
    timestamp = DateTime.UtcNow,
    service = "QDS AI Agent Backend"
});

app.Run("http://0.0.0.0:8080");
```

## Testing the Endpoint

### Using cURL

```bash
# Test health endpoint
curl -X GET http://192.168.1.65:8080/api/health

# With headers
curl -X GET \
  -H "Content-Type: application/json" \
  http://192.168.1.65:8080/api/health

# Verbose output
curl -v http://192.168.1.65:8080/api/health
```

### Using PowerShell

```powershell
# Simple test
Invoke-WebRequest -Uri "http://192.168.1.65:8080/api/health" -Method Get

# With JSON response
$response = Invoke-WebRequest -Uri "http://192.168.1.65:8080/api/health" -Method Get
$response.Content | ConvertFrom-Json
```

### Using Python

```python
import requests
import json

response = requests.get('http://192.168.1.65:8080/api/health')
print(f"Status Code: {response.status_code}")
print(f"Response: {json.dumps(response.json(), indent=2)}")
```

### Using Android Device

The QDS AI Agent app will automatically test the endpoint when:
1. You enter the IP:Port in Settings
2. You send a message

## Network Configuration

### Firewall Rules

Make sure your firewall allows traffic on the port:

**Windows Firewall:**
```powershell
# Allow port 8080
netsh advfirewall firewall add rule name="QDS AI Agent" dir=in action=allow protocol=tcp localport=8080
```

**Linux (ufw):**
```bash
sudo ufw allow 8080/tcp
```

**Linux (iptables):**
```bash
sudo iptables -A INPUT -p tcp --dport 8080 -j ACCEPT
```

### IP Binding

Make sure your server binds to all interfaces:

```
Listen on: 0.0.0.0:8080
NOT: 127.0.0.1:8080 (localhost only)
```

## Common Issues & Solutions

### Issue: "Connection refused"
**Cause**: Server not running or wrong port
**Solution**: 
- Start server
- Check port in Settings
- Verify firewall allows traffic

### Issue: "Connection timeout"
**Cause**: Server not reachable at IP:Port
**Solution**:
- Verify IP address (ping test)
- Check if on same network
- Check firewall rules

### Issue: "HTTP 404 Not Found"
**Cause**: `/api/health` endpoint doesn't exist
**Solution**:
- Implement the `/api/health` endpoint
- Check endpoint path is exactly `/api/health`
- Verify HTTP method is GET

### Issue: "HTTP 500 Server Error"
**Cause**: Server error in endpoint code
**Solution**:
- Check server logs
- Debug the endpoint implementation
- Handle exceptions properly

### Issue: "CORS Error" (in web clients)
**Cause**: CORS not enabled (only affects web, not Android app)
**Solution**:
- Enable CORS middleware
- Add proper CORS headers

## Expected Behavior

When user sends message in app:

```
User Types: "Hello"
     ↓
Click Send
     ↓
App sends: GET /api/health
     ↓
Server responds: 200 OK
     ↓
App displays: "✓ Connected: Hello"
```

If server doesn't respond:

```
App sends: GET /api/health
     ↓
Server doesn't respond / 500 error
     ↓
App displays: "✗ Connection failed"
```

## Performance Recommendations

1. **Response Time**: < 1 second (app timeout is 10 seconds)
2. **Keep-Alive**: Use HTTP keep-alive connections
3. **Compression**: Optional but recommended for large responses
4. **Caching**: Health endpoint should not cache responses

## Security Considerations

1. **HTTPS**: Consider upgrading to HTTPS for production
2. **Authentication**: Add authentication if needed (future version)
3. **Rate Limiting**: Implement rate limiting if needed
4. **Logging**: Log health checks for monitoring

## Production Deployment

### Docker Example

```dockerfile
FROM node:18-alpine
WORKDIR /app
COPY package.json .
RUN npm install
COPY . .
EXPOSE 8080
CMD ["node", "server.js"]
```

### Environment Variables

```bash
SERVER_IP=192.168.1.65
SERVER_PORT=8080
NODE_ENV=production
```

## Monitoring

Monitor your health endpoint:

```bash
# Every 10 seconds
watch -n 10 'curl -s http://192.168.1.65:8080/api/health | jq'
```

## Future Endpoints

For future versions, you may want to implement:

```
POST /api/messages      # Send messages to server
GET  /api/messages      # Get message history
POST /api/auth/login    # User authentication
POST /api/chat          # AI chat endpoint
GET  /api/status        # Server status
```

But for now, only `/api/health` is required.

## Debugging Checklist

- [ ] Server is running
- [ ] Server binds to 0.0.0.0 (all interfaces)
- [ ] Port is correct (8080 or custom)
- [ ] Firewall allows the port
- [ ] /api/health endpoint exists
- [ ] Endpoint responds with 200 OK
- [ ] Both devices on same network
- [ ] IP address is correct (not localhost)
- [ ] cURL/Postman can reach the endpoint
- [ ] CORS is enabled (if needed)

## Quick Setup Test

```bash
# Start simple Node.js server
node -e "require('http').createServer((req, res) => { res.writeHead(200); res.end(JSON.stringify({status:'ok'})); }).listen(8080);"

# In another terminal, test it
curl http://localhost:8080
# Should show: {"status":"ok"}

# Get your IP
ipconfig getifaddr en0  # macOS
hostname -I              # Linux
```

Then enter this IP:8080 in the QDS AI Agent Settings.

## Support

For implementation help:
1. Check the code examples above for your framework
2. Verify all requirements are met
3. Test endpoint with cURL first
4. Check server logs
5. Run debugging checklist

---

**Your backend server is now ready for QDS AI Agent integration!**
