// Updated EmailService.java with fixed HTML email
package plot.plot.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String email, String resetToken) {
        try {
            String resetLink = frontendUrl + "/forgot-password?token=" + resetToken;

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("Reset Your Password - Randa Real Estate");
            helper.setFrom("manzi2020d@gmail.com", "Randa Real Estate");

            // HTML email content with inline CSS
            String htmlContent = buildPasswordResetEmailTemplate(resetLink, email);
            helper.setText(htmlContent, true); // true = isHtml

            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String buildPasswordResetEmailTemplate(String resetLink, String email) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <style>\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        body {\n" +
                "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n" +
                "            background-color: #f5f5f5;\n" +
                "            padding: 20px;\n" +
                "        }\n" +
                "        .email-container {\n" +
                "            max-width: 600px;\n" +
                "            margin: 0 auto;\n" +
                "            background-color: white;\n" +
                "            border-radius: 12px;\n" +
                "            overflow: hidden;\n" +
                "            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);\n" +
                "        }\n" +
                "        .email-header {\n" +
                "            background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);\n" +
                "            color: white;\n" +
                "            padding: 40px 20px;\n" +
                "            text-align: center;\n" +
                "        }\n" +
                "        .email-header h1 {\n" +
                "            font-size: 24px;\n" +
                "            font-weight: 700;\n" +
                "            margin-bottom: 5px;\n" +
                "        }\n" +
                "        .email-header p {\n" +
                "            font-size: 14px;\n" +
                "            opacity: 0.9;\n" +
                "        }\n" +
                "        .email-body {\n" +
                "            padding: 40px;\n" +
                "            color: #333;\n" +
                "        }\n" +
                "        .greeting {\n" +
                "            font-size: 16px;\n" +
                "            margin-bottom: 20px;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "        .greeting strong {\n" +
                "            color: #007bff;\n" +
                "        }\n" +
                "        .message {\n" +
                "            font-size: 14px;\n" +
                "            color: #666;\n" +
                "            margin-bottom: 30px;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "        .reset-button-container {\n" +
                "            text-align: center;\n" +
                "            margin: 35px 0;\n" +
                "        }\n" +
                "        .reset-button {\n" +
                "            display: inline-block;\n" +
                "            background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);\n" +
                "            color: white;\n" +
                "            padding: 16px 40px;\n" +
                "            text-decoration: none;\n" +
                "            border-radius: 8px;\n" +
                "            font-weight: 700;\n" +
                "            font-size: 16px;\n" +
                "            box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);\n" +
                "            mso-padding-alt: 16px 40px;\n" +
                "            border: 2px solid #007bff;\n" +
                "        }\n" +
                "        .reset-link {\n" +
                "            background-color: #f5f5f5;\n" +
                "            padding: 15px;\n" +
                "            border-radius: 8px;\n" +
                "            margin: 25px 0;\n" +
                "            word-break: break-all;\n" +
                "        }\n" +
                "        .reset-link p {\n" +
                "            font-size: 12px;\n" +
                "            color: #999;\n" +
                "            margin-bottom: 8px;\n" +
                "        }\n" +
                "        .reset-link a {\n" +
                "            color: #007bff;\n" +
                "            font-size: 12px;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "        .security-note {\n" +
                "            background-color: #fff3cd;\n" +
                "            border-left: 4px solid #ffc107;\n" +
                "            padding: 15px;\n" +
                "            margin: 25px 0;\n" +
                "            border-radius: 4px;\n" +
                "            font-size: 13px;\n" +
                "            color: #856404;\n" +
                "        }\n" +
                "        .security-note strong {\n" +
                "            display: block;\n" +
                "            margin-bottom: 5px;\n" +
                "        }\n" +
                "        .email-footer {\n" +
                "            background-color: #f9f9f9;\n" +
                "            padding: 20px;\n" +
                "            text-align: center;\n" +
                "            border-top: 1px solid #eee;\n" +
                "        }\n" +
                "        .footer-text {\n" +
                "            font-size: 12px;\n" +
                "            color: #999;\n" +
                "            line-height: 1.6;\n" +
                "        }\n" +
                "        .footer-text a {\n" +
                "            color: #007bff;\n" +
                "            text-decoration: none;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"email-container\">\n" +
                "        <!-- Header -->\n" +
                "        <div class=\"email-header\">\n" +
                "            <h1>🔐 Password Reset</h1>\n" +
                "            <p>Randa Real Estate</p>\n" +
                "        </div>\n" +
                "        \n" +
                "        <!-- Body -->\n" +
                "        <div class=\"email-body\">\n" +
                "            <div class=\"greeting\">\n" +
                "                Hello <strong>" + email + "</strong>,\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"message\">\n" +
                "                We received a request to reset your password for your Randa Real Estate account. \n" +
                "                If you didn't make this request, you can safely ignore this email.\n" +
                "            </div>\n" +
                "            \n" +
                "            <!-- Reset Button -->\n" +
                "            <div class=\"reset-button-container\">\n" +
                "                <a href=\"" + resetLink + "\" class=\"reset-button\" style=\"display: inline-block; background: linear-gradient(135deg, #007bff 0%, #0056b3 100%); color: white; padding: 16px 40px; text-decoration: none; border-radius: 8px; font-weight: 700; font-size: 16px; box-shadow: 0 4px 12px rgba(0, 123, 255, 0.3);\">Reset Your Password</a>\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"message\">\n" +
                "                Or copy and paste this link in your browser:\n" +
                "            </div>\n" +
                "            \n" +
                "            <!-- Reset Link -->\n" +
                "            <div class=\"reset-link\">\n" +
                "                <p>Reset Link:</p>\n" +
                "                <a href=\"" + resetLink + "\" target=\"_blank\">" + resetLink + "</a>\n" +
                "            </div>\n" +
                "            \n" +
                "            <!-- Security Note -->\n" +
                "            <div class=\"security-note\">\n" +
                "                <strong>🔒 Security Notice</strong>\n" +
                "                This link will expire in <strong>1 hour</strong>. If the link expires, you can request a new password reset. \n" +
                "                Never share this link with anyone.\n" +
                "            </div>\n" +
                "            \n" +
                "            <div class=\"message\">\n" +
                "                If you have any questions or didn't request this password reset, please contact our support team.\n" +
                "            </div>\n" +
                "        </div>\n" +
                "        \n" +
                "        <!-- Footer -->\n" +
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

    // Alternative: Simple text email for backup
    public void sendPasswordResetEmailPlainText(String email, String resetToken) {
        String resetLink = frontendUrl + "/forgot-password?token=" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Reset Your Password - Randa Real Estate");
        message.setFrom("noreply@randarealestate.com");
        message.setText("Hello,\n\n" +
                "We received a request to reset your password for your Randa Real Estate account.\n\n" +
                "Click the link below to reset your password:\n" +
                resetLink + "\n\n" +
                "This link will expire in 1 hour.\n\n" +
                "If you didn't request this, you can safely ignore this email.\n\n" +
                "For security reasons, never share this link with anyone.\n\n" +
                "Best regards,\n" +
                "Randa Real Estate Team");

        mailSender.send(message);
    }

    // Verify email exists in database before sending reset email
    public boolean emailExists(String email) {
        // This will be checked in AdminService before calling this method
        return true;
    }
}