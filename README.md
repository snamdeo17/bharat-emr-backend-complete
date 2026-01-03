# 🏥 Bharat EMR Backend - Complete Production-Ready System

## 📋 Overview

Bharat EMR is a comprehensive Electronic Medical Records system designed for Indian healthcare providers. This Spring Boot backend provides robust APIs for doctor registration, patient management, visit tracking, prescription generation, and follow-up scheduling.

## ✨ Features

### Doctor Features
- ✅ Self-registration with OTP verification
- ✅ Auto-generated unique Doctor IDs
- ✅ Patient onboarding and management
- ✅ Complete visit workflow (History → Prescription → Follow-up)
- ✅ Prescription PDF generation
- ✅ Patient history tracking

### Patient Features
- ✅ OTP-based mobile authentication
- ✅ View all past visits and prescriptions
- ✅ Download prescription PDFs
- ✅ Follow-up appointment management
- ✅ WhatsApp/SMS notifications

### Admin Features
- ✅ Dashboard with analytics
- ✅ Doctor and patient management
- ✅ Block/unblock accounts
- ✅ System-wide statistics

## 🛠️ Tech Stack

- **Framework:** Spring Boot 2.7.18
- **Java Version:** 11
- **Database:** PostgreSQL
- **Security:** JWT (JSON Web Tokens)
- **ORM:** JPA/Hibernate
- **Migration:** Flyway
- **SMS:** Twilio
- **PDF:** iText7
- **Documentation:** Swagger/OpenAPI
- **Build Tool:** Maven

## 📦 Prerequisites

- Java 11 or higher
- Maven 3.6+
- PostgreSQL 12+
- (Optional) Docker & Docker Compose

## 🚀 Quick Start

### Option 1: Local Setup

#### 1. Clone the Repository
```bash
git clone https://github.com/snamdeo17/bharat-emr-backend-complete.git
cd bharat-emr-backend-complete
```

#### 2. Setup PostgreSQL Database
```bash
# Install PostgreSQL (if not already installed)
# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib

# macOS
brew install postgresql

# Start PostgreSQL service
sudo service postgresql start

# Create database
sudo -u postgres psql
CREATE DATABASE bharatemr;
CREATE USER bharatemr_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE bharatemr TO bharatemr_user;
\q
```

#### 3. Configure Environment Variables
Create a `.env` file or set environment variables:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=bharatemr
export DB_USERNAME=bharatemr_user
export DB_PASSWORD=your_password

export JWT_SECRET=YourSuperSecretKeyForJWTMustBeLongEnoughForHS512

# Optional: Twilio for SMS
export TWILIO_ACCOUNT_SID=your_twilio_sid
export TWILIO_AUTH_TOKEN=your_twilio_token
export TWILIO_PHONE_NUMBER=+1234567890

# Optional: WhatsApp Business API
export WHATSAPP_API_KEY=your_whatsapp_key
export WHATSAPP_API_URL=https://api.whatsapp.com
```

#### 4. Build and Run
```bash
# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run

