package com.carcrowd.dto.admin;

public class DeleteCarResponse {

    private Long carId;
    private String modelName;
    private String brandName;
    private boolean physicallyDeleted;
    private boolean deactivated;
    private long referencedLeadsCount;
    private String message;

    public DeleteCarResponse() {}

    public DeleteCarResponse(Long carId, String modelName, String brandName,
                             boolean physicallyDeleted, boolean deactivated,
                             long referencedLeadsCount, String message) {
        this.carId = carId;
        this.modelName = modelName;
        this.brandName = brandName;
        this.physicallyDeleted = physicallyDeleted;
        this.deactivated = deactivated;
        this.referencedLeadsCount = referencedLeadsCount;
        this.message = message;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public boolean isPhysicallyDeleted() {
        return physicallyDeleted;
    }

    public void setPhysicallyDeleted(boolean physicallyDeleted) {
        this.physicallyDeleted = physicallyDeleted;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public void setDeactivated(boolean deactivated) {
        this.deactivated = deactivated;
    }

    public long getReferencedLeadsCount() {
        return referencedLeadsCount;
    }

    public void setReferencedLeadsCount(long referencedLeadsCount) {
        this.referencedLeadsCount = referencedLeadsCount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
