package plot.plot.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import plot.plot.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PlotRepository extends JpaRepository<plot, Long> {
    List<plot> findByAdminId(Long adminId);
    List<plot> findByTitleContainingIgnoreCase(String title);
    List<plot> findByLocationContainingIgnoreCase(String location);

    @Query("SELECT p FROM plot p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    List<plot> findByPriceRange(@Param("minPrice") BigDecimal minPrice,
                                @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT p FROM plot p ORDER BY p.createdAt DESC")
    List<plot> findAllOrderByCreatedAtDesc();
}
