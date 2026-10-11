package com.farmgoat.registration.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class GoatForm {

    @NotBlank(message = "Enter a unique tag number")
    @Size(max = 40, message = "Tag number must be 40 characters or fewer")
    private String tagNumber;

    @NotBlank(message = "Enter a goat name")
    @Size(max = 100, message = "Name must be 100 characters or fewer")
    private String name;

    @NotBlank(message = "Enter the breed")
    @Size(max = 100, message = "Breed must be 100 characters or fewer")
    private String breed;

    @NotBlank(message = "Choose a gender")
    private String gender;

    @NotNull(message = "Choose the date of birth")
    private LocalDate dateOfBirth;

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
}
