package com.carcrowd.service;

import com.carcrowd.dto.admin.DashboardMetricsDTO;
import com.carcrowd.dto.admin.LeadSummaryDTO;
import com.carcrowd.dto.admin.PopularCarDemandDTO;
import com.carcrowd.entity.InterestRegistration;
import com.carcrowd.entity.LeadStatus;
import com.carcrowd.repository.InterestRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminDashboardService {

    private final InterestRegistrationRepository registrationRepository;

    public AdminDashboardService(InterestRegistrationRepository registrationRepository) {
        this.registrationRepository = registrationRepository;
    }

    @Transactional(readOnly = true)
    public DashboardMetricsDTO getDashboardMetrics() {
        long totalLeads = registrationRepository.count();
        long newLeads = registrationRepository.countByLeadStatus(LeadStatus.JOINED);
        long buyingNext30Days = registrationRepository.countBuyingWithin30Days();
        long carsWithDemand = registrationRepository.countDistinctCarsWithInterest();

        // 1. Recent Registrations
        List<InterestRegistration> recentList = registrationRepository.findTop10ByOrderByCreatedAtDesc();
        List<LeadSummaryDTO> recentDTOs = recentList.stream().map(r -> {
            String brandName = (r.getCar() != null && r.getCar().getBrand() != null) ? r.getCar().getBrand().getName() : "";
            String modelName = r.getCar() != null ? r.getCar().getModelName() : "";
            Long carId = r.getCar() != null ? r.getCar().getId() : null;
            return new LeadSummaryDTO(
                r.getId(),
                r.getFullName(),
                r.getPhoneNumber(),
                r.getEmail(),
                brandName,
                modelName,
                carId,
                r.getCity(),
                r.getAge(),
                r.getPurchaseTimeline(),
                r.getLeadStatus() != null ? r.getLeadStatus() : LeadStatus.JOINED,
                r.getCreatedAt(),
                r.getNextFollowUpDate(),
                r.getLastContactedAt()
            );
        }).toList();

        // 2. Popular Cars (Top 6)
        List<Object[]> popularAgg = registrationRepository.findPopularCarsAggregated();
        List<PopularCarDemandDTO> popularCars = new ArrayList<>();
        int limit = Math.min(popularAgg.size(), 6);
        for (int i = 0; i < limit; i++) {
            Object[] row = popularAgg.get(i);
            Long carId = ((Number) row[0]).longValue();
            String brandName = (String) row[1];
            String modelName = (String) row[2];
            long count = ((Number) row[3]).longValue();
            popularCars.add(new PopularCarDemandDTO(carId, brandName, modelName, count));
        }

        // 3. Demand by City
        List<Object[]> cityAgg = registrationRepository.countRegistrationsByCity();
        Map<String, Long> demandByCity = new LinkedHashMap<>();
        for (Object[] row : cityAgg) {
            String city = (String) row[0];
            long count = ((Number) row[1]).longValue();
            if (city != null && !city.isBlank()) {
                demandByCity.put(city, count);
            }
        }

        return new DashboardMetricsDTO(
            totalLeads,
            newLeads,
            buyingNext30Days,
            carsWithDemand,
            recentDTOs,
            popularCars,
            demandByCity
        );
    }
}
