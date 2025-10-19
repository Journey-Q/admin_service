# TripFluencer Points API - Permission Updates

## Summary of Changes

The following endpoints have been updated to be **publicly accessible** (no admin authentication required) to allow users to interact with their own points and redemptions.

---

## Public Endpoints (No Authentication Required)

### ✅ Point Settings APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/admin/auth/points/settings` | GET | **PUBLIC** | Get all point tier settings |
| `/admin/auth/points/settings/{tierName}` | GET | **PUBLIC** | Get specific tier settings |

**Use Case:** Users can view how many points they can earn for different like milestones.

**Example:**
```bash
# No token required
curl -X GET http://localhost:8080/admin/auth/points/settings
```

---

### ✅ TripFluencer Points APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/admin/auth/tripfluencer-points/user/{userId}` | GET | **PUBLIC** | Get points for specific user |

**Use Case:** Users can view their own point balance, total points earned, points used, and tier information.

**Example:**
```bash
# No token required
curl -X GET http://localhost:8080/admin/auth/tripfluencer-points/user/123
```

**Response:**
```json
{
  "id": 1,
  "userId": 123,
  "currentPoints": 850,
  "totalPointsEarned": 2450,
  "pointsUsed": 1600,
  "totalLikes": 127000,
  "followers": 15420,
  "tier": "GOLD",
  "isActive": true
}
```

---

### ✅ Redemption APIs

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/admin/auth/redemptions/user/{userId}` | GET | **PUBLIC** | Get user's redemption history |
| `/admin/auth/redemptions` | POST | **PUBLIC** | Redeem points for discount |

**Use Case 1: View Redemption History**
Users can see all their past and active redemptions.

**Example:**
```bash
# No token required
curl -X GET http://localhost:8080/admin/auth/redemptions/user/123
```

**Response:**
```json
{
  "redemptions": [
    {
      "id": 1,
      "userId": 123,
      "pointsUsed": 50,
      "discountPercentage": 50,
      "subscriptionType": "MONTHLY_PREMIUM",
      "originalPrice": 2500.0,
      "discountedPrice": 1250.0,
      "status": "ACTIVE",
      "redeemedAt": "2024-03-14T10:00:00",
      "expiresAt": "2024-04-14T10:00:00"
    }
  ],
  "userId": 123,
  "total": 1
}
```

**Use Case 2: Redeem Points**
Users can redeem their points for monthly premium subscription discounts.

**Example:**
```bash
# No token required
curl -X POST http://localhost:8080/admin/auth/redemptions \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 123,
    "pointsToRedeem": 50,
    "subscriptionType": "MONTHLY_PREMIUM"
  }'
```

**Response:**
```json
{
  "message": "Points redeemed successfully",
  "redemption": {
    "id": 2,
    "userId": 123,
    "pointsUsed": 50,
    "discountPercentage": 50,
    "originalPrice": 2500.0,
    "discountedPrice": 1250.0,
    "status": "ACTIVE",
    "expiresAt": "2024-04-20T10:00:00"
  }
}
```

---

## Admin-Only Endpoints (Authentication Required)

These endpoints remain **admin-only** and require `Authorization: Bearer {token}` header with admin role.

### Point Settings Management (Admin Only)

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/admin/auth/points/settings/{tierName}` | PUT | **ADMIN** | Update tier settings |
| `/admin/auth/points/settings/bulk` | PUT | **ADMIN** | Bulk update all tiers |
| `/admin/auth/points/settings/initialize` | POST | **ADMIN** | Initialize default settings |

---

