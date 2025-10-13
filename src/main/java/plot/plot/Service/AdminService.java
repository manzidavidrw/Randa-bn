package plot.plot.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import plot.plot.Repository.AdminRepository;
import plot.plot.model.Admin;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class AdminService implements UserDetailsService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Admin registerAdmin(Admin admin) {
        if (adminRepository.existsByUsername(admin.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (adminRepository.existsByEmail(admin.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        return adminRepository.save(admin);
    }

    public Admin findByUsername(String username) {
        return adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new User(admin.getUsername(), admin.getPassword(), new ArrayList<>());
    }

    public boolean changePassword(Long adminId, String currentPassword, String newPassword) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, admin.getPassword())) {
            return false; // Wrong current password
        }

        // Encode new password
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepository.save(admin);
        return true;
    }

    public Admin updateAdmin(Admin admin) {
        return adminRepository.save(admin);
    }

    public void initiatePasswordReset(String email) {
        // Find the admin by email
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        // Generate a reset token and expiry
        String resetToken = UUID.randomUUID().toString();
        admin.setResetToken(resetToken);
        admin.setResetTokenExpiry(LocalDateTime.now().plusHours(1));

        // Save changes to the admin
        adminRepository.save(admin);

        // Send the password reset email with email, username, and reset token
        emailService.sendPasswordResetEmail(
                admin.getEmail(),        // recipient email
                admin.getUsername(),     // recipient name for greeting
                resetToken               // reset token
        );
    }

    public boolean resetPassword(String token, String newPassword) {
        Admin admin = adminRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));

        if (admin.getResetTokenExpiry() == null ||
                admin.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }

        admin.setPassword(passwordEncoder.encode(newPassword));
        admin.setResetToken(null);
        admin.setResetTokenExpiry(null);

        adminRepository.save(admin);
        return true;
    }

    public boolean validateResetToken(String token) {
        Admin admin = adminRepository.findByResetToken(token).orElse(null);

        if (admin == null) {
            return false;
        }

        if (admin.getResetTokenExpiry() == null ||
                admin.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }
}