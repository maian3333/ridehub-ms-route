package com.ridehub.route.service.dto.response;

import com.ridehub.route.domain.enumeration.Gender;
import com.ridehub.route.domain.enumeration.StaffStatus;
import java.io.Serializable;
import java.time.Instant;

/**
 * A simplified DTO for returning driver information without exposing internal staff management.
 */
public class SimpleDriverResponseDTO implements Serializable {

    private Long id;
    
    // Driver-specific fields
    private String licenseClass;
    
    private Integer yearsExperience;
    
    private Instant createdAt;
    
    private Instant updatedAt;

    // Staff fields
    private Long staffId;
    
    private String name;
    
    private Integer age;
    
    private Gender gender;
    
    private String phoneNumber;
    
    private StaffStatus status;

    // Constructors
    public SimpleDriverResponseDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicenseClass() {
        return licenseClass;
    }

    public void setLicenseClass(String licenseClass) {
        this.licenseClass = licenseClass;
    }

    public Integer getYearsExperience() {
        return yearsExperience;
    }

    public void setYearsExperience(Integer yearsExperience) {
        this.yearsExperience = yearsExperience;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StaffStatus getStatus() {
        return status;
    }

    public void setStatus(StaffStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "SimpleDriverResponseDTO{" +
            "id=" + id +
            ", licenseClass='" + licenseClass + '\'' +
            ", yearsExperience=" + yearsExperience +
            ", createdAt=" + createdAt +
            ", updatedAt=" + updatedAt +
            ", staffId=" + staffId +
            ", name='" + name + '\'' +
            ", age=" + age +
            ", gender=" + gender +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", status=" + status +
            '}';
    }
}
