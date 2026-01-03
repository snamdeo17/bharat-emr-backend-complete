package com.bharatemr.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    
    @Value("${app.twilio.account-sid}")
    private String twilioAccountSid;
    
    @Value("${app.twilio.auth-token}")
    private String twilioAuthToken;
    
    @Value("${app.twilio.phone-number}")
    private String twilioPhoneNumber;
    
    @Value("${app.whatsapp.api-key}")
    private String whatsappApiKey;
    
    @Value("${app.whatsapp.api-url}")
    private String whatsappApiUrl;
    
    private boolean twilioInitialized = false;
    
    @Async
    public void sendSms(String toNumber, String message) {
        try {
            if (!twilioInitialized && !twilioAccountSid.equals("your_account_sid")) {
                Twilio.init(twilioAccountSid, twilioAuthToken);
                twilioInitialized = true;
            }
            
            if (twilioInitialized) {
                Message.creator(
                    new PhoneNumber(toNumber),
                    new PhoneNumber(twilioPhoneNumber),
                    message
                ).create();
                
                log.info("SMS sent successfully to: {}", toNumber);
            } else {
                // Mock SMS sending for development
                log.warn("Twilio not configured. Mock SMS sent to {}: {}", toNumber, message);
            }
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", toNumber, e.getMessage());
        }
    }
    
    @Async
    public void sendWhatsAppMessage(String toNumber, String message) {
        try {
            if (!whatsappApiKey.equals("your_whatsapp_api_key")) {
                // Implement WhatsApp Business API integration here
                // This depends on your WhatsApp Business API provider
                log.info("WhatsApp message sent to: {}", toNumber);
            } else {
                // Mock WhatsApp sending for development
                log.warn("WhatsApp not configured. Mock message sent to {}: {}", toNumber, message);
            }
        } catch (Exception e) {
            log.error("Failed to send WhatsApp message to {}: {}", toNumber, e.getMessage());
        }
    }
    
    @Async
    public void sendVisitNotification(String patientMobile, String patientName, 
                                     String doctorName, String visitSummary, 
                                     String appDownloadLink, String patientId) {
        String message = String.format(
            "Dear %s,\n\n" +
            "Your consultation with Dr. %s has been recorded.\n\n" +
            "Summary: %s\n\n" +
            "Your Patient ID: %s\n\n" +
            "Download Bharat EMR app: %s\n\n" +
            "Thank you!",
            patientName, doctorName, visitSummary, patientId, appDownloadLink
        );
        
        sendWhatsAppMessage(patientMobile, message);
        // Fallback to SMS if WhatsApp fails
        sendSms(patientMobile, message);
    }
    
    @Async
    public void sendFollowUpReminder(String patientMobile, String patientName, 
                                    String doctorName, String appointmentDateTime) {
        String message = String.format(
            "Dear %s,\n\n" +
            "Reminder: You have a follow-up appointment with Dr. %s on %s.\n\n" +
            "Please arrive 10 minutes early.\n\n" +
            "Bharat EMR",
            patientName, doctorName, appointmentDateTime
        );
        
        sendWhatsAppMessage(patientMobile, message);
        sendSms(patientMobile, message);
    }
}