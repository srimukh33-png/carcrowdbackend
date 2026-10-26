package com.carcrowd.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateCarRequest {

    @NotNull(message = "Car brand is required")
    private Long brandId;

    @NotBlank(message = "Model name is required")
    @Size(min = 2, max = 100, message = "Model name must be between 2 and 100 characters")
    private String modelName;

    @NotBlank(message = "Image URL is required")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    @Size(max = 50, message = "Body type cannot exceed 50 characters")
    private String bodyType;

    private String description;

    @Size(max = 100, message = "Fuel type cannot exceed 100 characters")
    private String fuelType;

    @Size(max = 100, message = "Transmission cannot exceed 100 characters")
    private String transmission;

    @Size(max = 50, message = "Seating capacity cannot exceed 50 characters")
    private String seating;

    private Boolean active = true;

    public CreateCarRequest() {}

    public CreateCarRequest(Long brandId, String modelName, String imageUrl, String bodyType,
                            String description, String fuelType, String transmission,
                            String seating, Boolean active) {
        this.brandId = brandId;
        this.modelName = modelName;
        this.imageUrl = imageUrl;
        this.bodyType = bodyType;
        this.description = description;
        this.fuelType = fuelType;
        this.transmission = transmission;
        this.seating = seating;
        this.active = active != null ? active : true;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getSeating() {
        return seating;
    }

    public void setSeating(String seating) {
        this.seating = seating;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
