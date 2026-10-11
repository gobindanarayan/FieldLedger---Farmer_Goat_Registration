package com.farmgoat.registration.config;

import com.farmgoat.registration.model.AppUser;
import com.farmgoat.registration.model.Farmer;
import com.farmgoat.registration.model.Goat;
import com.farmgoat.registration.model.Vaccination;
import com.farmgoat.registration.repository.AppUserRepository;
import com.farmgoat.registration.repository.FarmerRepository;
import com.farmgoat.registration.repository.GoatRepository;
import com.farmgoat.registration.repository.VaccinationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@Configuration
public class SampleDataInitializer {

    @Bean
    CommandLineRunner addDemoData(AppUserRepository userRepository,
                                  FarmerRepository farmerRepository,
                                  GoatRepository goatRepository,
                                  VaccinationRepository vaccinationRepository,
                                  PasswordEncoder passwordEncoder,
                                  @Value("${app.demo-data-enabled:true}") boolean demoDataEnabled,
                                  @Value("${app.demo-username:demo}") String username,
                                  @Value("${app.demo-password:demo12345}") String password) {
        return args -> {
            if (!demoDataEnabled) return;

            if (!userRepository.existsByUsername(username)) {
                AppUser user = new AppUser();
                user.setUsername(username);
                user.setPasswordHash(passwordEncoder.encode(password));
                user.setRole("ADMIN");
                userRepository.save(user);
            }

            if (farmerRepository.count() > 0) return;

            Farmer ramesh = farmer("Ramesh Kumar", "9876501234", "Greenfield", "12 Lake Road");
            Farmer lakshmi = farmer("Lakshmi Devi", "9876505678", "Hillview", "4 Market Street");
            farmerRepository.save(ramesh);
            farmerRepository.save(lakshmi);

            Goat chinna = goat("G-1001", "Chinna", "Boer", "Female", LocalDate.now().minusYears(2), ramesh);
            Goat bella = goat("G-1002", "Bella", "Sirohi", "Female", LocalDate.now().minusYears(1), ramesh);
            Goat raja = goat("G-2001", "Raja", "Jamunapari", "Male", LocalDate.now().minusMonths(18), lakshmi);
            goatRepository.save(chinna);
            goatRepository.save(bella);
            goatRepository.save(raja);

            vaccinationRepository.save(vaccination("PPR", LocalDate.now().minusMonths(2),
                    LocalDate.now().plusMonths(10), "Routine annual vaccination", chinna));
            vaccinationRepository.save(vaccination("Enterotoxaemia", LocalDate.now().minusMonths(1),
                    LocalDate.now().plusMonths(5), "Booster due in six months", bella));
            vaccinationRepository.save(vaccination("PPR", LocalDate.now().minusMonths(3),
                    LocalDate.now().plusMonths(9), "Healthy at check-up", raja));
        };
    }

    private Farmer farmer(String name, String phone, String village, String address) {
        Farmer farmer = new Farmer();
        farmer.setFullName(name);
        farmer.setPhone(phone);
        farmer.setVillage(village);
        farmer.setAddress(address);
        return farmer;
    }

    private Goat goat(String tag, String name, String breed, String gender, LocalDate dateOfBirth, Farmer farmer) {
        Goat goat = new Goat();
        goat.setTagNumber(tag);
        goat.setName(name);
        goat.setBreed(breed);
        goat.setGender(gender);
        goat.setDateOfBirth(dateOfBirth);
            farmer.addGoat(goat);
        return goat;
    }

    private Vaccination vaccination(String name, LocalDate date, LocalDate nextDate, String notes, Goat goat) {
        Vaccination vaccination = new Vaccination();
        vaccination.setVaccineName(name);
        vaccination.setVaccinationDate(date);
        vaccination.setNextDueDate(nextDate);
        vaccination.setNotes(notes);
        goat.addVaccination(vaccination);
        return vaccination;
    }
}