# Or run the JAR file
java -jar target/bharat-emr-backend-1.0.0.jar
```

### Option 2: Docker Setup

```bash
# Build and run with Docker Compose
docker-compose up -d
```

## 📚 API Documentation

Once the application is running, access:

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **API Docs:** http://localhost:8080/api-docs

## 🔑 API Endpoints

### Authentication & OTP
```
POST /api/otp/send - Generate and send OTP
POST /api/otp/verify - Verify OTP
```

### Doctor APIs
```
POST /api/doctors/register - Doctor self-registration
POST /api/doctors/login - Doctor login with OTP
GET /api/doctors/profile - Get doctor profile
PUT /api/doctors/profile - Update doctor profile
POST /api/doctors/patients - Onboard new patient
GET /api/doctors/patients - List doctor's patients
```

### Visit & Prescription APIs
```
POST /api/visits - Create visit with prescription
GET /api/visits/{id} - Get visit details
GET /api/doctors/{id}/visits - Doctor's visit history
GET /api/patients/{id}/visits - Patient's visit history
GET /api/visits/{id}/prescription/pdf - Download prescription PDF
```

### Follow-up APIs
```
POST /api/follow-ups - Schedule follow-up
GET /api/follow-ups/{id} - Get follow-up details
PUT /api/follow-ups/{id} - Update follow-up
DELETE /api/follow-ups/{id} - Cancel follow-up
```

### Patient APIs
```
POST /api/patients/login - Patient login with OTP
GET /api/patients/dashboard - Patient dashboard data
GET /api/patients/visits - Patient's visits
GET /api/patients/prescriptions - Patient's prescriptions
```

### Admin APIs
```
GET /api/admin/dashboard - Dashboard statistics
GET /api/admin/doctors - List all doctors
PUT /api/admin/doctors/{id}/block - Block/unblock doctor
GET /api/admin/patients - List all patients
PUT /api/admin/patients/{id}/block - Block/unblock patient
```

## 🗄️ Database Schema

The database schema includes:
- **doctors** - Doctor information
- **patients** - Patient records
- **visits** - Medical visit records
- **prescriptions** - Prescription details
- **prescription_medicines** - Medicines in prescriptions
- **prescription_tests** - Recommended tests
- **follow_ups** - Follow-up appointments
- **otp_verifications** - OTP verification records
- **admin_users** - Admin user accounts

All tables have proper indexes and foreign key constraints.

## 🧪 Testing

### Run Tests
```bash
./mvnw test
```

### Sample API Calls

**1. Generate OTP for Doctor Registration:**
```bash
curl -X POST http://localhost:8080/api/otp/send \
  -H "Content-Type: application/json" \
  -d '{
    "mobileNumber": "+919876543210",
    "purpose": "REGISTRATION"
  }'
```

**2. Register Doctor:**
```bash
curl -X POST http://localhost:8080/api/doctors/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Dr. Amit Kumar",
    "mobileNumber": "+919876543210",
    "email": "amit@example.com",
    "specialization": "Cardiologist",
    "qualification": "MBBS, MD",
    "yearsOfExperience": 10,
    "clinicName": "Kumar Clinic",
    "clinicAddress": "123 MG Road, Delhi",
    "medicalRegistrationNumber": "MCI123456",
    "otp": "123456"
  }'
```

## 🔒 Security

- JWT-based authentication
- Password hashing with BCrypt
- OTP verification for registration and login
- Role-based access control (DOCTOR, PATIENT, ADMIN)
- CORS configured for cross-origin requests
- Secure API endpoints

## 📊 Configuration

Key configuration properties in `application.yml`:

```yaml
app:
  jwt:
    secret: ${JWT_SECRET}
    expiration: 86400000 # 24 hours
  otp:
    expiration: 300000 # 5 minutes
    length: 6
  twilio:
    account-sid: ${TWILIO_ACCOUNT_SID}
    auth-token: ${TWILIO_AUTH_TOKEN}
```

## 🚢 Deployment

### Production Checklist
- [ ] Update JWT secret to a strong random key
- [ ] Configure production database
- [ ] Set up Twilio/SMS provider
- [ ] Configure WhatsApp Business API
- [ ] Set up SSL/TLS certificates
- [ ] Configure logging
- [ ] Set up monitoring and alerts
- [ ] Configure backup strategy

### Environment Variables for Production
```bash
SPRING_PROFILES_ACTIVE=prod
DB_HOST=your-prod-db-host
DB_USERNAME=prod_user
DB_PASSWORD=strong_password
JWT_SECRET=very_long_random_secret_key
```

## 📝 Development Notes

### Adding New Features
1. Create entity in `model/` package
2. Create repository in `repository/` package
3. Create DTOs in `dto/` package
4. Implement service in `service/` package
5. Create controller in `controller/` package
6. Add database migration in `resources/db/migration/`

### Code Style
- Use Lombok for boilerplate code
- Follow REST API best practices
- Write meaningful commit messages
- Add JavaDoc for public methods

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- **Shyam Namdeo** - [GitHub](https://github.com/snamdeo17)

## 🐛 Issues

Report issues at: https://github.com/snamdeo17/bharat-emr-backend-complete/issues

## 📞 Support

For support, email: support@bharatemr.com

---

**Built with ❤️ for Indian Healthcare**
