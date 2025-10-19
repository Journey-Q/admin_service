# TripFluencer Rewards Points System API Documentation

## Overview
This API manages the TripFluencer rewards points system where travelers with 1,000+ followers earn points through engagement (likes) and can redeem them for monthly premium subscription discounts.

## Core Concepts

### Point Tiers
Points are awarded based on likes received in different tiers:
- **Tier 1**: 0-1,000 likes → 10 points per 100 likes
- **Tier 2**: 1,001-10,000 likes → 20 points
- **Tier 3**: 10,001-100,000 likes → 30 points
- **Tier 4**: 100,001-500,000 likes → 40 points
- **Tier 5**: 500,001-1,000,000 likes → 50 points

### Redemption
- 1 point = 1% discount on monthly premium subscription
- Monthly Premium Price: Rs 2,500
- Maximum discount: 100% (100 points = FREE subscription)

### User Tiers (Based on Followers)
- **Silver**: 1,000 - 14,999 followers
- **Gold**: 15,000 - 24,999 followers
- **Platinum**: 25,000+ followers

---

## API Endpoints

### Point Settings Management

#### 1. Get All Point Settings
```http
GET /admin/auth/points/settings
Authorization: Bearer {token}
```

**Response:**
```json
{
  "settings": [
    {
      "id": 1,
      "tierName": "tier1",
      "minLikes": 0,
      "maxLikes": 1000,
      "pointsPerMilestone": 10,
      "isActive": true
    },
    ...
  ],
  "total": 5
}
```

#### 2. Get Point Settings by Tier
```http
GET /admin/auth/points/settings/{tierName}
Authorization: Bearer {token}
```

**Example:** `GET /admin/auth/points/settings/tier1`

#### 3. Update Point Settings
```http
PUT /admin/auth/points/settings/{tierName}
Authorization: Bearer {token}
Content-Type: application/json

{
  "tierName": "tier1",
  "pointsPerMilestone": 15
}
```

#### 4. Bulk Update Point Settings
```http
PUT /admin/auth/points/settings/bulk
Authorization: Bearer {token}
Content-Type: application/json

[
  {
    "tierName": "tier1",
    "pointsPerMilestone": 10
  },
  {
    "tierName": "tier2",
    "pointsPerMilestone": 20
  }
]
```

#### 5. Initialize Default Settings
```http
POST /admin/auth/points/settings/initialize
Authorization: Bearer {token}
```

---

### TripFluencer Points Management

#### 1. Get All TripFluencers
```http
GET /admin/auth/tripfluencer-points
Authorization: Bearer {token}
```

**Response:**
```json
{
  "tripFluencers": [
    {
      "id": 1,
      "userId": 123,
      "userName": "Sarah Johnson",
      "userEmail": "sarah.j@email.com",
      "profileImage": "https://...",
      "currentPoints": 850,
      "totalPointsEarned": 2450,
      "pointsUsed": 1600,
      "totalLikes": 127000,
      "followers": 15420,
      "tier": "GOLD",
      "isActive": true,
      "createdAt": "2024-01-15T10:00:00",
      "updatedAt": "2024-03-20T15:30:00"
    }
  ],
  "total": 1248
}
```

#### 2. Get User Points
```http
GET /admin/auth/tripfluencer-points/user/{userId}
Authorization: Bearer {token}
```

**Example:** `GET /admin/auth/tripfluencer-points/user/123`

#### 3. Add Points (Admin Action)
```http
POST /admin/auth/tripfluencer-points/add
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 123,
  "points": 50,
  "reason": "Bonus for exceptional content"
}
```

**Response:**
```json
{
  "message": "Points added successfully",
  "pointsAdded": 50,
  "tripFluencer": { ... }
}
```

#### 4. Deduct Points (Admin Action)
```http
POST /admin/auth/tripfluencer-points/deduct
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 123,
  "points": 25,
  "reason": "Policy violation penalty"
}
```

**Response:**
```json
{
  "message": "Points deducted successfully",
  "pointsDeducted": 25,
  "tripFluencer": { ... }
}
```

#### 5. Get Top Earners
```http
GET /admin/auth/tripfluencer-points/top-earners
Authorization: Bearer {token}
```

Returns top 10 TripFluencers by total points earned.

#### 6. Get Points Statistics
```http
GET /admin/auth/tripfluencer-points/statistics
Authorization: Bearer {token}
```

**Response:**
```json
{
  "activeTripFluencers": 1248,
  "totalPointsEarned": 2400000,
  "totalPointsUsed": 1200000,
  "activeSubscriptions": 567,
  "averagePointsPerUser": 1923
}
```

#### 7. Toggle TripFluencer Active Status
```http
PATCH /admin/auth/tripfluencer-points/user/{userId}/toggle-active
Authorization: Bearer {token}
```

---

### Point Redemption Management

#### 1. Get All Redemptions
```http
GET /admin/auth/redemptions
Authorization: Bearer {token}
```

**Response:**
```json
{
  "redemptions": [
    {
      "id": 1,
      "userId": 123,
      "userName": "Sarah Johnson",
      "userEmail": "sarah.j@email.com",
      "pointsUsed": 50,
      "discountPercentage": 50,
      "subscriptionType": "MONTHLY_PREMIUM",
      "originalPrice": 2500.0,
      "discountedPrice": 1250.0,
      "subscriptionId": 456,
      "status": "ACTIVE",
      "redeemedAt": "2024-03-14T10:00:00",
      "expiresAt": "2024-04-14T10:00:00"
    }
  ],
  "total": 1567
}
```

