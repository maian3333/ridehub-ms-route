package com.ridehub.route.service.dto.request;

import com.ridehub.route.domain.enumeration.Gender;
import com.ridehub.route.domain.enumeration.StaffStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * A simplified DTO for creating/updating drivers without requiring staff management.
 */
public class SimpleDriverRequestDTO implements Serializable {

    // Driver-specific fields
    private String licenseClass;
    
    private Integer yearsExperience;

    // Staff fields that will be automatically managed
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    
    private Integer age;
    
    private Gender gender;
    
    private String phoneNumber;
    
    private StaffStatus status;

    // Constructors
    public SimpleDriverRequestDTO() {}

    public SimpleDriverRequestDTO(String name, String licenseClass, Integer yearsExperience) {
        this.name = name;
        this.licenseClass = licenseClass;
        this.yearsExperience = yearsExperience;
    }

    // Getters and Setters
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
        return "SimpleDriverRequestDTO{" +
            "licenseClass='" + licenseClass + '\'' +
            ", yearsExperience=" + yearsExperience +
            ", name='" + name + '\'' +
            ", age=" + age +
            ", gender=" + gender +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", status=" + status +
            '}';
    }
}
