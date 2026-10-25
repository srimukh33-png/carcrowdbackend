package com.carcrowd;

import com.carcrowd.config.DataSeeder;
import com.carcrowd.entity.Car;
import com.carcrowd.entity.CarBrand;
import com.carcrowd.repository.CarBrandRepository;
import com.carcrowd.repository.CarRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class BrandSeederTest {

    @Autowired
    private CarBrandRepository brandRepository;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private DataSeeder dataSeeder;

    @Test
    @DisplayName("Verify that initial seeding inserted the expected default brands")
    public void testInitialSeededBrands() {
        assertTrue(brandRepository.existsByName("Tata"), "Tata should be seeded");
        assertTrue(brandRepository.existsByName("Maruti Suzuki"), "Maruti Suzuki should be seeded");
        assertTrue(brandRepository.existsByName("Hyundai"), "Hyundai should be seeded");
        assertTrue(brandRepository.existsByName("Mahindra"), "Mahindra should be seeded");

        long brandCount = brandRepository.count();
        assertTrue(brandCount >= 20, "At least 20 brands should be present");
    }

    @Test
    @DisplayName("Verify existsByName and existsByNameIgnoreCase functionality")
    public void testExistsByNameAndCaseInsensitive() {
        assertTrue(brandRepository.existsByName("Tata"));
        assertTrue(brandRepository.existsByNameIgnoreCase("tata"));
        assertTrue(brandRepository.existsByNameIgnoreCase("TATA"));
        assertTrue(brandRepository.existsByNameIgnoreCase("TaTa"));

        assertFalse(brandRepository.existsByName("NonExistentBrand12345"));
        assertFalse(brandRepository.existsByNameIgnoreCase("nonexistentbrand12345"));
    }

    @Test
    @Transactional
    @DisplayName("Running the seeder multiple times does not insert duplicate brands")
    public void testSeederIdempotency() {
        long initialBrandCount = brandRepository.count();
        long initialCarCount = carRepository.count();

        // Run seeder second time
        dataSeeder.run();

        assertEquals(initialBrandCount, brandRepository.count(), "Brand count must remain unchanged after second run");
        assertEquals(initialCarCount, carRepository.count(), "Car count must remain unchanged after second run");

        // Run seeder third time
        dataSeeder.run();

        assertEquals(initialBrandCount, brandRepository.count(), "Brand count must remain unchanged after third run");
        assertEquals(initialCarCount, carRepository.count(), "Car count must remain unchanged after third run");
    }

    @Test
    @Transactional
    @DisplayName("A missing brand is inserted properly")
    public void testInsertMissingBrand() {
        String newBrandName = "TestLucidMotors";
        assertFalse(brandRepository.existsByNameIgnoreCase(newBrandName));

        CarBrand newBrand = brandRepository.save(new CarBrand(newBrandName, null, true));
        assertNotNull(newBrand.getId());
        assertTrue(brandRepository.existsByName(newBrandName));
        assertTrue(brandRepository.existsByNameIgnoreCase("testlucidmotors"));
    }

    @Test
    @Transactional
    @DisplayName("Case and whitespace variations are identified as existing and do not create duplicates")
    public void testCaseAndWhitespaceHandling() {
        String testBrandName = "Volvo";
        assertTrue(brandRepository.existsByNameIgnoreCase(testBrandName));

        // When normalizing "  volvo  " -> trimmed to "volvo"
        String rawInput = "  volvo  ";
        String normalized = rawInput.trim();

        Optional<CarBrand> found = brandRepository.findByNameIgnoreCase(normalized);
        assertTrue(found.isPresent(), "Brand should be found case-insensitively");
        assertEquals("Volvo", found.get().getName(), "Preferred display capitalization should be preserved");
    }

    @Test
    @Transactional
    @DisplayName("Database unique constraint prevents duplicate brand names with exact or conflicting names")
    public void testUniqueConstraintEnforcement() {
        CarBrand duplicateBrand = new CarBrand("Tata", null, true);
        assertThrows(DataIntegrityViolationException.class, () -> {
            brandRepository.saveAndFlush(duplicateBrand);
        }, "Inserting a duplicate brand name must violate unique constraint");
    }

    @Test
    @DisplayName("Existing car-to-brand relationships remain intact after seeding")
    public void testCarBrandRelationshipsIntact() {
        Optional<CarBrand> tataBrand = brandRepository.findByNameIgnoreCase("Tata");
        assertTrue(tataBrand.isPresent());

        List<Car> tataCars = carRepository.findByBrandIdAndActiveTrueOrderByModelNameAsc(tataBrand.get().getId());
        assertFalse(tataCars.isEmpty(), "Tata should have associated cars");

        for (Car car : tataCars) {
            assertEquals(tataBrand.get().getId(), car.getBrand().getId(), "Car must point to Tata brand ID");
            assertEquals("Tata", car.getBrand().getName(), "Car's brand name must be Tata");
        }
    }
}
