package plot.plot.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import plot.plot.Service.CloudinaryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ImageController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            if (image.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select an image to upload");
            }

            String imageUrl = cloudinaryService.uploadImage(image);

            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Image uploaded successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading image: " + e.getMessage());
        }
    }

    @PostMapping("/upload-multiple")
    public ResponseEntity<?> uploadMultipleImages(@RequestParam("images") List<MultipartFile> images) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            if (images.isEmpty()) {
                return ResponseEntity.badRequest().body("Please select images to upload");
            }

            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = cloudinaryService.uploadImage(image);
                    imageUrls.add(imageUrl);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("imageUrls", imageUrls);
            response.put("message", "Images uploaded successfully");
            response.put("count", imageUrls.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error uploading images: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteImage(@RequestParam("publicId") String publicId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).body("Unauthorized");
            }

            cloudinaryService.deleteImage(publicId);
            return ResponseEntity.ok("Image deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting image: " + e.getMessage());
        }
    }
}
