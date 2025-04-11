package com.awss3.s3.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

@Service
public class EmailService {

    private final SesClient sesClient;

    @Value("${aws.ses.from.email}")
    private String fromEmail;

    @Value("${application.base.url}")
    private String appBaseUrl;

    // Constructor injection of the configured SES client
    public EmailService(SesClient sesClient) {
        this.sesClient = sesClient;
    }

    public void sendConfirmationEmail(String toEmail, String fromEmail, double amount, String transactionId) {
        String confirmationLink = appBaseUrl + "/payments/confirm?transactionId=" + transactionId;

        String subject = "Payment Confirmation Required";
        String message = String.format(
                "You have received a payment request of $%.2f from %s. Please confirm your balance to complete the payment.\n\nClick the link to confirm: %s",
                amount, fromEmail, confirmationLink);

        SendEmailRequest emailRequest = SendEmailRequest.builder()
                .source(this.fromEmail)
                .destination(Destination.builder().toAddresses(toEmail).build())
                .message(Message.builder()
                        .subject(Content.builder().data(subject).build())
                        .body(Body.builder()
                                .text(Content.builder().data(message).build())
                                .build())
                        .build())
                .build();

        sesClient.sendEmail(emailRequest);
    }
    
    /**
     * Verifies an email address in AWS SES to ensure it's allowed to send/receive emails.
     *
     * @param emailAddress The email address to verify
     */
    public void verifyEmailAddress(String emailAddress) {
        try {
            VerifyEmailIdentityRequest verifyRequest = VerifyEmailIdentityRequest.builder()
                    .emailAddress(emailAddress)
                    .build();

            sesClient.verifyEmailIdentity(verifyRequest);
            System.out.println("Verification email sent to: " + emailAddress);
        } catch (SesException e) {
            System.err.println("Failed to verify email address: " + e.getMessage());
        }
    }
}
