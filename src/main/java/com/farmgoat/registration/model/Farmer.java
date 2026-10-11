package com.farmgoat.registration.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @OneToMany(mappedBy = "farmer", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id DESC")
    private List<Goat> goats = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getVillage() { return village; }
    public void setVillage(String village) { this.village = village; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public List<Goat> getGoats() { return goats; }
    public void setGoats(List<Goat> goats) { this.goats = goats; }

    public void addGoat(Goat goat) {
        goats.add(goat);
        goat.setFarmer(this);
    }

    public void removeGoat(Goat goat) {
        goats.remove(goat);
        goat.setFarmer(null);
    }
}
