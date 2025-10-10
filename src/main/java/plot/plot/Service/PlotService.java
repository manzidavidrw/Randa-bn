package plot.plot.Service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import plot.plot.Repository.PlotRepository;
import plot.plot.dto.PlotRequest;
import plot.plot.model.Admin;
import plot.plot.model.plot;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PlotService {

    @Autowired
    private PlotRepository plotRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    public plot createPlot(PlotRequest plotRequest, Admin admin, List<MultipartFile> images) throws IOException {
        plot plot = new plot();
        plot.setTitle(plotRequest.getTitle());
        plot.setDescription(plotRequest.getDescription());
        plot.setPrice(plotRequest.getPrice());
        plot.setLocation(plotRequest.getLocation());
        plot.setSize(plotRequest.getSize());
        plot.setPlotType(plotRequest.getPlotType());
        plot.setAdmin(admin);

        // Upload images to Cloudinary
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = cloudinaryService.uploadImage(image);
                    imageUrls.add(imageUrl);
                }
            }
        }
        plot.setImageUrls(imageUrls);

        return plotRepository.save(plot);
    }

    public plot updatePlot(Long plotId, PlotRequest plotRequest, List<MultipartFile> newImages) throws IOException {
        plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new RuntimeException("Plot not found"));

        plot.setTitle(plotRequest.getTitle());
        plot.setDescription(plotRequest.getDescription());
        plot.setPrice(plotRequest.getPrice());
        plot.setLocation(plotRequest.getLocation());
        plot.setSize(plotRequest.getSize());
        plot.setPlotType(plotRequest.getPlotType());

        // Add new images if provided
        if (newImages != null && !newImages.isEmpty()) {
            List<String> currentImages = plot.getImageUrls() != null ?
                    new ArrayList<>(plot.getImageUrls()) : new ArrayList<>();

            for (MultipartFile image : newImages) {
                if (!image.isEmpty()) {
                    String imageUrl = cloudinaryService.uploadImage(image);
                    currentImages.add(imageUrl);
                }
            }
            plot.setImageUrls(currentImages);
        }

        return plotRepository.save(plot);
    }

    public List<plot> getAllPlots() {
        return plotRepository.findAllOrderByCreatedAtDesc();
    }

    public Optional<plot> getPlotById(Long id) {
        return plotRepository.findById(id);
    }

    public List<plot> getPlotsByAdmin(Long adminId) {
        return plotRepository.findByAdminId(adminId);
    }

    public List<plot> searchPlotsByTitle(String title) {
        return plotRepository.findByTitleContainingIgnoreCase(title);
    }

    public List<plot> searchPlotsByLocation(String location) {
        return plotRepository.findByLocationContainingIgnoreCase(location);
    }

    public List<plot> getPlotsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return plotRepository.findByPriceRange(minPrice, maxPrice);
    }

    public void deletePlot(Long plotId) {
        plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new RuntimeException("Plot not found"));
        plotRepository.delete(plot);
    }

    public List<plot> searchPlots(String title, String location, BigDecimal minPrice, BigDecimal maxPrice, String plotType) {
        List<plot> allPlots = plotRepository.findAll();

        return allPlots.stream()
                .filter(p -> title == null || p.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(p -> location == null || p.getLocation().toLowerCase().contains(location.toLowerCase()))
                .filter(p -> minPrice == null || p.getPrice().compareTo(minPrice) >= 0)
                .filter(p -> maxPrice == null || p.getPrice().compareTo(maxPrice) <= 0)
                .filter(p -> plotType == null || p.getPlotType().equalsIgnoreCase(plotType))
                .toList();
    }

}
