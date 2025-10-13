package plot.plot.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    public void sendPasswordResetEmail(String email, String username, String resetToken) {
        try {
            String resetLink = frontendUrl + "/forgot-password?token=" + resetToken;

            // Prepare email body
            Map<String, Object> body = new HashMap<>();
            body.put("sender", Map.of("name", senderName, "email", senderEmail));
            body.put("to", List.of(Map.of("email", email)));
            body.put("subject", "Reset Your Password - Randa Real Estate");
            body.put("htmlContent", buildPasswordResetEmailTemplate(resetLink, username));

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            // Send request
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    BREVO_API_URL,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            System.out.println("DEBUG: Password reset email sent successfully to: " + email);
            System.out.println("DEBUG Response: " + response.getBody());

        } catch (Exception e) {
            System.out.println("DEBUG: Failed to send email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }


    private String buildPasswordResetEmailTemplate(String resetLink, String username) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        * { margin: 0; padding: 0; box-sizing: border-box; }\n" +
                "        body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f5f5; padding: 20px; }\n" +
                "        .email-container { max-width: 600px; margin: 0 auto; background-color: white; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }\n" +
                "        .email-header { background: linear-gradient(135deg, #007bff 0%, #0056b3 100%); color: white; padding: 40px 20px; text-align: center; }\n" +
                "        .email-header h1 { font-size: 24px; font-weight: 700; margin-bottom: 5px; }\n" +
                "        .email-header p { font-size: 14px; opacity: 0.9; }\n" +
                "        .email-body { padding: 40px; color: #333; }\n" +
                "        .greeting { font-size: 16px; margin-bottom: 20px; line-height: 1.6; }\n" +
                "        .greeting strong { color: #007bff; }\n" +
                "        .message { font-size: 14px; color: #666; margin-bottom: 30px; line-height: 1.6; }\n" +
                "        .reset-button-container { text-align: center; margin: 35px 0; }\n" +
                "        .reset-button { display: inline-block; background: linear-gradient(135deg, #007bff 0%, #0056b3 100%); color: white; padding: 16px 40px; text-decoration: none; border-radius: 8px; font-weight: 700; font-size: 16px; box-shadow: 0 4px 12px rgba(0,123,255,0.3); border: 2px solid #007bff; }\n" +
                "        .reset-link { background-color: #f5f5f5; padding: 15px; border-radius: 8px; margin: 25px 0; word-break: break-all; }\n" +
                "        .reset-link p { font-size: 12px; color: #999; margin-bottom: 8px; }\n" +
                "        .reset-link a { color: #007bff; font-size: 12px; text-decoration: none; }\n" +
                "        .security-note { background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 25px 0; border-radius: 4px; font-size: 13px; color: #856404; }\n" +
                "        .email-footer { background-color: #f9f9f9; padding: 20px; text-align: center; border-top: 1px solid #eee; }\n" +
                "        .footer-text { font-size: 12px; color: #999; line-height: 1.6; }\n" +
                "        .footer-text a { color: #007bff; text-decoration: none; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <div class=\"email-header\">\n" +
                "            <h1>🔐 Password Reset</h1>\n" +
                "            <p>Randa Real Estate</p>\n" +
                "        </div>\n" +
                "        <div class=\"email-body\">\n" +
                "            <div class=\"greeting\">Hello <strong>" + username + "</strong>,</div>\n" +
                "            <div class=\"message\">We received a request to reset your password for your Randa Real Estate account. If you didn't make this request, you can safely ignore this email.</div>\n" +
                "            <div class=\"reset-button-container\">\n" +
                "                <a href=\"" + resetLink + "\" class=\"reset-button\">Reset Your Password</a>\n" +
                "            </div>\n" +
                "            <div class=\"message\">Or copy and paste this link in your browser:</div>\n" +
                "            <div class=\"reset-link\">\n" +
                "                <p>Reset Link:</p>\n" +
                "                <a href=\"" + resetLink + "\" target=\"_blank\">" + resetLink + "</a>\n" +
                "            </div>\n" +
                "            <div class=\"security-note\">\n" +
                "                <strong>🔒 Security Notice</strong>\n" +
                "                This link will expire in <strong>1 hour</strong>. If the link expires, you can request a new password reset. Never share this link with anyone.\n" +
                "            </div>\n" +
                "            <div class=\"message\">If you have any questions or didn’t request this password reset, please contact our support team.</div>\n" +
                "        </div>\n" +
                "        <div class=\"email-footer\">\n" +
                "            <div class=\"footer-text\">\n" +
                "                <p>© 2025 Randa Real Estate. All rights reserved.</p>\n" +
                "                <p><a href=\"https://randarealEstate.com\">Visit our website</a></p>\n" +
                "                <p>This is an automated message, please do not reply to this email.</p>\n" +
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
    }

    public boolean emailExists(String email) {
        return true;
    }
}
