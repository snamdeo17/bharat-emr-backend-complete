package com.bharatemr.service;

import com.bharatemr.model.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class PdfGeneratorService {
    
    @Value("${app.file-upload.prescription-dir:./prescriptions}")
    private String prescriptionDir;
    
    public String generatePrescriptionPdf(Visit visit, Prescription prescription) throws IOException {
        // Ensure directory exists
        File directory = new File(prescriptionDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        String fileName = "prescription_" + visit.getId() + "_" + System.currentTimeMillis() + ".pdf";
        String filePath = prescriptionDir + File.separator + fileName;
        
        PdfWriter writer = new PdfWriter(new FileOutputStream(filePath));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        // Header
        addHeader(document, visit.getDoctor());
        
        // Patient Information
        addPatientInfo(document, visit);
        
        // Prescription Details
        addPrescriptionDetails(document, prescription);
        
        // Footer
        addFooter(document, visit.getDoctor());
        
        document.close();
        
        log.info("Prescription PDF generated: {}", filePath);
        
        return filePath;
    }
    
    private void addHeader(Document document, Doctor doctor) {
        // Doctor's header
        Paragraph clinicName = new Paragraph(doctor.getClinicName())
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(clinicName);
        
        Paragraph doctorName = new Paragraph("Dr. " + doctor.getFullName())
                .setFontSize(16)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(doctorName);
        
        Paragraph qualification = new Paragraph(doctor.getQualification() + " - " + doctor.getSpecialization())
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER);
        document.add(qualification);
        
        Paragraph address = new Paragraph(doctor.getClinicAddress())
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(address);
        
        // Separator line
        document.add(new Paragraph("=".repeat(80))
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10));
    }
    
    private void addPatientInfo(Document document, Visit visit) {
        Patient patient = visit.getPatient();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        
        Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        table.setWidth(UnitValue.createPercentValue(100));
        
        table.addCell(createCell("Patient Name: " + patient.getFullName()));
        table.addCell(createCell("Patient ID: " + patient.getPatientId()));
        table.addCell(createCell("Age/Gender: " + patient.getAge() + " / " + patient.getGender()));
        table.addCell(createCell("Date: " + visit.getVisitDate().format(formatter)));
        table.addCell(createCell("Mobile: " + patient.getMobileNumber()));
        table.addCell(createCell("Visit ID: " + visit.getId()));
        
        document.add(table);
        document.add(new Paragraph("\n"));
        
        // Chief Complaint
        document.add(new Paragraph("Chief Complaint:").setBold());
        document.add(new Paragraph(visit.getChiefComplaint()).setMarginBottom(10));
        
        // Clinical Notes
        if (visit.getClinicalNotes() != null && !visit.getClinicalNotes().isEmpty()) {
            document.add(new Paragraph("Clinical Notes:").setBold());
            document.add(new Paragraph(visit.getClinicalNotes()).setMarginBottom(10));
        }
    }
    
    private void addPrescriptionDetails(Document document, Prescription prescription) {
        // Medicines
        if (!prescription.getMedicines().isEmpty()) {
            document.add(new Paragraph("\nPrescription:").setBold().setFontSize(14));
            document.add(new Paragraph("=".repeat(80)));
            
            Table medicineTable = new Table(UnitValue.createPercentArray(new float[]{3, 2, 2, 2, 3}));
            medicineTable.setWidth(UnitValue.createPercentValue(100));
            
            // Header
            medicineTable.addHeaderCell(createHeaderCell("Medicine"));
            medicineTable.addHeaderCell(createHeaderCell("Dosage"));
            medicineTable.addHeaderCell(createHeaderCell("Frequency"));
            medicineTable.addHeaderCell(createHeaderCell("Duration"));
            medicineTable.addHeaderCell(createHeaderCell("Instructions"));
            
            // Medicines
            for (Medicine medicine : prescription.getMedicines()) {
                medicineTable.addCell(createCell(medicine.getMedicineName()));
                medicineTable.addCell(createCell(medicine.getDosage()));
                medicineTable.addCell(createCell(medicine.getFrequency()));
                medicineTable.addCell(createCell(medicine.getDuration()));
                medicineTable.addCell(createCell(medicine.getInstructions() != null ? 
                    medicine.getInstructions() : "-"));
            }
            
            document.add(medicineTable);
        }
        
        // Tests
        if (!prescription.getTests().isEmpty()) {
            document.add(new Paragraph("\nRecommended Tests:").setBold().setFontSize(14));
            document.add(new Paragraph("=".repeat(80)));
            
            for (Test test : prescription.getTests()) {
                document.add(new Paragraph("• " + test.getTestName()));
                if (test.getInstructions() != null && !test.getInstructions().isEmpty()) {
                    document.add(new Paragraph("  Instructions: " + test.getInstructions())
                            .setFontSize(10)
                            .setItalic());
                }
            }
        }
    }
    
    private void addFooter(Document document, Doctor doctor) {
        document.add(new Paragraph("\n\n"));
        document.add(new Paragraph("=".repeat(80)));
        
        Paragraph signature = new Paragraph("Dr. " + doctor.getFullName())
                .setTextAlignment(TextAlignment.RIGHT)
                .setBold();
        document.add(signature);
        
        Paragraph regNo = new Paragraph("Reg. No: " + doctor.getMedicalRegistrationNumber())
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10);
        document.add(regNo);
        
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Note: This is a digitally generated prescription.")
                .setFontSize(8)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER));
    }
    
    private Cell createCell(String content) {
        return new Cell()
                .add(new Paragraph(content))
                .setBorder(Border.NO_BORDER)
                .setPadding(5);
    }
    
    private Cell createHeaderCell(String content) {
        return new Cell()
                .add(new Paragraph(content).setBold())
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(5);
    }
    
    public byte[] readPdfFile(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }
}