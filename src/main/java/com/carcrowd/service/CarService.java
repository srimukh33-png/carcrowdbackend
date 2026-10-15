package com.carcrowd.service;

import com.carcrowd.dto.CarDetailDTO;
import com.carcrowd.dto.CarSummaryDTO;
import com.carcrowd.entity.Car;
import com.carcrowd.exception.ResourceNotFoundException;
import com.carcrowd.repository.CarRepository;
import com.carcrowd.repository.InterestRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CarService {

    private final CarRepository carRepository;
    private final InterestRegistrationRepository interestRepository;

    public CarService(CarRepository carRepository, InterestRegistrationRepository interestRepository) {
        this.carRepository = carRepository;
        this.interestRepository = interestRepository;
    }

    public List<CarSummaryDTO> getCars(Long brandId) {
        List<Car> cars;
        if (brandId != null) {
            cars = carRepository.findByBrandIdAndActiveTrueOrderByModelNameAsc(brandId);
        } else {
            cars = carRepository.findAllByActiveTrueOrderByModelNameAsc();
        }

        return cars.stream()
            .map(this::mapToSummaryDTO)
            .collect(Collectors.toList());
    }

    public CarDetailDTO getCarDetail(Long carId) {
        Car car = carRepository.findByIdAndActiveTrue(carId)
            .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + carId));
        return mapToDetailDTO(car);
    }

    public Car getCarEntity(Long carId) {
        return carRepository.findByIdAndActiveTrue(carId)
            .orElseThrow(() -> new ResourceNotFoundException("Car not found with ID: " + carId));
    }

    private CarSummaryDTO mapToSummaryDTO(Car car) {
        long buyerCount = interestRepository.countActiveByCarId(car.getId());
        return new CarSummaryDTO(
            car.getId(),
            car.getBrand().getId(),
            car.getBrand().getName(),
            car.getModelName(),
            car.getImageUrl(),
            car.getBodyType(),
            buyerCount
        );
    }

    private CarDetailDTO mapToDetailDTO(Car car) {
        long buyerCount = interestRepository.countActiveByCarId(car.getId());
        return new CarDetailDTO(
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
            buyerCount
        );
    }
}
