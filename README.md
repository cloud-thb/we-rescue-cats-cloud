[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=cloud-thb_we-rescue-cats-cloud&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=cloud-thb_we-rescue-cats-cloud)

# We Rescue Cats 🐱🐾

A comprehensive web portal for cat rescue organizations to manage cat breeds information, adoptable cats, and the adoption process.

## Tech Stack

### Frontend
- **Angular 20.1.0** with TypeScript
- **Angular Material** for UI components
- **Leaflet** for interactive maps
- **RxJS** for reactive programming

### Backend
- **Java 21** with **Spring Boot 3.5.4**
- **Spring Security** with JWT authentication
- **Spring Data JPA** for database operations
- **Spring Validation** for input validation
- **Lombok** for reducing boilerplate code

### Database
- **MySQL** for data persistence
- **JPA/Hibernate** for ORM mapping

## Features

### Cat Breeds Management
- Browse all cat breeds with detailed information
- Filter breeds by attributes (child-friendly, dog-friendly, energy level, origin)
- View breed characteristics and compatibility information
- Integration with The Cat API for breed data and images

### Adoptable Cats
- View all available cats for adoption
- Interactive map showing cat locations
- Filter cats by breed, age, and gender
- Detailed cat profiles with photos and descriptions

### Adoption System
- User registration and authentication
- Adoption application form with validation
- Admin panel for managing adoption requests
- Multi-status adoption workflow (Pending → Approved → Completed)
- User adoption history and statistics

### Admin Features
- Dashboard with comprehensive statistics
- User management system
- Cat status management
- Adoption request processing
- Anonymous data export for health organizations

### API for Health Organizations
- API token-based access
- Anonymous adoption data export

## Project Structure

```
we-rescue-cats/
├── frontend/                           # Angular application
│   ├── src/
│   ├── package.json
│   └── angular.json
├── backend/                            # Spring Boot application
│   ├── src/main/java/
│   ├── src/main/resources/
│   │           ├ application.properties
│   │           └ data.sql
│   └── pom.xml
└── README.md
```

## Prerequisites

- **Node.js** (v18 or higher)
- **Angular CLI** (v18 or higher)
- **Java 21**
- **Maven 3.6+**
- **MySQL 8.0+**

## Installation & Setup

### 1. Database Setup

Create a MySQL database:

```sql
CREATE DATABASE we_rescue_cats;
CREATE USER 'rescue_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON we_rescue_cats.* TO 'rescue_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Backend Setup

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Configure database connection in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/we_rescue_cats
   spring.datasource.username=rescue_user
   spring.datasource.password=your_password
   ```

3. Build and run the Spring Boot application:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

The backend will start on `http://localhost:8080` and automatically:
- Create database tables using JPA annotations
- Populate initial data (users, cats, breeds, adoptions)
- Load API tokens for health organizations from `data.sql`

### 3. Frontend Setup

1. Navigate to the frontend directory:
   ```bash
   cd frontend
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
   ```bash
   ng serve
   ```

The frontend will be available at `http://localhost:4200`

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Cat Breeds
- `GET /api/breeds` - Get all breeds
- `GET /api/breeds/{id}` - Get breed by ID
- `GET /api/breeds/search` - Search breeds with filters

### Cats
- `GET /api/cats` - Get all cats
- `GET /api/cats/available` - Get available cats
- `GET /api/cats/area` - Get cats in geographical area
- `GET /api/cats/{id}` - Get cat by ID
- `GET /api/cats/breed/{breedId}` - Get cats by breed


### Adoptions
- `POST /api/adoptions` - Create adoption request
- `GET /api/adoptions/user/{userId}` - Get user's adoptions
- `GET /api/adoptions/pending` - Get pending adoptions (Admin)
- `PUT /api/adoptions/{id}/approve` - Approve adoption (Admin)
- `PUT /api/adoptions/{id}/complete` - Complete adoption (Admin)
- `PUT /api/adoptions/{id}/reject` - Reject adoption (Admin)

### Users
- `GET /api/users` - Get all users (Admin)
- `GET /api/users/{id}` - Get user by ID (Admin)
- `POST /api/users/register` - Register new user (Admin)
- `DELETE /api/users/{id}` - Delete user (Admin)

### Dashboard
- `GET /api/dashboard/stats` - Get dashboard statistics

### Health Data Export
- `GET /api/health-data/anonymous-adoptions` - Export anonymous adoption data

## Health Organizations API

For health institutions to access anonymous adoption data, use one of the pre-configured API tokens:

**Available API Tokens:**
- `health_test_token_123` - Test Health Institution
- `health_regional_456` - Regional Medical Center  
- `health_research_789` - University Research Lab

**Example API Call:**
```bash
curl -X GET "http://localhost:8080/api/health-data/anonymous-adoptions" \
     -H "X-API-Token: health_test_token_123" \
     -H "Content-Type: application/json"
```

**Alternative tokens for testing:**
```bash
# Regional Medical Center
curl -X GET "http://localhost:8080/api/health-data/anonymous-adoptions" \
     -H "X-API-Token: health_regional_456" \
     -H "Content-Type: application/json"

# University Research Lab  
curl -X GET "http://localhost:8080/api/health-data/anonymous-adoptions" \
     -H "X-API-Token: health_research_789" \
     -H "Content-Type: application/json"
```

**Response Example:**
```json
[
  {
    "adoptionDate": "2025-08-11T01:24:09.046215",
    "catBreed": "Himalayan",
    "catAge": 6,
    "locationRegion": "654 Maple Drive",
    "status": "COMPLETED",
    "tenantId": null
  },
  {
    "adoptionDate": "2025-08-11T01:24:09.047913",
    "catBreed": "Turkish Angora", 
    "catAge": 1,
    "locationRegion": "987 Cedar Lane",
    "status": "COMPLETED",
    "tenantId": null
  }
]
```

**Note:** The API returns anonymized data with:
- Full timestamp of adoption date
- Cat breed and age information
- General location region (street address level)
- Adoption status (PENDING, APPROVED, COMPLETED)
- Tenant ID for multi-tenant support

## Default Users

After data initialization, the following users are available:

**Admin User:**
- Email: `admin@werescuecats.io`
- Password: `admin123`

**Regular Users:**
- Email: `john.doe@example.com` / Password: `password123`
- Email: `jane.smith@example.com` / Password: `password123`
- Email: `mike.johnson@example.com` / Password: `password123`

## Security Features

- JWT-based authentication
- Role-based access control (USER/ADMIN)
- Password encryption using BCrypt
- API token validation for external access
- CORS configuration for frontend integration
- Input validation and sanitization

## Multi-Tenant Support

The application includes tenant support for future expansion:
- Tenant ID field in user and adoption entities
- API structure ready for multi-tenant deployment
- Database structure prepared for multi-tenant deployment

## Performance Considerations

- Caching implementation for frequently accessed data (@Cacheable on breed and cat queries)
- Custom database queries with @Query annotations for optimized data retrieval
- Lazy loading for entity relationships to reduce memory usage
- Built with Spring Boot for scalability and concurrent request handling

## Testing

Run backend tests:
```bash
cd backend
mvn test
```

Run frontend tests:
```bash
cd frontend
ng test
```
