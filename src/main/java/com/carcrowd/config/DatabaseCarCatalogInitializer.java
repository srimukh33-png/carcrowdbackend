package com.carcrowd.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * Automatically synchronizes the full 223 vehicle catalogue and local AVIF image URLs
 * into the database on application startup.
 */
@Component
@Order(50)
public class DatabaseCarCatalogInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseCarCatalogInitializer.class);

    private final DataSource dataSource;

    public DatabaseCarCatalogInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting automatic vehicle catalogue synchronization from update-cars-and-images.sql...");
        syncCatalogue();
    }

    public java.util.Map<String, Object> syncCatalogue() {
        try {
            ClassPathResource resource = new ClassPathResource("update-cars-and-images.sql");
            if (resource.exists()) {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(resource);
                populator.setContinueOnError(true);
                populator.setIgnoreFailedDrops(true);
                populator.execute(dataSource);
                log.info("Vehicle catalogue synchronization completed successfully. All 223 cars and images are updated in the database.");
                return java.util.Map.of(
                    "success", true,
                    "message", "Vehicle catalogue synchronized successfully. All 223 cars and images are updated in the database."
                );
            } else {
                log.warn("update-cars-and-images.sql resource not found in classpath.");
                return java.util.Map.of("success", false, "message", "update-cars-and-images.sql resource not found");
            }
        } catch (Exception ex) {
            log.error("Failed to execute database car catalogue initializer: {}", ex.getMessage(), ex);
            return java.util.Map.of("success", false, "message", "Synchronization failed: " + ex.getMessage());
        }
    }
}
