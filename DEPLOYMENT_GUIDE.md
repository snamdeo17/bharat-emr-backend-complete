# 🚀 Bharat EMR - Complete Deployment & Testing Guide

## ✅ Project Status: 100% COMPLETE!

**All backend components are now implemented and ready for deployment!**

---

## 📊 What's Included

### Backend Components (100% Complete)

#### 1. Database Layer ✅
- Complete PostgreSQL schema
- 10+ tables with relationships
- Flyway migrations
- Indexes and constraints

#### 2. Entity Models ✅
- Doctor, Patient, Visit, Prescription
- Medicine, Test, FollowUp, OtpVerification
- JPA annotations and auditing

#### 3. Repositories ✅
- 8 JPA repositories
- Custom query methods
- Pagination support

#### 4. DTOs ✅
- 11+ DTO classes
- Validation annotations
- Response wrappers

#### 5. Services ✅
- DoctorService
- PatientService
- VisitService
- PrescriptionService
- FollowUpService
- OtpService
- NotificationService
- PdfGeneratorService

#### 6. Controllers ✅
- OtpController
- DoctorController
- PatientController
- VisitController
- FollowUpController
- AdminController

#### 7. Security ✅
- JWT authentication
- Role-based access control
- CORS configuration
- Security filters

#### 8. Configuration ✅
- Swagger/OpenAPI documentation
- Environment-specific configs
- Docker support
- Exception handling

---

## 🛠️ Quick Start

### Option 1: Local Development

#### Prerequisites
```bash
# Check Java version
java -version  # Should be 11 or higher

# Check Maven
mvn -version

# Check PostgreSQL
psql --version
```

#### Setup Steps

**1. Clone the repository**
```bash
git clone https://github.com/snamdeo17/bharat-emr-backend-complete.git
cd bharat-emr-backend-complete
```

**2. Setup PostgreSQL Database**
```bash
# Start PostgreSQL
sudo service postgresql start

# Create database and user
sudo -u postgres psql
```

```sql
CREATE DATABASE bharatemr;
CREATE USER bharatemr_user WITH PASSWORD 'bharatemr_password';
GRANT ALL PRIVILEGES ON DATABASE bharatemr TO bharatemr_user;
\q
```

**3. Configure Environment (Optional)**
```bash
# Copy example env file
cp .env.example .env

# Edit .env with your settings
nano .env
```

**4. Build and Run**
```bash
# Build the project
./mvnw clean install

# Run in development mode
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Or run the JAR
java -jar target/bharat-emr-backend-1.0.0.jar
```

**5. Verify Installation**
```bash
# Check if server is running
curl http://localhost:8080/actuator/health

# Open Swagger UI in browser
open http://localhost:8080/swagger-ui.html
```

### Option 2: Docker Deployment

**1. Using Docker Compose (Recommended)**
```bash
# Clone repository
git clone https://github.com/snamdeo17/bharat-emr-backend-complete.git
cd bharat-emr-backend-complete

# Start all services
docker-compose up -d

# Check logs
docker-compose logs -f backend

# Access application
open http://localhost:8080/swagger-ui.html
```

**2. Manual Docker Build**
```bash
# Build image
docker build -t bharatemr-backend .

# Run container
docker run -p 8080:8080 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=bharatemr \
  -e DB_USERNAME=bharatemr_user \
  -e DB_PASSWORD=bharatemr_password \
  -e JWT_SECRET=YourSecretKey \
  bharatemr-backend
```

---

## 🧪 Testing the API

### Using Swagger UI (Easiest)

1. Open: `http://localhost:8080/swagger-ui.html`
2. Explore all available endpoints
3. Test directly from the browser

### Using cURL

#### 1. Send OTP for Doctor Registration
```bash
curl -X POST http://localhost:8080/api/otp/send \
  -H "Content-Type: application/json" \
  -d '{
    "mobileNumber": "+919876543210",
    "purpose": "REGISTRATION"
  }'
```

#### 2. Register Doctor
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

**Response:**
```json
{
  "success": true,
  "message": "Doctor registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userId": "DRKUMA1234",
    "userName": "Dr. Amit Kumar",
    "userType": "DOCTOR"
  }
}
```

