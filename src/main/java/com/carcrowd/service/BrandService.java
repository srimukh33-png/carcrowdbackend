package com.carcrowd.service;

import com.carcrowd.dto.BrandDTO;
import com.carcrowd.entity.CarBrand;
import com.carcrowd.exception.ResourceNotFoundException;
import com.carcrowd.repository.CarBrandRepository;
import com.carcrowd.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BrandService {

    private final CarBrandRepository brandRepository;
    private final CarRepository carRepository;

    public BrandService(CarBrandRepository brandRepository, CarRepository carRepository) {
        this.brandRepository = brandRepository;
        this.carRepository = carRepository;
    }

    public List<BrandDTO> getActiveBrands() {
        return brandRepository.findAllByActiveTrueOrderByNameAsc()
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    public BrandDTO getBrandById(Long brandId) {
        CarBrand brand = brandRepository.findById(brandId)
            .filter(CarBrand::isActive)
            .orElseThrow(() -> new ResourceNotFoundException("Brand not found with ID: " + brandId));
        return mapToDTO(brand);
    }

    private BrandDTO mapToDTO(CarBrand brand) {
        long carCount = carRepository.findByBrandIdAndActiveTrueOrderByModelNameAsc(brand.getId()).size();
        return new BrandDTO(
            brand.getId(),
            brand.getName(),
            brand.getLogoUrl(),
            brand.isActive(),
            carCount
        );
    }
}
