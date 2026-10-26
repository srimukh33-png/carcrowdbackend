package com.carcrowd.service;

import com.carcrowd.dto.BrandDTO;
import com.carcrowd.dto.admin.CarAdminDTO;
import com.carcrowd.dto.admin.CreateCarRequest;
import com.carcrowd.dto.admin.DeleteCarResponse;
import com.carcrowd.dto.admin.UpdateCarRequest;
import com.carcrowd.entity.Car;
import com.carcrowd.entity.CarBrand;
import com.carcrowd.exception.BadRequestException;
import com.carcrowd.exception.ResourceNotFoundException;
import com.carcrowd.repository.CarBrandRepository;
import com.carcrowd.repository.CarRepository;
import com.carcrowd.repository.InterestRegistrationRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminCarService {

    private static final Logger log = LoggerFactory.getLogger(AdminCarService.class);

    private final CarRepository carRepository;
    private final CarBrandRepository brandRepository;
    private final InterestRegistrationRepository interestRepository;

    public AdminCarService(CarRepository carRepository,
                           CarBrandRepository brandRepository,
                           InterestRegistrationRepository interestRepository) {
        this.carRepository = carRepository;
        this.brandRepository = brandRepository;
        this.interestRepository = interestRepository;
    }

    @Transactional(readOnly = true)
    public Page<CarAdminDTO> getCars(String search, Long brandId, Boolean active,
                                     int page, int size, String sortBy, String sortDirection) {
        // Normalize sorting properties
        String sortProperty = switch (sortBy != null ? sortBy.toLowerCase() : "createdat") {
            case "model", "modelname" -> "modelName";
            case "brand", "brandname" -> "brand.name";
            case "bodytype" -> "bodyType";
            case "active" -> "active";
            default -> "createdAt";
        };

        Sort sort = "asc".equalsIgnoreCase(sortDirection)
                ? Sort.by(sortProperty).ascending()
                : Sort.by(sortProperty).descending();

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

        Specification<Car> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (brandId != null) {
                predicates.add(cb.equal(root.get("brand").get("id"), brandId));
            }

            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }

            if (search != null && !search.trim().isEmpty()) {
                String term = "%" + search.trim().toLowerCase() + "%";
                Join<Car, CarBrand> brandJoin = root.join("brand", JoinType.LEFT);
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("modelName")), term),
                        cb.like(cb.lower(brandJoin.get("name")), term),
                        cb.like(cb.lower(root.get("bodyType")), term)
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Car> carPage = carRepository.findAll(spec, pageable);
        return carPage.map(this::mapToAdminDTO);
    }

    @Transactional(readOnly = true)
    public CarAdminDTO getCarById(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car model not found with ID: " + id));
        return mapToAdminDTO(car);
    }

    public CarAdminDTO createCar(CreateCarRequest request, String adminEmail) {
        if (request == null) {
            throw new BadRequestException("Car request payload cannot be empty");
        }

        CarBrand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Automotive brand not found with ID: " + request.getBrandId()));

        String normalizedModel = request.getModelName().trim();

        // Prevent duplicate model name under the same brand
        Optional<Car> existing = carRepository.findByBrandIdAndModelNameIgnoreCase(brand.getId(), normalizedModel);
        if (existing.isPresent()) {
            throw new BadRequestException(String.format("Car model '%s' already exists for %s", normalizedModel, brand.getName()));
        }

        Car car = new Car();
        car.setBrand(brand);
        car.setModelName(normalizedModel);
        car.setImageUrl(request.getImageUrl().trim());
        car.setBodyType(request.getBodyType() != null && !request.getBodyType().isBlank() ? request.getBodyType().trim() : "SUV");
        car.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        car.setFuelType(request.getFuelType() != null && !request.getFuelType().isBlank() ? request.getFuelType().trim() : "Petrol");
        car.setTransmission(request.getTransmission() != null && !request.getTransmission().isBlank() ? request.getTransmission().trim() : "Manual");
        car.setSeating(request.getSeating() != null && !request.getSeating().isBlank() ? request.getSeating().trim() : "5 Seater");
        car.setActive(request.getActive() != null ? request.getActive() : true);
        car.setCreatedAt(LocalDateTime.now());

        Car saved = carRepository.save(car);
        log.info("Car created: ID={}, model='{}', brand='{}' by admin={}", saved.getId(), saved.getModelName(), brand.getName(), adminEmail);

        return mapToAdminDTO(saved);
    }

    public CarAdminDTO updateCar(Long id, UpdateCarRequest request, String adminEmail) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car model not found with ID: " + id));

        CarBrand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Automotive brand not found with ID: " + request.getBrandId()));

        String normalizedModel = request.getModelName().trim();

        // Check duplicate if model name or brand changed
        Optional<Car> duplicate = carRepository.findByBrandIdAndModelNameIgnoreCase(brand.getId(), normalizedModel);
        if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
            throw new BadRequestException(String.format("Car model '%s' already exists for %s", normalizedModel, brand.getName()));
        }

        car.setBrand(brand);
        car.setModelName(normalizedModel);
        car.setImageUrl(request.getImageUrl().trim());
        if (request.getBodyType() != null && !request.getBodyType().isBlank()) {
            car.setBodyType(request.getBodyType().trim());
        }
        if (request.getDescription() != null) {
            car.setDescription(request.getDescription().trim());
        }
        if (request.getFuelType() != null && !request.getFuelType().isBlank()) {
            car.setFuelType(request.getFuelType().trim());
        }
        if (request.getTransmission() != null && !request.getTransmission().isBlank()) {
            car.setTransmission(request.getTransmission().trim());
        }
        if (request.getSeating() != null && !request.getSeating().isBlank()) {
            car.setSeating(request.getSeating().trim());
        }
        if (request.getActive() != null) {
            car.setActive(request.getActive());
        }

        Car saved = carRepository.save(car);
        log.info("Car updated: ID={}, model='{}', brand='{}' by admin={}", saved.getId(), saved.getModelName(), brand.getName(), adminEmail);

        return mapToAdminDTO(saved);
    }

    public CarAdminDTO updateCarStatus(Long id, boolean active, String adminEmail) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car model not found with ID: " + id));

        car.setActive(active);
        Car saved = carRepository.save(car);
        log.info("Car status updated: ID={}, active={} by admin={}", saved.getId(), active, adminEmail);

        return mapToAdminDTO(saved);
    }

    public DeleteCarResponse deleteCar(Long id, String adminEmail) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Car model not found with ID: " + id));

        String modelName = car.getModelName();
        String brandName = car.getBrand().getName();

        // Verify if car has dependent customer registrations
        long leadCount = interestRepository.countByCarId(id);

        if (leadCount > 0) {
            // Safe deactivation / soft deletion to preserve historical customer records
            car.setActive(false);
            carRepository.save(car);
            log.info("Car ID={} ('{}') has {} customer references. Safely deactivated to preserve data by admin={}",
                    id, modelName, leadCount, adminEmail);

            return new DeleteCarResponse(
                    id,
                    modelName,
                    brandName,
                    false,
                    true,
                    leadCount,
                    String.format("Car '%s' has %d customer request reference(s). To preserve historical data, it was safely deactivated and removed from the public catalogue.", modelName, leadCount)
            );
        }

        // Controlled physical deletion when no customer records reference this car
        carRepository.delete(car);
        log.info("Car ID={} ('{}') physically deleted by admin={}", id, modelName, adminEmail);

        return new DeleteCarResponse(
                id,
                modelName,
                brandName,
                true,
                false,
                0,
                String.format("Car '%s' was successfully deleted from the catalogue.", modelName)
        );
    }

    @Transactional(readOnly = true)
    public List<BrandDTO> getAllBrands() {
        return brandRepository.findAllByActiveTrueOrderByNameAsc()
                .stream()
                .map(b -> new BrandDTO(b.getId(), b.getName(), b.getLogoUrl(), b.isActive(), 0))
                .collect(Collectors.toList());
    }

    private CarAdminDTO mapToAdminDTO(Car car) {
        long buyerCount = interestRepository.countActiveByCarId(car.getId());
        long totalRegistrations = interestRepository.countByCarId(car.getId());
        return new CarAdminDTO(
                car.getId(),
                car.getBrand().getId(),
                car.getBrand().getName(),
                car.getModelName(),
                car.getImageUrl(),
                car.getBodyType(),
                car.getDescription(),
                car.getFuelType(),
                car.getTransmission(),
                car.getSeating(),
                car.isActive(),
                car.getCreatedAt(),
                buyerCount,
                totalRegistrations
        );
    }
}