#### 3. Onboard Patient (With JWT Token)
```bash
curl -X POST http://localhost:8080/api/doctors/DRKUMA1234/patients \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "fullName": "Rahul Sharma",
    "gender": "Male",
    "age": 35,
    "mobileNumber": "+919876543211",
    "email": "rahul@example.com",
    "address": "456 Park Street, Mumbai"
  }'
```

#### 4. Create Visit with Prescription
```bash
curl -X POST http://localhost:8080/api/visits \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "patientId": 1,
    "chiefComplaint": "Chest pain and shortness of breath",
    "presentIllness": "Patient complaining of chest pain for 2 days",
    "clinicalNotes": "BP: 140/90, Heart rate: 85 bpm",
    "medicines": [
      {
        "medicineName": "Aspirin",
        "dosage": "75mg",
        "frequency": "Once daily",
        "duration": "30 days",
        "instructions": "Take after breakfast"
      },
      {
        "medicineName": "Atorvastatin",
        "dosage": "10mg",
        "frequency": "Once daily",
        "duration": "30 days",
        "instructions": "Take at bedtime"
      }
    ],
    "tests": [
      {
        "testName": "ECG",
        "instructions": "Fasting required"
      },
      {
        "testName": "Lipid Profile",
        "instructions": "12 hour fasting"
      }
    ],
    "followUp": {
      "scheduledDate": "2026-01-15T10:00:00",
      "notes": "Review test results"
    }
  }'
```

#### 5. Generate Prescription PDF
```bash
curl -X POST http://localhost:8080/api/visits/1/prescription/generate-pdf \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

#### 6. Download Prescription PDF
```bash
curl -X GET http://localhost:8080/api/visits/1/prescription/pdf \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  --output prescription.pdf
```

### Using Postman

1. Import the API collection (create from Swagger)
2. Set environment variables:
   - `base_url`: http://localhost:8080
   - `jwt_token`: (get from login/register)
3. Test all endpoints

---

## 📊 Admin Dashboard Testing

```bash
# Get dashboard statistics
curl -X GET http://localhost:8080/api/admin/dashboard \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"

# List all doctors
curl -X GET http://localhost:8080/api/admin/doctors \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"

# Block a doctor
curl -X PUT "http://localhost:8080/api/admin/doctors/DRKUMA1234/block?block=true" \
  -H "Authorization: Bearer ADMIN_JWT_TOKEN"
```

---

## 🔧 Configuration

### Environment Variables

**Required:**
```bash
DB_HOST=localhost
DB_PORT=5432
DB_NAME=bharatemr
DB_USERNAME=bharatemr_user
DB_PASSWORD=your_password
JWT_SECRET=your_very_long_secret_key_here
```

**Optional (for SMS/WhatsApp):**
```bash
TWILIO_ACCOUNT_SID=your_sid
TWILIO_AUTH_TOKEN=your_token
TWILIO_PHONE_NUMBER=+1234567890
WHATSAPP_API_KEY=your_key
WHATSAPP_API_URL=https://api.whatsapp.com
```

### Application Profiles

**Development:**
```bash
java -jar app.jar --spring.profiles.active=dev
```

**Production:**
```bash
java -jar app.jar --spring.profiles.active=prod
```

---

## 📱 API Endpoints Summary

### Public Endpoints (No Auth Required)
- `POST /api/otp/send` - Send OTP
- `POST /api/otp/verify` - Verify OTP
- `POST /api/doctors/register` - Doctor registration
- `POST /api/doctors/login` - Doctor login
- `POST /api/patients/login` - Patient login

### Doctor Endpoints (Requires DOCTOR role)
- `GET /api/doctors/profile/{id}` - Get profile
- `PUT /api/doctors/profile/{id}` - Update profile
- `POST /api/doctors/{id}/patients` - Onboard patient
- `GET /api/doctors/{id}/patients` - List patients
- `POST /api/visits` - Create visit
- `PUT /api/visits/{id}` - Update visit
- `POST /api/visits/{id}/prescription/generate-pdf` - Generate PDF
- `POST /api/follow-ups/visit/{id}` - Schedule follow-up

### Patient Endpoints (Requires PATIENT role)
- `GET /api/patients/profile/{id}` - Get profile
- `GET /api/patients/dashboard/{id}` - Get dashboard
- `GET /api/patients/{id}/visits` - List visits
- `GET /api/visits/{id}/prescription/pdf` - Download prescription

### Admin Endpoints (Requires ADMIN role)
- `GET /api/admin/dashboard` - Dashboard stats
- `GET /api/admin/doctors` - List all doctors
- `GET /api/admin/patients` - List all patients
- `PUT /api/admin/doctors/{id}/block` - Block/unblock doctor
- `PUT /api/admin/patients/{id}/block` - Block/unblock patient

---

## 🐛 Troubleshooting

### Common Issues

**1. Database Connection Failed**
```bash
# Check PostgreSQL is running
sudo service postgresql status

