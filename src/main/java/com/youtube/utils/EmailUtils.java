package com.youtube.utils;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailUtils {

    private final Logger log = LoggerFactory.getLogger(EmailUtils.class);

    private final Resend resendClient;

    public EmailUtils(Resend resendClient) {
        this.resendClient = resendClient;
    }

    public void sendEmail(String from, String to, String subject, String htmlContent) {
        CreateEmailOptions email = CreateEmailOptions.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .html(htmlContent)
                .build();

        try {
            resendClient.emails().send(email);
        } catch (Exception e) {
            log.info("Error sending email: {}", e.getMessage());
        }

    }
}
