package com.farmgoat.registration.web;

import com.farmgoat.registration.repository.FarmerRepository;
import com.farmgoat.registration.repository.GoatRepository;
import com.farmgoat.registration.repository.VaccinationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final FarmerRepository farmerRepository;
    private final GoatRepository goatRepository;
    private final VaccinationRepository vaccinationRepository;

    public HomeController(FarmerRepository farmerRepository, GoatRepository goatRepository,
                          VaccinationRepository vaccinationRepository) {
        this.farmerRepository = farmerRepository;
        this.goatRepository = goatRepository;
        this.vaccinationRepository = vaccinationRepository;
    }

    @GetMapping("/")
    public String dashboard(Model model) {
        model.addAttribute("farmerCount", farmerRepository.count());
        model.addAttribute("goatCount", goatRepository.count());
        model.addAttribute("vaccinationCount", vaccinationRepository.count());
        return "dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