# Check database exists
psql -U bharatemr_user -d bharatemr

# Verify credentials in application.yml
```

**2. JWT Token Issues**
```bash
# Ensure JWT_SECRET is set and long enough
echo $JWT_SECRET

# Token expired - login again to get new token
```

**3. OTP Not Sending**
```bash
# Check Twilio credentials
# In dev mode, OTP is logged in console
# Check application logs
```

**4. PDF Generation Failed**
```bash
# Ensure prescriptions directory exists
mkdir -p ./prescriptions
chmod 755 ./prescriptions
```

### Logs

```bash
# View application logs
tail -f logs/bharatemr.log

# Docker logs
docker-compose logs -f backend

# Check for errors
grep -i error logs/bharatemr.log
```

---

## 🚀 Production Deployment

### AWS Deployment

**1. Deploy on AWS Elastic Beanstalk**
```bash
# Install EB CLI
pip install awsebcli

# Initialize
eb init -p java-11 bharat-emr

# Create environment
eb create bharat-emr-prod

# Deploy
eb deploy
```

**2. Deploy on AWS ECS**
```bash
# Build and push Docker image
docker build -t bharatemr-backend .
docker tag bharatemr-backend:latest YOUR_ECR_URL/bharatemr:latest
docker push YOUR_ECR_URL/bharatemr:latest

# Deploy via ECS console or CLI
```

### Heroku Deployment

```bash
# Login to Heroku
heroku login

# Create app
heroku create bharatemr-backend

# Add PostgreSQL
heroku addons:create heroku-postgresql:hobby-dev

# Set environment variables
heroku config:set JWT_SECRET=your_secret

# Deploy
git push heroku main

# Open app
heroku open
```

### DigitalOcean Deployment

```bash
# Create Droplet (Ubuntu 20.04)
# SSH into droplet
ssh root@your-droplet-ip

# Install Java, PostgreSQL, Docker
apt update && apt install openjdk-11-jdk postgresql docker.io

# Clone and setup
git clone https://github.com/snamdeo17/bharat-emr-backend-complete.git
cd bharat-emr-backend-complete

# Run with Docker
docker-compose up -d
```

---

## 📊 Performance & Scaling

### Database Optimization
```sql
-- Add indexes for better performance
CREATE INDEX idx_doctor_mobile ON doctors(mobile_number);
CREATE INDEX idx_patient_mobile ON patients(mobile_number);
CREATE INDEX idx_visit_date ON visits(visit_date);
```

### Caching (Future Enhancement)
```java
// Add Redis for caching
@Cacheable(value = "doctors", key = "#doctorId")
public DoctorDto getDoctorProfile(String doctorId) {...}
```

### Load Balancing
```nginx
upstream bharatemr {
    server backend1:8080;
    server backend2:8080;
    server backend3:8080;
}
```

---

## ✅ Success! You're Ready to Go!

Your Bharat EMR backend is now:
- ✅ Fully implemented
- ✅ Production-ready
- ✅ Documented
- ✅ Deployable

**Next Steps:**
1. Test all endpoints using Swagger UI
2. Deploy to your preferred platform
3. Build the mobile app (React Native)
4. Add more features as needed

---

**Built with ❤️ for Indian Healthcare**

**Repository:** https://github.com/snamdeo17/bharat-emr-backend-complete

**Support:** support@bharatemr.com
