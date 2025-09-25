package plot.plot.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import plot.plot.Service.AdminService;
import plot.plot.model.Admin;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Admin admin = adminService.findByUsername(authentication.getName());

            // Return admin without password
            admin.setPassword(null);
            return ResponseEntity.ok(admin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching profile: " + e.getMessage());
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Admin updatedAdmin) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Admin currentAdmin = adminService.findByUsername(authentication.getName());

            // Update allowed fields
            currentAdmin.setEmail(updatedAdmin.getEmail());
            currentAdmin.setPhoneNumber(updatedAdmin.getPhoneNumber());

            Admin savedAdmin = adminService.updateAdmin(currentAdmin);
            savedAdmin.setPassword(null); // Don't return password

            return ResponseEntity.ok(savedAdmin);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating profile: " + e.getMessage());
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Admin admin = adminService.findByUsername(authentication.getName());

            boolean success = adminService.changePassword(admin.getId(),
                    request.getCurrentPassword(), request.getNewPassword());

            if (success) {
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("Current password is incorrect");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error changing password: " + e.getMessage());
        }
    }

    // Inner class for password change request
    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        // Constructors
        public ChangePasswordRequest() {}

        public ChangePasswordRequest(String currentPassword, String newPassword) {
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
        }

        // Getters and Setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }

        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}