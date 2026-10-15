package com.carcrowd.dto.admin;

public class PopularCarDemandDTO {
    private Long carId;
    private String brandName;
    private String modelName;
    private long buyerCount;

    public PopularCarDemandDTO() {}

    public PopularCarDemandDTO(Long carId, String brandName, String modelName, long buyerCount) {
        this.carId = carId;
        this.brandName = brandName;
        this.modelName = modelName;
        this.buyerCount = buyerCount;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
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

    public long getBuyerCount() {
        return buyerCount;
    }

    public void setBuyerCount(long buyerCount) {
        this.buyerCount = buyerCount;
    }
}