### TripFluencer Management (Admin Only)

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/admin/auth/tripfluencer-points` | GET | **ADMIN** | Get all TripFluencers |
| `/admin/auth/tripfluencer-points/add` | POST | **ADMIN** | Add points to user |
| `/admin/auth/tripfluencer-points/deduct` | POST | **ADMIN** | Deduct points from user |
| `/admin/auth/tripfluencer-points/top-earners` | GET | **ADMIN** | Get top earners |
| `/admin/auth/tripfluencer-points/statistics` | GET | **ADMIN** | Get statistics |
| `/admin/auth/tripfluencer-points/user/{userId}/toggle-active` | PATCH | **ADMIN** | Toggle user active status |

---

### Redemption Management (Admin Only)

| Endpoint | Method | Access | Description |
|----------|--------|--------|-------------|
| `/admin/auth/redemptions` | GET | **ADMIN** | Get all redemptions |
| `/admin/auth/redemptions/status/{status}` | GET | **ADMIN** | Get redemptions by status |
| `/admin/auth/redemptions/{redemptionId}/cancel` | PATCH | **ADMIN** | Cancel & refund redemption |
| `/admin/auth/redemptions/statistics` | GET | **ADMIN** | Get redemption statistics |
| `/admin/auth/redemptions/expire-old` | POST | **ADMIN** | Expire old redemptions |

---

## Frontend Integration Examples

### User Dashboard - View Points Balance

```javascript
// No authentication needed for viewing own points
async function getUserPoints(userId) {
  const response = await fetch(`/admin/auth/tripfluencer-points/user/${userId}`);
  const data = await response.json();

  console.log(`Current Points: ${data.currentPoints}`);
  console.log(`Total Earned: ${data.totalPointsEarned}`);
  console.log(`Tier: ${data.tier}`);

  return data;
}
```

### User Dashboard - View Redemption History

```javascript
// No authentication needed for viewing own redemptions
async function getUserRedemptions(userId) {
  const response = await fetch(`/admin/auth/redemptions/user/${userId}`);
  const data = await response.json();

  console.log(`Total Redemptions: ${data.total}`);
  data.redemptions.forEach(r => {
    console.log(`${r.pointsUsed} points → ${r.discountPercentage}% OFF`);
  });

  return data.redemptions;
}
```

### User Dashboard - Redeem Points

```javascript
// No authentication needed for redeeming points
async function redeemPoints(userId, pointsToRedeem) {
  const response = await fetch('/admin/auth/redemptions', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      userId: userId,
      pointsToRedeem: pointsToRedeem,
      subscriptionType: 'MONTHLY_PREMIUM'
    })
  });

  const data = await response.json();

  if (response.ok) {
    console.log('Points redeemed successfully!');
    console.log(`Discount: ${data.redemption.discountPercentage}%`);
    console.log(`New Price: Rs ${data.redemption.discountedPrice}`);
  } else {
    console.error('Failed:', data.error);
  }

  return data;
}
```

### User Dashboard - View Point Tiers

```javascript
// No authentication needed for viewing point tier structure
async function getPointTiers() {
  const response = await fetch('/admin/auth/points/settings');
  const data = await response.json();

  data.settings.forEach(tier => {
    console.log(`${tier.tierName}: ${tier.minLikes}-${tier.maxLikes} likes = ${tier.pointsPerMilestone} points`);
  });

  return data.settings;
}
```

---

## Security Considerations

1. **Public Endpoints**: While these endpoints are public, users should only access their own data using their userId
2. **Future Enhancement**: Consider adding user authentication to verify the requesting user matches the userId in the request
3. **Rate Limiting**: Implement rate limiting on public endpoints to prevent abuse
4. **Input Validation**: All user inputs are validated on the server side

---

## Benefits of These Changes

### For Users:
- ✅ Can view their point balance anytime
- ✅ Can see how many points they can earn
- ✅ Can view their redemption history
- ✅ Can redeem points for discounts independently
- ✅ No need for admin intervention for basic operations

### For Admins:
- ✅ Reduced workload (users handle their own redemptions)
- ✅ Full control over point management (add/deduct/toggle)
- ✅ Access to statistics and analytics
- ✅ Ability to cancel redemptions if needed
- ✅ Control over point tier settings

---

## Migration Notes

If you're updating from the previous version:

1. **Frontend**: Update API calls to remove authentication headers for public endpoints
2. **Testing**: Test all public endpoints without authentication
3. **Documentation**: Update user-facing documentation to show self-service features
4. **Security Config**: Ensure Spring Security configuration allows these endpoints without authentication

---

## Questions?

For any issues or questions about the permission changes, refer to the main API documentation in `TRIPFLUENCER_POINTS_API.md` or check application logs for detailed error messages.