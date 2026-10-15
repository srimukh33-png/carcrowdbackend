package com.carcrowd.dto.admin;

import java.util.Map;

public class CarDemandDTO {
    private Long carId;
    private Long brandId;
    private String brandName;
    private String modelName;
    private long totalInterestedBuyers;
    private Map<String, Long> cityDemand;
    private long newLast7Days;
    private long newLast30Days;

    public CarDemandDTO() {}

    public CarDemandDTO(Long carId, Long brandId, String brandName, String modelName,
                        long totalInterestedBuyers, Map<String, Long> cityDemand,
                        long newLast7Days, long newLast30Days) {
        this.carId = carId;
        this.brandId = brandId;
        this.brandName = brandName;
        this.modelName = modelName;
        this.totalInterestedBuyers = totalInterestedBuyers;
        this.cityDemand = cityDemand;
        this.newLast7Days = newLast7Days;
        this.newLast30Days = newLast30Days;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public long getTotalInterestedBuyers() {
        return totalInterestedBuyers;
    }

    public void setTotalInterestedBuyers(long totalInterestedBuyers) {
        this.totalInterestedBuyers = totalInterestedBuyers;
    }

    public Map<String, Long> getCityDemand() {
        return cityDemand;
    }

    public void setCityDemand(Map<String, Long> cityDemand) {
        this.cityDemand = cityDemand;
    }

    public long getNewLast7Days() {
        return newLast7Days;
    }

    public void setNewLast7Days(long newLast7Days) {
        this.newLast7Days = newLast7Days;
    }

    public long getNewLast30Days() {
        return newLast30Days;
    }

    public void setNewLast30Days(long newLast30Days) {
        this.newLast30Days = newLast30Days;
    }
}
