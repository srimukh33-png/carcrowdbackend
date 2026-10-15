package com.carcrowd.dto;

public class InterestResponseDTO {
    private boolean success;
    private String message;
    private Long carId;
    private String carModel;
    private long currentBuyerCount;
    private boolean duplicate;

    public InterestResponseDTO() {}

    public InterestResponseDTO(boolean success, String message, Long carId, String carModel,
                               long currentBuyerCount, boolean duplicate) {
        this.success = success;
        this.message = message;
        this.carId = carId;
        this.carModel = carModel;
        this.currentBuyerCount = currentBuyerCount;
        this.duplicate = duplicate;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public long getCurrentBuyerCount() {
        return currentBuyerCount;
    }

    public void setCurrentBuyerCount(long currentBuyerCount) {
        this.currentBuyerCount = currentBuyerCount;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }
}
