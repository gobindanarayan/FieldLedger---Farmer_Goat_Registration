package com.farmgoat.registration.web;

import com.farmgoat.registration.model.Farmer;
import com.farmgoat.registration.model.Goat;
import com.farmgoat.registration.model.Vaccination;
import com.farmgoat.registration.repository.FarmerRepository;
import com.farmgoat.registration.repository.GoatRepository;
import com.farmgoat.registration.repository.VaccinationRepository;
import com.farmgoat.registration.web.form.GoatForm;
import com.farmgoat.registration.web.form.VaccinationForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/goats")
public class GoatController {

    private final GoatRepository goatRepository;
    private final FarmerRepository farmerRepository;
    private final VaccinationRepository vaccinationRepository;

    public GoatController(GoatRepository goatRepository, FarmerRepository farmerRepository,
                          VaccinationRepository vaccinationRepository) {
        this.goatRepository = goatRepository;
        this.farmerRepository = farmerRepository;
        this.vaccinationRepository = vaccinationRepository;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String q, Model model) {
        String query = q.trim();
        List<Goat> goats = query.isEmpty() ? goatRepository.findAll() : goatRepository.search(query);
        model.addAttribute("goats", goats);
        model.addAttribute("q", q);
        return "goats/list";
    }

    @GetMapping("/{goatId}")
    public String details(@PathVariable Long goatId, Model model) {
        Goat goat = goatRepository.findById(goatId).orElse(null);
        if (goat == null) return "redirect:/goats?missing";
        model.addAttribute("goat", goat);
        model.addAttribute("vaccinations", goat.getVaccinations());
        return "goats/details";
    }

    @PostMapping("/{goatId}/delete")
    public String delete(@PathVariable Long goatId, RedirectAttributes redirectAttributes) {
        Goat goat = goatRepository.findById(goatId).orElse(null);
        if (goat == null) return "redirect:/goats?missing";

        goatRepository.delete(goat);
        redirectAttributes.addFlashAttribute("success", "Goat and its vaccination records deleted.");
        return "redirect:/goats";
    }

    @GetMapping("/farmer/{farmerId}/new")
    public String newGoat(@PathVariable Long farmerId, Model model) {
        Farmer farmer = farmerRepository.findById(farmerId).orElse(null);
        if (farmer == null) return "redirect:/farmers?missing";
        model.addAttribute("farmer", farmer);
        model.addAttribute("goatForm", new GoatForm());
        return "goats/form";
    }

    @PostMapping("/farmer/{farmerId}")
    public String create(@PathVariable Long farmerId, @Valid @ModelAttribute GoatForm goatForm,
                         BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {
        Farmer farmer = farmerRepository.findById(farmerId).orElse(null);
        if (farmer == null) return "redirect:/farmers?missing";

        if (goatForm.getTagNumber() != null && goatRepository.findByTagNumberIgnoreCase(goatForm.getTagNumber().trim()).isPresent()) {
            bindingResult.rejectValue("tagNumber", "duplicate", "That tag number is already registered");
        }
        if (goatForm.getDateOfBirth() != null && goatForm.getDateOfBirth().isAfter(LocalDate.now())) {
            bindingResult.rejectValue("dateOfBirth", "futureDate", "Date of birth cannot be in the future");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("farmer", farmer);
            return "goats/form";
        }

        Goat goat = new Goat();
        goat.setTagNumber(goatForm.getTagNumber().trim());
        goat.setName(goatForm.getName().trim());
        goat.setBreed(goatForm.getBreed().trim());
        goat.setGender(goatForm.getGender());
        goat.setDateOfBirth(goatForm.getDateOfBirth());
        farmer.addGoat(goat);
        goatRepository.save(goat);
        redirectAttributes.addFlashAttribute("success", "Goat registered successfully.");
        return "redirect:/goats/" + goat.getId();
    }

    @GetMapping("/{goatId}/vaccinations/new")
    public String newVaccination(@PathVariable Long goatId, Model model) {
        Goat goat = goatRepository.findById(goatId).orElse(null);
        if (goat == null) return "redirect:/goats?missing";
        model.addAttribute("goat", goat);
        model.addAttribute("vaccinationForm", new VaccinationForm());
        return "vaccinations/form";
    }

    @PostMapping("/{goatId}/vaccinations")
    public String createVaccination(@PathVariable Long goatId,
                                    @Valid @ModelAttribute VaccinationForm vaccinationForm,
                                    BindingResult bindingResult, Model model,
                                    RedirectAttributes redirectAttributes) {
        Goat goat = goatRepository.findById(goatId).orElse(null);
        if (goat == null) return "redirect:/goats?missing";

        if (vaccinationForm.getVaccinationDate() != null && vaccinationForm.getNextDueDate() != null
                && !vaccinationForm.getNextDueDate().isAfter(vaccinationForm.getVaccinationDate())) {
            bindingResult.rejectValue("nextDueDate", "dateOrder", "Next due date must be after the vaccination date");
        }
        if (vaccinationForm.getVaccinationDate() != null && vaccinationForm.getVaccinationDate().isAfter(LocalDate.now())) {
            bindingResult.rejectValue("vaccinationDate", "futureDate", "Vaccination date cannot be in the future");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("goat", goat);
            return "vaccinations/form";
        }

        Vaccination vaccination = new Vaccination();
        vaccination.setVaccineName(vaccinationForm.getVaccineName().trim());
        vaccination.setVaccinationDate(vaccinationForm.getVaccinationDate());
        vaccination.setNextDueDate(vaccinationForm.getNextDueDate());
        vaccination.setNotes(vaccinationForm.getNotes() == null ? "" : vaccinationForm.getNotes().trim());
        goat.addVaccination(vaccination);
        vaccinationRepository.save(vaccination);
        redirectAttributes.addFlashAttribute("success", "Vaccination record added.");
        return "redirect:/goats/" + goatId;
    }
}
