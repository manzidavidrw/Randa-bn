package plot.plot.Controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import plot.plot.Config.JwtTokenProvider;
import plot.plot.Service.AdminService;
import plot.plot.dto.*;
import plot.plot.model.Admin;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody loginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);
            Admin admin = adminService.findByUsername(loginRequest.getUsername());

            return ResponseEntity.ok(new AuthResponse(jwt, admin.getUsername(),
                    admin.getEmail(), admin.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            Admin admin = new Admin(
                    registerRequest.getUsername(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    registerRequest.getPhoneNumber()
            );

            Admin savedAdmin = adminService.registerAdmin(admin);

            return ResponseEntity.ok("Admin registered successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            adminService.initiatePasswordReset(request.getEmail());
            return ResponseEntity.ok(new ApiResponse("Password reset email sent successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }

    @PostMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean isValid = adminService.validateResetToken(token);
        if (isValid) {
            return ResponseEntity.ok(new ApiResponse("Token is valid"));
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse("Token is invalid or expired"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            adminService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok(new ApiResponse("Password reset successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse(e.getMessage()));
        }
    }
}