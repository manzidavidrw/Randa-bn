package plot.plot.Controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import plot.plot.Service.AdminService;
import plot.plot.Service.PlotService;
import plot.plot.dto.PlotRequest;
import plot.plot.model.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/plots")
@CrossOrigin(origins = "*", maxAge = 3600)
public class PlotController {

    @Autowired
    private PlotService plotService;

    @Autowired
    private AdminService adminService;

    @PostMapping
    public ResponseEntity<?> createPlot(
            @Valid @ModelAttribute PlotRequest plotRequest,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Admin admin = adminService.findByUsername(authentication.getName());

            plot plot = plotService.createPlot(plotRequest, admin, images);
            return ResponseEntity.ok(plot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating plot: " + e.getMessage());
        }
    }

    @GetMapping("/public")
    public ResponseEntity<List<plot>> getAllPlots() {
        List<plot> plots = plotService.getAllPlots();
        return ResponseEntity.ok(plots);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<?> getPlotById(@PathVariable Long id) {
        Optional<plot> plot = plotService.getPlotById(id);
        if (plot.isPresent()) {
            return ResponseEntity.ok(plot.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/my-plots")
    public ResponseEntity<List<plot>> getMyPlots() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Admin admin = adminService.findByUsername(authentication.getName());

        List<plot> plots = plotService.getPlotsByAdmin(admin.getId());
        return ResponseEntity.ok(plots);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlot(
            @PathVariable Long id,
            @Valid @ModelAttribute PlotRequest plotRequest,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Admin admin = adminService.findByUsername(authentication.getName());

            Optional<plot> existingPlot = plotService.getPlotById(id);
            if (existingPlot.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Check if the plot belongs to the authenticated admin
            if (!existingPlot.get().getAdmin().getId().equals(admin.getId())) {
                return ResponseEntity.status(403).body("Access denied");
            }

            plot updatedPlot = plotService.updatePlot(id, plotRequest, images);
            return ResponseEntity.ok(updatedPlot);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating plot: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePlot(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Admin admin = adminService.findByUsername(authentication.getName());

            Optional<plot> existingPlot = plotService.getPlotById(id);
            if (existingPlot.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Check if the plot belongs to the authenticated admin
            if (!existingPlot.get().getAdmin().getId().equals(admin.getId())) {
                return ResponseEntity.status(403).body("Access denied");
            }

            plotService.deletePlot(id);
            return ResponseEntity.ok("Plot deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting plot: " + e.getMessage());
        }
    }

    @GetMapping("/public/search")
    public ResponseEntity<List<plot>> searchPlots(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {

        List<plot> plots;

        if (title != null && !title.isEmpty()) {
            plots = plotService.searchPlotsByTitle(title);
        } else if (location != null && !location.isEmpty()) {
            plots = plotService.searchPlotsByLocation(location);
        } else if (minPrice != null && maxPrice != null) {
            plots = plotService.getPlotsByPriceRange(minPrice, maxPrice);
        } else {
            plots = plotService.getAllPlots();
        }

        return ResponseEntity.ok(plots);
    }
}