#### 2. Get Redemptions by User
```http
GET /admin/auth/redemptions/user/{userId}
Authorization: Bearer {token}
```

#### 3. Get Redemptions by Status
```http
GET /admin/auth/redemptions/status/{status}
Authorization: Bearer {token}
```

**Status values:** `ACTIVE`, `EXPIRED`, `CANCELLED`

**Example:** `GET /admin/auth/redemptions/status/ACTIVE`

#### 4. Redeem Points
```http
POST /admin/auth/redemptions
Authorization: Bearer {token}
Content-Type: application/json

{
  "userId": 123,
  "pointsToRedeem": 50,
  "subscriptionType": "MONTHLY_PREMIUM"
}
```

**Response:**
```json
{
  "message": "Points redeemed successfully",
  "redemption": { ... }
}
```

#### 5. Cancel Redemption
```http
PATCH /admin/auth/redemptions/{redemptionId}/cancel
Authorization: Bearer {token}
```

This will refund the points to the user's account.

#### 6. Get Redemption Statistics
```http
GET /admin/auth/redemptions/statistics
Authorization: Bearer {token}
```

**Response:**
```json
{
  "activeRedemptions": 567,
  "totalPointsRedeemed": 45000
}
```

#### 7. Expire Old Redemptions (Manual Trigger)
```http
POST /admin/auth/redemptions/expire-old
Authorization: Bearer {token}
```

---

## Database Schema

### Table: `point_settings`
```sql
CREATE TABLE point_settings (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  tier_name VARCHAR(50) UNIQUE NOT NULL,
  min_likes INT NOT NULL,
  max_likes INT NOT NULL,
  points_per_milestone INT NOT NULL,
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

### Table: `tripfluencer_points`
```sql
CREATE TABLE tripfluencer_points (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNIQUE NOT NULL,
  current_points INT DEFAULT 0,
  total_points_earned INT DEFAULT 0,
  points_used INT DEFAULT 0,
  total_likes INT DEFAULT 0,
  followers INT DEFAULT 0,
  tier VARCHAR(20) DEFAULT 'SILVER',
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP,
  updated_at TIMESTAMP
);
```

### Table: `point_redemptions`
```sql
CREATE TABLE point_redemptions (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  points_used INT NOT NULL,
  discount_percentage INT NOT NULL,
  subscription_type VARCHAR(50) DEFAULT 'MONTHLY_PREMIUM',
  original_price DECIMAL(10,2) NOT NULL,
  discounted_price DECIMAL(10,2) NOT NULL,
  subscription_id BIGINT,
  status VARCHAR(20) DEFAULT 'ACTIVE',
  redeemed_at TIMESTAMP,
  expires_at TIMESTAMP
);
```

---

## Error Responses

### 400 Bad Request
```json
{
  "error": "Insufficient points. User has: 25, trying to redeem: 50",
  "status": 400
}
```

### 404 Not Found
```json
{
  "error": "TripFluencer not found for user: 123",
  "status": 404
}
```

### 500 Internal Server Error
```json
{
  "error": "Failed to fetch TripFluencers: Database connection error",
  "status": 500
}
```

---

## How It Works

### 1. User Qualification
- User must have 1,000+ followers to become a TripFluencer
- Admin can create/update TripFluencer records through the API

### 2. Earning Points
- When a user's post receives likes, points are awarded based on the tier system
- Points are automatically calculated using the `calculatePointsFromLikes` method
- Example: 5,000 likes = 20 points (Tier 2)

### 3. Redeeming Points
- User can redeem points for monthly premium subscription discount
- 1 point = 1% discount
- Maximum 100 points can be redeemed (100% discount = FREE)
- Points are immediately deducted upon redemption

### 4. Point Management (Admin)
- Admins can manually add/deduct points
- Admins can view all TripFluencers and their point balances
- Admins can configure point tier settings
- Admins can view redemption history

---

## Integration Notes

### User Service Integration
The system fetches user details (name, email, profile image) from the User Service. Configure the URL in your application properties:

```properties
user.service.url=http://localhost:8081/api/users
```

### Subscription Service Integration
When points are redeemed, the system can optionally integrate with a Subscription Service to create the actual subscription. The `subscriptionId` field stores the reference.

---

## Best Practices

1. **Initialize Settings First**: Call `/points/settings/initialize` when setting up the system for the first time
2. **Regular Monitoring**: Use statistics endpoints to monitor system health
3. **Expire Old Redemptions**: Set up a scheduled job to call `/redemptions/expire-old` daily
4. **Validate User**: Always verify user exists in User Service before operations
5. **Audit Logging**: All admin actions are logged with admin email and timestamp

---

## Future Enhancements

- Automated point awarding through event listeners
- Scheduled jobs for automatic redemption expiration
- Point history tracking (audit trail)
- Point expiration policy
- Tiered redemption rates (different rates for different user tiers)
- Integration with notification service for point updates
- Export functionality for reporting

---

## Support

For issues or questions about the Points API, contact the development team or check the application logs for detailed error messages.