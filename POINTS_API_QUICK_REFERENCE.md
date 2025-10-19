# TripFluencer Points API - Quick Reference

## Base URL
All endpoints require authentication: `Authorization: Bearer {token}`

---

## Point Settings APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/auth/points/settings` | Get all point tier settings |
| GET | `/admin/auth/points/settings/{tierName}` | Get specific tier settings |
| PUT | `/admin/auth/points/settings/{tierName}` | Update tier settings |
| PUT | `/admin/auth/points/settings/bulk` | Bulk update all tiers |
| POST | `/admin/auth/points/settings/initialize` | Initialize default settings |

---

## TripFluencer Points Management APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/auth/tripfluencer-points` | Get all TripFluencers with points |
| GET | `/admin/auth/tripfluencer-points/user/{userId}` | Get points for specific user |
| POST | `/admin/auth/tripfluencer-points/add` | Add points to user (admin) |
| POST | `/admin/auth/tripfluencer-points/deduct` | Deduct points from user (admin) |
| GET | `/admin/auth/tripfluencer-points/top-earners` | Get top 10 point earners |
| GET | `/admin/auth/tripfluencer-points/statistics` | Get points statistics |
| PATCH | `/admin/auth/tripfluencer-points/user/{userId}/toggle-active` | Toggle user active status |

---

## Redemption APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/admin/auth/redemptions` | Get all redemptions |
| GET | `/admin/auth/redemptions/user/{userId}` | Get user's redemption history |
| GET | `/admin/auth/redemptions/status/{status}` | Get redemptions by status |
| POST | `/admin/auth/redemptions` | Redeem points for discount |
| PATCH | `/admin/auth/redemptions/{redemptionId}/cancel` | Cancel redemption & refund |
| GET | `/admin/auth/redemptions/statistics` | Get redemption statistics |
| POST | `/admin/auth/redemptions/expire-old` | Expire old redemptions |

---

## Common Request Bodies

### Add Points
```json
{
  "userId": 123,
  "points": 50,
  "reason": "Bonus reward"
}
```

### Deduct Points
```json
{
  "userId": 123,
  "points": 25,
  "reason": "Penalty"
}
```

### Update Point Settings
```json
{
  "tierName": "tier1",
  "pointsPerMilestone": 15
}
```

### Redeem Points
```json
{
  "userId": 123,
  "pointsToRedeem": 50,
  "subscriptionType": "MONTHLY_PREMIUM"
}
```

---

## Response Examples

### Success Response
```json
{
  "message": "Points added successfully",
  "pointsAdded": 50,
  "tripFluencer": {
    "id": 1,
    "userId": 123,
    "currentPoints": 900,
    "totalPointsEarned": 2500
  }
}
```

### Error Response
```json
{
  "error": "Insufficient points",
  "status": 400
}
```

---

## Point Calculation Logic

| Likes Range | Points Awarded |
|-------------|----------------|
| 0 - 1,000 | 10 points per 100 likes |
| 1,001 - 10,000 | 20 points |
| 10,001 - 100,000 | 30 points |
| 100,001 - 500,000 | 40 points |
| 500,001 - 1,000,000 | 50 points |

---

## Redemption Logic

- **1 Point = 1% Discount**
- **Maximum: 100 points = 100% discount (FREE)**
- **Monthly Premium Price: Rs 2,500**

### Examples:
- 25 points → 25% OFF → Rs 1,875
- 50 points → 50% OFF → Rs 1,250
- 100 points → 100% OFF → FREE

---

## User Tiers (Based on Followers)

| Tier | Follower Range |
|------|----------------|
| SILVER | 1,000 - 14,999 |
| GOLD | 15,000 - 24,999 |
| PLATINUM | 25,000+ |

---

## Status Values

### Redemption Status
- `ACTIVE` - Currently active subscription
- `EXPIRED` - Subscription period ended
- `CANCELLED` - Manually cancelled by admin

---

## Frontend Integration

### Fetch All TripFluencers
```javascript
const response = await fetch('/admin/auth/tripfluencer-points', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
const data = await response.json();
```

### Add Points
```javascript
const response = await fetch('/admin/auth/tripfluencer-points/add', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    userId: 123,
    points: 50,
    reason: 'Bonus reward'
  })
});
```

### Get Statistics
```javascript
const statsResponse = await fetch('/admin/auth/tripfluencer-points/statistics', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
const stats = await statsResponse.json();
```

---

## Testing with cURL

### Get All TripFluencers
```bash
curl -X GET http://localhost:8080/admin/auth/tripfluencer-points \
  -H "Authorization: Bearer YOUR_TOKEN"
```

### Add Points
```bash
curl -X POST http://localhost:8080/admin/auth/tripfluencer-points/add \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"userId": 123, "points": 50, "reason": "Bonus"}'
```

### Get Redemptions
```bash
curl -X GET http://localhost:8080/admin/auth/redemptions \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Notes

1. All endpoints require admin authentication
2. Token must be included in Authorization header
3. Point operations are transactional (auto-rollback on error)
4. User details are fetched from User Service if available
5. Database tables are auto-created on first run
6. Default point settings are initialized automatically