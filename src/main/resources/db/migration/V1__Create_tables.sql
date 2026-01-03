-- Bharat EMR Database Schema

-- Doctors Table
CREATE TABLE doctors (
    id BIGSERIAL PRIMARY KEY,
    doctor_id VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    mobile_number VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(255),
    specialization VARCHAR(255) NOT NULL,
    qualification VARCHAR(255) NOT NULL,
    years_of_experience INTEGER NOT NULL,
    clinic_name VARCHAR(255) NOT NULL,
    clinic_address TEXT NOT NULL,
    medical_registration_number VARCHAR(100) NOT NULL,
    profile_photo_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    is_blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_doctors_mobile ON doctors(mobile_number);
CREATE INDEX idx_doctors_doctor_id ON doctors(doctor_id);

-- Patients Table
CREATE TABLE patients (
    id BIGSERIAL PRIMARY KEY,
    patient_id VARCHAR(50) UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    gender VARCHAR(20) NOT NULL,
    age INTEGER NOT NULL,
    mobile_number VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    address TEXT,
    onboarded_by_doctor_id BIGINT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    is_blocked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (onboarded_by_doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

CREATE INDEX idx_patients_mobile ON patients(mobile_number);
CREATE INDEX idx_patients_patient_id ON patients(patient_id);
CREATE INDEX idx_patients_doctor ON patients(onboarded_by_doctor_id);

-- Visits Table
CREATE TABLE visits (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    visit_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    chief_complaint TEXT NOT NULL,
    past_illness TEXT,
    present_illness TEXT,
    medical_history TEXT,
    surgical_history TEXT,
    clinical_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE
);

CREATE INDEX idx_visits_patient ON visits(patient_id);
CREATE INDEX idx_visits_doctor ON visits(doctor_id);
CREATE INDEX idx_visits_date ON visits(visit_date);

-- Prescriptions Table
CREATE TABLE prescriptions (
    id BIGSERIAL PRIMARY KEY,
    visit_id BIGINT NOT NULL UNIQUE,
    pdf_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (visit_id) REFERENCES visits(id) ON DELETE CASCADE
);

-- Medicines Table
CREATE TABLE prescription_medicines (
    id BIGSERIAL PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    medicine_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL,
    duration VARCHAR(100) NOT NULL,
    instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE CASCADE
);

CREATE INDEX idx_medicines_prescription ON prescription_medicines(prescription_id);

-- Tests Table
CREATE TABLE prescription_tests (
    id BIGSERIAL PRIMARY KEY,
    prescription_id BIGINT NOT NULL,
    test_name VARCHAR(255) NOT NULL,
    test_type VARCHAR(100),
    instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (prescription_id) REFERENCES prescriptions(id) ON DELETE CASCADE
);

CREATE INDEX idx_tests_prescription ON prescription_tests(prescription_id);

-- Follow-ups Table
CREATE TABLE follow_ups (
    id BIGSERIAL PRIMARY KEY,
    visit_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    doctor_id BIGINT NOT NULL,
    scheduled_date TIMESTAMP NOT NULL,
    status VARCHAR(50) DEFAULT 'SCHEDULED',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (visit_id) REFERENCES visits(id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES doctors(id) ON DELETE CASCADE,
    CHECK (status IN ('SCHEDULED', 'COMPLETED', 'CANCELLED', 'RESCHEDULED'))
);

CREATE INDEX idx_followups_patient ON follow_ups(patient_id);
CREATE INDEX idx_followups_doctor ON follow_ups(doctor_id);
CREATE INDEX idx_followups_date ON follow_ups(scheduled_date);
CREATE INDEX idx_followups_status ON follow_ups(status);

-- OTP Verifications Table
CREATE TABLE otp_verifications (
    id BIGSERIAL PRIMARY KEY,
    mobile_number VARCHAR(20) NOT NULL,
    otp VARCHAR(10) NOT NULL,
    purpose VARCHAR(50) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    expiry_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (purpose IN ('REGISTRATION', 'LOGIN', 'PASSWORD_RESET'))
);

CREATE INDEX idx_otp_mobile ON otp_verifications(mobile_number);
CREATE INDEX idx_otp_expiry ON otp_verifications(expiry_time);

-- Admin Users Table
CREATE TABLE admin_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(50) DEFAULT 'ADMIN',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (role IN ('SUPER_ADMIN', 'ADMIN'))
);

-- Notifications Table
CREATE TABLE notifications (
    id BIGSERIAL PRIMARY KEY,
    recipient_mobile VARCHAR(20) NOT NULL,
    recipient_type VARCHAR(50) NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    message TEXT NOT NULL,
    status VARCHAR(50) DEFAULT 'PENDING',
    sent_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (recipient_type IN ('DOCTOR', 'PATIENT')),
    CHECK (notification_type IN ('SMS', 'WHATSAPP', 'PUSH')),
    CHECK (status IN ('PENDING', 'SENT', 'FAILED'))
);

CREATE INDEX idx_notifications_mobile ON notifications(recipient_mobile);
CREATE INDEX idx_notifications_status ON notifications(status);