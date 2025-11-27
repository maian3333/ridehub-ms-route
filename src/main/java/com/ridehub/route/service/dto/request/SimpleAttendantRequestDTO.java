package com.ridehub.route.service.dto.request;

import com.ridehub.route.domain.enumeration.Gender;
import com.ridehub.route.domain.enumeration.StaffStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

/**
 * A simplified DTO for creating/updating attendants without requiring staff management.
 */
public class SimpleAttendantRequestDTO implements Serializable {

    // Staff fields that will be automatically managed
    @NotNull
    @Size(min = 1, max = 255)
    private String name;
    
    private Integer age;
    
    private Gender gender;
    
    private String phoneNumber;
    
    private StaffStatus status;

    // Constructors
    public SimpleAttendantRequestDTO() {}

    public SimpleAttendantRequestDTO(String name) {
        this.name = name;
    }

    // Getters and Setters
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
        return "SimpleAttendantRequestDTO{" +
            "name='" + name + '\'' +
            ", age=" + age +
            ", gender=" + gender +
            ", phoneNumber='" + phoneNumber + '\'' +
            ", status=" + status +
            '}';
    }
}
