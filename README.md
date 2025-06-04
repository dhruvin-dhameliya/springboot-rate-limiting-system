# API Documentation with Rate Limiting

This document provides information about all API endpoints in the application, including their rate limiting configurations. Use this for testing the API with consideration of rate limits.

## Table of Contents
1. [Rate Limiting Types](#rate-limiting-types)
2. [Common Headers](#common-headers)
3. [Rate Limit Test Controllers](#rate-limit-test-controllers)
4. [Aggregated Rate Limit Controller](#aggregated-rate-limit-controller)
7. [IP Filter Controller](#ip-filter-controller)
8. [IP Filter Test Controller](#ip-filter-test-controller)
9. [Testing Flow For IP Whitelist and Blacklist](#testing-flow-for-ip-whitelist-and-blacklist)

## Rate Limiting Types

The application supports various types of rate limiting:

- **ENDPOINT_BASED**: Limits requests based on the specific endpoint
- **IP_BASED**: Limits requests based on the client IP address
- **USER_BASED**: Limits requests based on the authenticated user
- **API_KEY_BASED**: Limits requests based on the API key in the header
- **METHOD_BASED**: Different rate limits for different HTTP methods

## Common Headers

- `X-API-Key`: Required for API key-based rate-limited endpoints
- `Authorization`: Required for user-based rate-limited endpoints (format: Bearer token)

## Rate Limit Test Controllers

### Purpose
This controller demonstrates various rate limiting strategies with different configurations and types.

### API Endpoints

| Endpoint | Method | Description | Rate Limit |
|----------|--------|-------------|------------|
| `/api/v1/default` | GET | Tests default global rate limiting | Global config (50 req/min) |
| `/api/v1/endpoint-specific` | GET | Tests endpoint-specific rate limiting | 5 req/min |
| `/api/v1/ip-based` | GET | Tests IP-based rate limiting | 3 req/min |
| `/api/v1/user-based` | GET | Tests user-based rate limiting | 2 req/min |
| `/api/v1/api-key-based` | GET | Tests API key-based rate limiting | 10 req/min |
| `/api/v1/method-specific` | GET | Tests GET method rate limiting | 5 req/min |
| `/api/v1/method-specific` | POST | Tests POST method rate limiting | 3 req/min |
| `/api/v1/method-specific` | PUT | Tests PUT method rate limiting | 2 req/min |
| `/api/v1/method-specific` | DELETE | Tests DELETE method rate limiting | 1 req/min |
| `/api/v1/ddos-protection` | GET | Tests DDoS protection | 5 req/min + DDoS protection |
| `/api/v1/generate-api-key` | POST | Generates a test API key | No specific limit |
| `/api/v1/revoke-api-key` | POST | Revokes a test API key | No specific limit |

### Testing
- To test default rate limiting, make more than 50 requests in a minute to `/api/v1/default`
- To test endpoint-specific rate limiting, make more than 5 requests in a minute to `/api/v1/endpoint-specific`
- To test IP-based rate limiting, make more than 3 requests in a minute to `/api/v1/ip-based`
- For user-based rate limiting, send requests with Basic Auth (testuser:password) to `/api/v1/user-based`
- For API key-based, first call `/api/v1/generate-api-key` to get a key, then send it in X-API-Key header
- For method-specific limits, test different HTTP methods against the same endpoint
- For DDoS protection, rapidly send 10+ requests to `/api/v1/ddos-protection`

## Aggregated Rate Limit Controller

### Purpose
This controller demonstrates class-level rate limiting where multiple endpoints share the same limit.

### API Endpoints

| Endpoint | Method | Description | Rate Limit |
|----------|--------|-------------|------------|
| `/api/aggregated/endpoint1` | GET | Uses class-level rate limiting | 20 req/min (shared) |
| `/api/aggregated/endpoint2` | GET | Uses class-level rate limiting | 20 req/min (shared) |
| `/api/aggregated/override` | GET | Overrides the class-level rate limit | 3 req/min (specific) |

### Testing
- Make requests to both `endpoint1` and `endpoint2` - they share the same counter (20 req/min total)
- After hitting the limit on one endpoint, try the other - it should also be limited
- Test `override` endpoint separately as it has its own 3 req/min limit

## IP Filter Controller

### Purpose
This controller manages IP whitelist and blacklist functionality for bypassing or blocking specific IPs.

### API Endpoints

| Endpoint | Method | Description | Rate Limit |
|----------|--------|-------------|------------|
| `/api/ip-filter/whitelist` | GET | Get all whitelisted IPs | No specific limit |
| `/api/ip-filter/blacklist` | GET | Get all blacklisted IPs | No specific limit |
| `/api/ip-filter/whitelist/{ipAddress}` | POST | Add IP to whitelist | IP-based (10 req/min) |
| `/api/ip-filter/whitelist/{ipAddress}` | DELETE | Remove IP from whitelist | IP-based (10 req/min) |
| `/api/ip-filter/blacklist/{ipAddress}` | POST | Add IP to blacklist | IP-based (10 req/min) |
| `/api/ip-filter/blacklist/{ipAddress}` | DELETE | Remove IP from blacklist | IP-based (10 req/min) |
| `/api/ip-filter/status/{ipAddress}` | GET | Check IP status | No specific limit |

### Testing
- View current whitelisted IPs with GET to `/api/ip-filter/whitelist`
- View current blacklisted IPs with GET to `/api/ip-filter/blacklist`
- Add IP to whitelist: POST to `/api/ip-filter/whitelist/192.168.1.123`
- Remove IP from whitelist: DELETE to `/api/ip-filter/whitelist/192.168.1.123`
- Add IP to blacklist: POST to `/api/ip-filter/blacklist/192.168.1.123`
- Remove IP from blacklist: DELETE to `/api/ip-filter/blacklist/192.168.1.123`
- Check IP status: GET to `/api/ip-filter/status/192.168.1.123`

## IP Filter Test Controller

### Purpose
This controller provides test endpoints specifically for demonstrating IP whitelisting and blacklisting.

### API Endpoints

| Endpoint | Method | Description | Rate Limit |
|----------|--------|-------------|------------|
| `/api/test/ip-filter/strict-limit` | GET | Tests strict rate limit | 1 req/min |
| `/api/test/ip-filter/blacklist-test` | GET | Tests blacklist blocking | None, but blacklisted IPs blocked |
| `/api/test/ip-filter/whitelist-me` | GET | Adds your IP to whitelist | No specific limit |
| `/api/test/ip-filter/unwhitelist-me` | GET | Removes your IP from whitelist | No specific limit |

### Testing
1. First, check your IP status with `/api/ip-filter/status/{your-ip}`
2. Test the strict 1 req/min limit by sending two quick requests to `/api/test/ip-filter/strict-limit`
3. Add your IP to the whitelist using `/api/test/ip-filter/whitelist-me`
4. Test the strict limit again - you should bypass the limit
5. Remove your IP from whitelist using `/api/test/ip-filter/unwhitelist-me`
6. For blacklist testing:
   - Try accessing `/api/test/ip-filter/blacklist-test` normally
   - Add your IP to blacklist with `/api/ip-filter/blacklist/{your-ip}`
   - Try to access any API endpoint - you should get a 403 Forbidden
   - Remove your IP from blacklist to restore access

## Testing Flow For IP Whitelist and Blacklist

### Testing Whitelist (bypassing rate limits)
1. First test the strict-limit endpoint: `/api/test/ip-filter/strict-limit`
2. Send a second request immediately - you should get a 429 Too Many Requests error
3. Add your IP to whitelist: `/api/test/ip-filter/whitelist-me`
4. Try again with multiple requests to the strict-limit endpoint - no rate limiting should apply
5. Remove your IP from whitelist: `/api/test/ip-filter/unwhitelist-me`
6. Test again to confirm rate limiting is back in effect

### Testing Blacklist (blocking access)
1. Check your IP status: `/api/ip-filter/status/{your-ip}`
2. Test access to any endpoint (e.g. `/api/test/ip-filter/blacklist-test`)
3. Add your IP to blacklist: `/api/ip-filter/blacklist/{your-ip}` (do this from another IP)
4. Attempt to access any endpoint - you should receive a 403 Forbidden response
5. Remove your IP from blacklist: `/api/ip-filter/blacklist/{your-ip}` (from another IP)
6. Confirm you can access endpoints again

Remember that blacklisted IPs are completely blocked from accessing the API, so you'll need to use another IP or device to remove your IP from the blacklist. 