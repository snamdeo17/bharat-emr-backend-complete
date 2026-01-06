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
        document.setMargins(20, 30, 20, 30); // Reduced margins

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
                .setFontSize(16) // Reduced from 20
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(0);
        document.add(clinicName);

        Paragraph doctorName = new Paragraph("Dr. " + doctor.getFullName())
                .setFontSize(12) // Reduced from 16
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(0);
        document.add(doctorName);

        Paragraph qualification = new Paragraph(doctor.getQualification() + " - " + doctor.getSpecialization())
                .setFontSize(10) // Reduced from 12
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(0);
        document.add(qualification);

        Paragraph address = new Paragraph(doctor.getClinicAddress())
                .setFontSize(9) // Reduced from 10
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
        document.add(address);

        // Separator line
        document.add(new Paragraph("-".repeat(110))
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5));
    }

    private void addPatientInfo(Document document, Visit visit) {
        Patient patient = visit.getPatient();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        Table table = new Table(UnitValue.createPercentArray(new float[] { 1, 1 }));
        table.setWidth(UnitValue.createPercentValue(100));
        table.setMarginBottom(5);

        table.addCell(createCell("Patient: " + patient.getFullName(), 10));
        table.addCell(createCell("ID: " + patient.getPatientId(), 10));
        table.addCell(createCell("Age/Gender: " + patient.getAge() + " / " + patient.getGender(), 10));
        table.addCell(createCell("Date: " + visit.getVisitDate().format(formatter), 10));
        table.addCell(createCell("Mobile: " + patient.getMobileNumber(), 10));
        table.addCell(createCell("Visit ID: " + visit.getId(), 10));

        document.add(table);

        // Chief Complaint
        document.add(new Paragraph("Chief Complaint:").setBold().setFontSize(10).setMarginBottom(0));
        document.add(new Paragraph(visit.getChiefComplaint()).setFontSize(10).setMarginBottom(5));

        // Clinical Notes
        if (visit.getClinicalNotes() != null && !visit.getClinicalNotes().isEmpty()) {
            document.add(new Paragraph("Clinical Notes:").setBold().setFontSize(10).setMarginBottom(0));
            document.add(new Paragraph(visit.getClinicalNotes()).setFontSize(10).setMarginBottom(5));
        }
    }

    private void addPrescriptionDetails(Document document, Prescription prescription) {
        // Medicines
        if (!prescription.getMedicines().isEmpty()) {
            document.add(new Paragraph("Prescription:").setBold().setFontSize(11).setMarginBottom(2));

            Table medicineTable = new Table(UnitValue.createPercentArray(new float[] { 3, 2, 2, 2, 3 }));
            medicineTable.setWidth(UnitValue.createPercentValue(100));
            medicineTable.setMarginBottom(10);

            // Header
            medicineTable.addHeaderCell(createHeaderCell("Medicine"));
            medicineTable.addHeaderCell(createHeaderCell("Dosage"));
            medicineTable.addHeaderCell(createHeaderCell("Freq"));
            medicineTable.addHeaderCell(createHeaderCell("Dur"));
            medicineTable.addHeaderCell(createHeaderCell("Instructions"));

            // Medicines
            for (Medicine medicine : prescription.getMedicines()) {
                medicineTable.addCell(createCell(medicine.getMedicineName(), 9));
                medicineTable.addCell(createCell(medicine.getDosage(), 9));
                medicineTable.addCell(createCell(medicine.getFrequency(), 9));
                medicineTable.addCell(createCell(medicine.getDuration(), 9));
                medicineTable
                        .addCell(createCell(medicine.getInstructions() != null ? medicine.getInstructions() : "-", 9));
            }

            document.add(medicineTable);
        }

        // Tests
        if (!prescription.getTests().isEmpty()) {
            document.add(new Paragraph("Recommended Tests:").setBold().setFontSize(11).setMarginBottom(2));

            for (Test test : prescription.getTests()) {
                Paragraph testPara = new Paragraph("• " + test.getTestName()).setFontSize(9).setMarginBottom(0);
                if (test.getInstructions() != null && !test.getInstructions().isEmpty()) {
                    testPara.add(new Paragraph(" (Inst: " + test.getInstructions() + ")")
                            .setFontSize(8)
                            .setItalic());
                }
                document.add(testPara);
            }
        }
    }

    private void addFooter(Document document, Doctor doctor) {
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("-".repeat(110)).setFontSize(8).setMarginBottom(5));

        Paragraph signature = new Paragraph("Dr. " + doctor.getFullName())
                .setTextAlignment(TextAlignment.RIGHT)
                .setBold()
                .setFontSize(10)
                .setMarginBottom(0);
        document.add(signature);

        Paragraph regNo = new Paragraph("Reg. No: " + doctor.getMedicalRegistrationNumber())
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(9)
                .setMarginBottom(10);
        document.add(regNo);

        document.add(new Paragraph("Note: This is a digitally generated prescription.")
                .setFontSize(7)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER));
    }

    private Cell createCell(String content, float fontSize) {
        return new Cell()
                .add(new Paragraph(content).setFontSize(fontSize))
                .setBorder(Border.NO_BORDER)
                .setPadding(2);
    }

    private Cell createHeaderCell(String content) {
        return new Cell()
                .add(new Paragraph(content).setBold().setFontSize(9))
                .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                .setPadding(3);
    }

    public byte[] readPdfFile(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }
}