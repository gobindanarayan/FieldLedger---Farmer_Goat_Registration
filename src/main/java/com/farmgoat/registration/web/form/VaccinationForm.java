package com.farmgoat.registration.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class VaccinationForm {

    @NotBlank(message = "Enter the vaccine name")
    @Size(max = 100, message = "Vaccine name must be 100 characters or fewer")
    private String vaccineName;

    @NotNull(message = "Choose the vaccination date")
    private LocalDate vaccinationDate;

    @NotNull(message = "Choose the next due date")
    private LocalDate nextDueDate;

    @Size(max = 500, message = "Notes must be 500 characters or fewer")
    private String notes;

    public String getVaccineName() { return vaccineName; }
    public void setVaccineName(String vaccineName) { this.vaccineName = vaccineName; }
    public LocalDate getVaccinationDate() { return vaccinationDate; }
    public void setVaccinationDate(LocalDate vaccinationDate) { this.vaccinationDate = vaccinationDate; }
    public LocalDate getNextDueDate() { return nextDueDate; }
    public void setNextDueDate(LocalDate nextDueDate) { this.nextDueDate = nextDueDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
