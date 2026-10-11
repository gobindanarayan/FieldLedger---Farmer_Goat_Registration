package com.farmgoat.registration.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FarmerForm {

    @NotBlank(message = "Enter the farmer's name")
    @Size(max = 100, message = "Name must be 100 characters or fewer")
    private String fullName;

    @NotBlank(message = "Enter a phone number")
    @Size(max = 25, message = "Phone number must be 25 characters or fewer")
    private String phone;

    @NotBlank(message = "Enter the village or town")
    @Size(max = 100, message = "Village must be 100 characters or fewer")
    private String village;

    @Size(max = 255, message = "Address must be 255 characters or fewer")
    private String address;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
