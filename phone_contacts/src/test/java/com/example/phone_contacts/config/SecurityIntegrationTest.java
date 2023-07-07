package com.example.phone_contacts.config;

import com.example.phone_contacts.model.Person;
import com.example.phone_contacts.repository.PersonRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PersonRepository personRepository;

    /**
     * Sets up a test user before each test execution.
     */
    @BeforeEach
    void setUp() {
        Optional<Person> existingPerson = personRepository.findByUsername("user");
        if (existingPerson.isEmpty()) {
            Person person = new Person();
            person.setUsername("user");
            person.setPassword("secret");
            personRepository.save(person);
        }
    }

    /**
     * Tests the authentication success scenario.
     *
     * @throws Exception if an error occurs during the test.
     */
    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testAuthenticationSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/contacts"))
                .andExpect(authenticated());
    }

    /**
     * Tests the authentication failure scenario.
     *
     * @throws Exception if an error occurs during the test.
     */
    @Test
    void testAuthenticationFailure() throws Exception {
        mockMvc.perform(formLogin("/login")
                        .user("username", "john")
                        .password("password", "wrong-password"))
                .andExpect(unauthenticated());
    }
}
