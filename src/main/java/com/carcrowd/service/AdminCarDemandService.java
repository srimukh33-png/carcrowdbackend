package com.carcrowd.service;

import com.carcrowd.dto.admin.CarDemandDTO;
import com.carcrowd.dto.admin.LeadSummaryDTO;
import com.carcrowd.entity.Car;
import com.carcrowd.entity.InterestRegistration;
import com.carcrowd.entity.LeadStatus;
import com.carcrowd.exception.ResourceNotFoundException;
import com.carcrowd.repository.CarRepository;
import com.carcrowd.repository.InterestRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class AdminCarDemandService {

    private final CarRepository carRepository;
    private final InterestRegistrationRepository registrationRepository;

    public AdminCarDemandService(CarRepository carRepository,
                                 InterestRegistrationRepository registrationRepository) {
        this.carRepository = carRepository;
        this.registrationRepository = registrationRepository;
    }

    @Transactional(readOnly = true)
    public List<CarDemandDTO> getCarDemandAnalytics(String brand, String model, String city, String sortBy) {
        List<Car> cars = carRepository.findAllByActiveTrueOrderByModelNameAsc();
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        List<CarDemandDTO> demandList = new ArrayList<>();

        for (Car car : cars) {
            String bName = car.getBrand() != null ? car.getBrand().getName() : "";
            String mName = car.getModelName();

            // Apply brand filter if specified
            if (StringUtils.hasText(brand) && !bName.equalsIgnoreCase(brand.trim())) {
                continue;
            }

            // Apply model filter if specified
            if (StringUtils.hasText(model) && !mName.toLowerCase().contains(model.trim().toLowerCase())) {
                continue;
            }

            long totalCount = registrationRepository.countActiveByCarId(car.getId());
            long last7Count = registrationRepository.countActiveByCarIdAndCreatedAtAfter(car.getId(), sevenDaysAgo);
            long last30Count = registrationRepository.countActiveByCarIdAndCreatedAtAfter(car.getId(), thirtyDaysAgo);

            // City demand
            List<Object[]> cityBreakdown = registrationRepository.countCityDemandForCar(car.getId());
            Map<String, Long> cityMap = new LinkedHashMap<>();
            for (Object[] row : cityBreakdown) {
                String cityName = (String) row[0];
                long cCount = ((Number) row[1]).longValue();
                if (cityName != null && !cityName.isBlank()) {
                    cityMap.put(cityName, cCount);
                }
            }

            // Apply city filter if specified
            if (StringUtils.hasText(city)) {
                String lowerCity = city.trim().toLowerCase();
                boolean matchesCity = cityMap.keySet().stream()
                    .anyMatch(c -> c.toLowerCase().contains(lowerCity));
                if (!matchesCity) {
                    continue;
                }
            }

            demandList.add(new CarDemandDTO(
                car.getId(),
                car.getBrand() != null ? car.getBrand().getId() : null,
                bName,
                mName,
                totalCount,
                cityMap,
                last7Count,
                last30Count
            ));
        }

        // Sorting
        if ("recent".equalsIgnoreCase(sortBy) || "newLast7Days".equalsIgnoreCase(sortBy)) {
            demandList.sort((a, b) -> Long.compare(b.getNewLast7Days(), a.getNewLast7Days()));
        } else if ("newLast30Days".equalsIgnoreCase(sortBy)) {
            demandList.sort((a, b) -> Long.compare(b.getNewLast30Days(), a.getNewLast30Days()));
        } else if ("model".equalsIgnoreCase(sortBy)) {
            demandList.sort(Comparator.comparing(CarDemandDTO::getModelName));
        } else if ("brand".equalsIgnoreCase(sortBy)) {
            demandList.sort(Comparator.comparing(CarDemandDTO::getBrandName));
        } else {
            // Default: sort by totalInterestedBuyers descending
            demandList.sort((a, b) -> Long.compare(b.getTotalInterestedBuyers(), a.getTotalInterestedBuyers()));
        }

        return demandList;
    }

    @Transactional(readOnly = true)
    public List<LeadSummaryDTO> getLeadsForCar(Long carId) {
        if (!carRepository.existsById(carId)) {
            throw new ResourceNotFoundException("Car not found with ID: " + carId);
        }

        return registrationRepository.findAll((root, query, cb) ->
            cb.equal(root.get("car").get("id"), carId)
        ).stream().map(r -> {
            String brandName = (r.getCar() != null && r.getCar().getBrand() != null) ? r.getCar().getBrand().getName() : "";
            String modelName = r.getCar() != null ? r.getCar().getModelName() : "";
            return new LeadSummaryDTO(
                r.getId(),
                r.getFullName(),
                r.getPhoneNumber(),
                r.getEmail(),
                brandName,
                modelName,
                r.getCar() != null ? r.getCar().getId() : null,
                r.getCity(),
                r.getAge(),
                r.getPurchaseTimeline(),
                r.getLeadStatus() != null ? r.getLeadStatus() : LeadStatus.JOINED,
                r.getCreatedAt(),
                r.getNextFollowUpDate(),
                r.getLastContactedAt()
            );
        }).toList();
    }
}
