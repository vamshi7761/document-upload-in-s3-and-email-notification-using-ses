package com.awss3.s3.Controller;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private com.awss3.s3.Service.EmailService emailService;

    // Mock storage for transactions with metadata including amount and creation time
    private final Map<String, TransactionMetadata> pendingTransactions = new HashMap<>();

    // Define the link expiry duration (e.g., 15 minutes)
    private static final long EXPIRY_DURATION_MINUTES = 15;
    
    @PostMapping("/verify-email")
    public String verifyEmail(@RequestParam String email) {
        try {
            emailService.verifyEmailAddress(email);
            return "Verification email sent to: " + email;
        } catch (Exception e) {
            return "Failed to send verification email: " + e.getMessage();
        }
    }


    @PostMapping("/complete")
    public String completePayment(@RequestParam String fromEmail,
                                  @RequestParam String toEmail,
                                  @RequestParam double amount) {
        // Generate a unique transaction ID
        String transactionId = java.util.UUID.randomUUID().toString();

        // Store the pending transaction with current time as creation time
        pendingTransactions.put(transactionId, new TransactionMetadata(amount, LocalDateTime.now()));

        // Send email to the recipient with a confirmation link
        emailService.sendConfirmationEmail(toEmail, fromEmail, amount, transactionId);

        return "Payment request sent. Awaiting recipient confirmation.";
    }

    @GetMapping("/confirm")
    public String confirmPayment(@RequestParam String transactionId) {
        if (pendingTransactions.containsKey(transactionId)) {
            TransactionMetadata metadata = pendingTransactions.get(transactionId);
            LocalDateTime creationTime = metadata.getCreationTime();

            if (creationTime.plusMinutes(EXPIRY_DURATION_MINUTES).isAfter(LocalDateTime.now())) {
                double amount = pendingTransactions.remove(transactionId).getAmount(); // Remove the transaction after confirmation
                // Perform additional logic if needed (e.g., update balances in DB)
                return "Payment confirmed successfully for amount: $" + amount;
            } else {
                // Return as invalid payment without removing the transaction
                return "Invalid payment: The confirmation link has expired.";
            }
        } else {
            return "Invalid transaction ID or the transaction has already been processed.";
        }
    }

    // Inner class to hold transaction metadata
    private static class TransactionMetadata {
        private final double amount;
        private final LocalDateTime creationTime;

        public TransactionMetadata(double amount, LocalDateTime creationTime) {
            this.amount = amount;
            this.creationTime = creationTime;
        }

        public double getAmount() {
            return amount;
        }

        public LocalDateTime getCreationTime() {
            return creationTime;
        }
    }
}
