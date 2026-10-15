package com.carcrowd.dto;

public class BrandDTO {
    private Long id;
    private String name;
    private String logoUrl;
    private boolean active;
    private long carCount;

    public BrandDTO() {}

    public BrandDTO(Long id, String name, String logoUrl, boolean active, long carCount) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.active = active;
        this.carCount = carCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getCarCount() {
        return carCount;
    }

    public void setCarCount(long carCount) {
        this.carCount = carCount;
    }
}
