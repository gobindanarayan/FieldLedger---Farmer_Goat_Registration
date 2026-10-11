package com.farmgoat.registration.web;

import com.farmgoat.registration.model.Farmer;
import com.farmgoat.registration.repository.FarmerRepository;
import com.farmgoat.registration.web.form.FarmerForm;
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

import java.util.List;

@Controller
@RequestMapping("/farmers")
public class FarmerController {

    private final FarmerRepository farmerRepository;

    public FarmerController(FarmerRepository farmerRepository) {
        this.farmerRepository = farmerRepository;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String q, Model model) {
        String query = q.trim();
        List<Farmer> farmers = query.isEmpty() ? farmerRepository.findAll() :
                farmerRepository.findByFullNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrVillageContainingIgnoreCase(
                        query, query, query);
        model.addAttribute("farmers", farmers);
        model.addAttribute("q", q);
        return "farmers/list";
    }

    @GetMapping("/new")
    public String newFarmer(Model model) {
        model.addAttribute("farmerForm", new FarmerForm());
        return "farmers/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute FarmerForm farmerForm, BindingResult bindingResult,
                         Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) return "farmers/form";

        Farmer farmer = new Farmer();
        farmer.setFullName(farmerForm.getFullName().trim());
        farmer.setPhone(farmerForm.getPhone().trim());
        farmer.setVillage(farmerForm.getVillage().trim());
        farmer.setAddress(farmerForm.getAddress() == null ? "" : farmerForm.getAddress().trim());
        farmerRepository.save(farmer);
        redirectAttributes.addFlashAttribute("success", "Farmer registered successfully.");
        return "redirect:/farmers/" + farmer.getId();
    }

    @GetMapping("/{farmerId}")
    public String details(@PathVariable Long farmerId, Model model) {
        Farmer farmer = farmerRepository.findById(farmerId).orElse(null);
        if (farmer == null) return "redirect:/farmers?missing";
        model.addAttribute("farmer", farmer);
        return "farmers/details";
    }

    @PostMapping("/{farmerId}/delete")
    public String delete(@PathVariable Long farmerId, RedirectAttributes redirectAttributes) {
        Farmer farmer = farmerRepository.findById(farmerId).orElse(null);
        if (farmer == null) return "redirect:/farmers?missing";

        farmerRepository.delete(farmer);
        redirectAttributes.addFlashAttribute("success", "Farmer, goats, and vaccination records deleted.");
        return "redirect:/farmers";
    }
}
