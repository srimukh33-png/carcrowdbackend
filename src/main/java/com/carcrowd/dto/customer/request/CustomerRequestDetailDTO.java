package com.carcrowd.dto.customer.request;

import com.carcrowd.entity.LeadStatus;
import java.time.LocalDateTime;
import java.util.List;

public class CustomerRequestDetailDTO extends CustomerRequestSummaryDTO {

    private String fullName;
    private String phoneNumber;
    private String email;
    private Integer age;
    private String purchaseTimeline;
    private List<StatusTimelineEventDTO> timeline;

    public CustomerRequestDetailDTO() {}

    public CustomerRequestDetailDTO(Long id, Long carId, Long brandId, String brandName,
                                   String modelName, String imageUrl, String city,
                                   LocalDateTime joinedDate, LocalDateTime lastUpdatedDate,
                                   LeadStatus status, String statusLabel, String statusMessage,
                                   String nextStep, String fullName, String phoneNumber,
                                   String email, Integer age, String purchaseTimeline,
                                   List<StatusTimelineEventDTO> timeline) {
        super(id, carId, brandId, brandName, modelName, imageUrl, city, joinedDate,
              lastUpdatedDate, status, statusLabel, statusMessage, nextStep);
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.age = age;
        this.purchaseTimeline = purchaseTimeline;
        this.timeline = timeline;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getPurchaseTimeline() {
        return purchaseTimeline;
    }

    public void setPurchaseTimeline(String purchaseTimeline) {
        this.purchaseTimeline = purchaseTimeline;
    }

    public List<StatusTimelineEventDTO> getTimeline() {
        return timeline;
    }

    public void setTimeline(List<StatusTimelineEventDTO> timeline) {
        this.timeline = timeline;
    }
}
