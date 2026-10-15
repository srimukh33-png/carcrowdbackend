package com.carcrowd.dto;

public class CarSummaryDTO {
    private Long id;
    private Long brandId;
    private String brandName;
    private String modelName;
    private String imageUrl;
    private String bodyType;
    private long buyerCount;

    public CarSummaryDTO() {}

    public CarSummaryDTO(Long id, Long brandId, String brandName, String modelName,
                         String imageUrl, String bodyType, long buyerCount) {
        this.id = id;
        this.brandId = brandId;
        this.brandName = brandName;
        this.modelName = modelName;
        this.imageUrl = imageUrl;
        this.bodyType = bodyType;
        this.buyerCount = buyerCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public long getBuyerCount() {
        return buyerCount;
    }

    public void setBuyerCount(long buyerCount) {
        this.buyerCount = buyerCount;
    }
}
