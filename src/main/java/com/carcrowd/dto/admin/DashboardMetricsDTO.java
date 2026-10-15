package com.carcrowd.dto.admin;

import java.util.List;
import java.util.Map;

public class DashboardMetricsDTO {
    private long totalLeads;
    private long newLeads;
    private long buyingNext30Days;
    private long carsWithDemand;
    private List<LeadSummaryDTO> recentRegistrations;
    private List<PopularCarDemandDTO> popularCars;
    private Map<String, Long> demandByCity;

    public DashboardMetricsDTO() {}

    public DashboardMetricsDTO(long totalLeads, long newLeads, long buyingNext30Days, long carsWithDemand,
                               List<LeadSummaryDTO> recentRegistrations, List<PopularCarDemandDTO> popularCars,
                               Map<String, Long> demandByCity) {
        this.totalLeads = totalLeads;
        this.newLeads = newLeads;
        this.buyingNext30Days = buyingNext30Days;
        this.carsWithDemand = carsWithDemand;
        this.recentRegistrations = recentRegistrations;
        this.popularCars = popularCars;
        this.demandByCity = demandByCity;
    }

    public long getTotalLeads() {
        return totalLeads;
    }

    public void setTotalLeads(long totalLeads) {
        this.totalLeads = totalLeads;
    }

    public long getNewLeads() {
        return newLeads;
    }

    public void setNewLeads(long newLeads) {
        this.newLeads = newLeads;
    }

    public long getBuyingNext30Days() {
        return buyingNext30Days;
    }

    public void setBuyingNext30Days(long buyingNext30Days) {
        this.buyingNext30Days = buyingNext30Days;
    }

    public long getCarsWithDemand() {
        return carsWithDemand;
    }

    public void setCarsWithDemand(long carsWithDemand) {
        this.carsWithDemand = carsWithDemand;
    }

    public List<LeadSummaryDTO> getRecentRegistrations() {
        return recentRegistrations;
    }

    public void setRecentRegistrations(List<LeadSummaryDTO> recentRegistrations) {
        this.recentRegistrations = recentRegistrations;
    }

    public List<PopularCarDemandDTO> getPopularCars() {
        return popularCars;
    }

    public void setPopularCars(List<PopularCarDemandDTO> popularCars) {
        this.popularCars = popularCars;
    }

    public Map<String, Long> getDemandByCity() {
        return demandByCity;
    }

    public void setDemandByCity(Map<String, Long> demandByCity) {
        this.demandByCity = demandByCity;
    }
}
