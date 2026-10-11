package com.farmgoat.registration.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.CascadeType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Goat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Enter a unique tag number")
    @Column(nullable = false, unique = true)
    private String tagNumber;

    @NotBlank(message = "Enter a goat name")
    private String name;

    @NotBlank(message = "Enter the breed")
    private String breed;

    @NotBlank(message = "Choose a gender")
    private String gender;

    private LocalDate dateOfBirth;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farmer_id", nullable = false)
    private Farmer farmer;

    @OneToMany(
            mappedBy = "goat",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("vaccinationDate DESC, id DESC")
    private List<Vaccination> vaccinations = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTagNumber() { return tagNumber; }
    public void setTagNumber(String tagNumber) { this.tagNumber = tagNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }
    public List<Vaccination> getVaccinations() { return vaccinations; }
    public void setVaccinations(List<Vaccination> vaccinations) { this.vaccinations = vaccinations; }
}
