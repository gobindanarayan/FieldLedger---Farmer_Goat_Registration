package com.farmgoat.registration;

import com.farmgoat.registration.model.Goat;
import com.farmgoat.registration.model.Farmer;
import com.farmgoat.registration.model.Vaccination;
import com.farmgoat.registration.repository.FarmerRepository;
import com.farmgoat.registration.repository.GoatRepository;
import com.farmgoat.registration.repository.VaccinationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ApplicationSmokeTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private GoatRepository goatRepository;

    @Autowired
    private VaccinationRepository vaccinationRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void loginAndRegistrationFlowWorks() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Sign in to your account")));

        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));

        org.junit.jupiter.api.Assertions.assertEquals(2, farmerRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(3, goatRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(3, vaccinationRepository.count());

        MvcResult loginResult = mockMvc.perform(post("/login")
                        .with(csrf())
                        .param("username", "demo")
                        .param("password", "demo12345"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        MockHttpSession session = (MockHttpSession) loginResult.getRequest().getSession(false);
        org.junit.jupiter.api.Assertions.assertNotNull(session);

        mockMvc.perform(get("/").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Your records at a glance")));

        MvcResult farmerResult = mockMvc.perform(post("/farmers").session(session).with(csrf())
                        .param("fullName", "Test Farmer")
                        .param("phone", "9000000001")
                        .param("village", "Test Village")
                        .param("address", "1 Demo Street"))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String farmerLocation = farmerResult.getResponse().getRedirectedUrl();
        String farmerId = farmerLocation.substring(farmerLocation.lastIndexOf('/') + 1);

        mockMvc.perform(get("/farmers").param("q", "Test").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Test Farmer")));
        mockMvc.perform(get(farmerLocation).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("FARMER PROFILE")))
                .andExpect(content().string(containsString("Delete farmer")));
        mockMvc.perform(get("/goats/farmer/" + farmerId + "/new").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Register a goat")));

        MvcResult goatResult = mockMvc.perform(post("/goats/farmer/" + farmerId).session(session).with(csrf())
                        .param("tagNumber", "TEST-1")
                        .param("name", "Test Goat")
                        .param("breed", "Boer")
                        .param("gender", "Female")
                        .param("dateOfBirth", LocalDate.now().minusYears(1).toString()))
                .andExpect(status().is3xxRedirection())
                .andReturn();
        String goatLocation = goatResult.getResponse().getRedirectedUrl();
        String goatId = goatLocation.substring(goatLocation.lastIndexOf('/') + 1);

        mockMvc.perform(get("/goats").param("q", "TEST-1").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Test Goat")));
        mockMvc.perform(get("/goats/" + goatId + "/vaccinations/new").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Add vaccination")));

        mockMvc.perform(post("/goats/" + goatId + "/vaccinations").session(session).with(csrf())
                        .param("vaccineName", "Test vaccine")
                        .param("vaccinationDate", LocalDate.now().minusDays(1).toString())
                        .param("nextDueDate", LocalDate.now().plusMonths(6).toString())
                        .param("notes", "Smoke test record"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/goats/" + goatId).session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Test vaccine")))
                .andExpect(content().string(containsString("Test Farmer")))
                .andExpect(content().string(containsString("Delete goat")));

        org.junit.jupiter.api.Assertions.assertEquals(3, farmerRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(4, goatRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(4, vaccinationRepository.count());

        mockMvc.perform(post("/goats/" + goatId + "/delete").session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/goats"));
        org.junit.jupiter.api.Assertions.assertEquals(3, farmerRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(3, goatRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(3, vaccinationRepository.count());

        mockMvc.perform(post("/goats/farmer/" + farmerId).session(session).with(csrf())
                        .param("tagNumber", "TEST-2")
                        .param("name", "Second Test Goat")
                        .param("breed", "Boer")
                        .param("gender", "Male")
                        .param("dateOfBirth", LocalDate.now().minusYears(2).toString()))
                .andExpect(status().is3xxRedirection());
        Goat testGoat = goatRepository.findByTagNumberIgnoreCase("TEST-2").orElseThrow();
        mockMvc.perform(post("/goats/" + testGoat.getId() + "/vaccinations").session(session).with(csrf())
                        .param("vaccineName", "Cascade test vaccine")
                        .param("vaccinationDate", LocalDate.now().minusDays(2).toString())
                        .param("nextDueDate", LocalDate.now().plusMonths(6).toString())
                        .param("notes", "Must be deleted with the farmer"))
                .andExpect(status().is3xxRedirection());
        org.junit.jupiter.api.Assertions.assertEquals(4, goatRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(4, vaccinationRepository.count());

        mockMvc.perform(post("/farmers/" + farmerId + "/delete").session(session).with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/farmers"));
        org.junit.jupiter.api.Assertions.assertEquals(2, farmerRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(3, goatRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(3, vaccinationRepository.count());

        Farmer sqlFarmer = new Farmer();
        sqlFarmer.setFullName("SQL Cascade Farmer");
        sqlFarmer.setPhone("9000000002");
        sqlFarmer.setVillage("SQL Village");
        farmerRepository.save(sqlFarmer);
        Goat sqlGoat = new Goat();
        sqlGoat.setTagNumber("SQL-1");
        sqlGoat.setName("SQL Test Goat");
        sqlGoat.setBreed("Boer");
        sqlGoat.setGender("Female");
        sqlGoat.setFarmer(sqlFarmer);
        goatRepository.save(sqlGoat);
        Vaccination sqlVaccination = new Vaccination();
        sqlVaccination.setVaccineName("SQL test vaccine");
        sqlVaccination.setVaccinationDate(LocalDate.now().minusDays(1));
        sqlVaccination.setNextDueDate(LocalDate.now().plusMonths(6));
        sqlVaccination.setGoat(sqlGoat);
        vaccinationRepository.save(sqlVaccination);

        jdbcTemplate.update("DELETE FROM farmer WHERE id = ?", sqlFarmer.getId());
        org.junit.jupiter.api.Assertions.assertEquals(2, farmerRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(3, goatRepository.count());
        org.junit.jupiter.api.Assertions.assertEquals(3, vaccinationRepository.count());

    }
}
