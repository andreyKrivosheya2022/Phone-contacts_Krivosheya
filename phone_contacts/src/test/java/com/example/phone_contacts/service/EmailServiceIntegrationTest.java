package com.example.phone_contacts.service;
import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.model.Email;
import com.example.phone_contacts.repository.ContactRepository;
import com.example.phone_contacts.repository.EmailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmailServiceIntegrationTest {

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private ContactRepository contactRepository;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(emailRepository);
    }

    @Test
    void testCreateEmail() {
        Contact contact = new Contact();
        contact.setName("Andrey");
        contact = contactRepository.save(contact);

        Email emailToSave = new Email();
        emailToSave.setContact(contact);
        emailToSave.setAddress("test@example.com");

        Email createdEmail = emailService.createEmail(emailToSave);

        assertNotNull(createdEmail.getId());
        assertEquals("test@example.com", createdEmail.getAddress());
    }
    @Test
    void testDeleteEmail() {
        Contact contact = new Contact();
        contact.setName("Andrey");
        contact = contactRepository.save(contact);
        Email email = new Email();
        email.setContact(contact);
        email.setAddress("test@example.com");
        Email savedEmail = emailRepository.save(email);

        emailService.deleteEmail(savedEmail.getId());

        Optional<Email> deletedEmail = emailRepository.findById(savedEmail.getId());
        assertFalse(deletedEmail.isPresent());
    }

    @Test
    void testGetEmailById_existingEmail() {
        Contact contact = new Contact();
        contact.setName("Andrey");
        contact = contactRepository.save(contact);

        Email email = new Email();
        email.setContact(contact);
        email.setAddress("test@example.com");
        Email savedEmail = emailRepository.save(email);

        Optional<Email> foundEmail = emailService.getEmailById(savedEmail.getId());

        assertTrue(foundEmail.isPresent());
        assertEquals(savedEmail.getId(), foundEmail.get().getId());
        assertEquals("test@example.com", foundEmail.get().getAddress());
    }

    @Test
    void testGetEmailById_nonexistentEmail() {
        Optional<Email> foundEmail = emailService.getEmailById(1L);

        assertFalse(foundEmail.isPresent());
    }

    @Test
    void testGetEmailByAddressAndContact_existingEmail() {
        Email email = new Email();
        email.setAddress("test@example.com");
        Contact contact = new Contact();
        contact.setName("Andrey");
        contact = contactRepository.save(contact);
        email.setContact(contact);
        Email savedEmail = emailRepository.save(email);

        Optional<Email> foundEmail = emailService.getEmailByAddressAndContact("test@example.com", contact);

        assertTrue(foundEmail.isPresent());
        assertEquals(savedEmail.getId(), foundEmail.get().getId());
        assertEquals("test@example.com", foundEmail.get().getAddress());
    }

    @Test
    void testGetEmailByAddressAndContact_nonexistentEmail() {
        Contact contact = new Contact();
        contact.setName("Andrey");
        contact = contactRepository.save(contact);

        Optional<Email> foundEmail = emailService.getEmailByAddressAndContact("test@example.com", contact);

        assertFalse(foundEmail.isPresent());
    }

    @Test
    void testGetAllEmails() {
        Contact contact = new Contact();
        contact.setName("Andrey");
        contact = contactRepository.save(contact);

        Email email1 = new Email();
        email1.setAddress("test@example.com");
        email1.setContact(contact);
        Email email2 = new Email();
        email2.setAddress("another@example.com");
        email2.setContact(contact);
        emailRepository.save(email1);
        emailRepository.save(email2);

        Iterable<Email> allEmails = emailService.getAllEmails();

        int count = 0;
        for (Email email : allEmails) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    void testUpdateEmail_existingEmail() {
        Contact contact = new Contact();
        contact.setName("Andrey");
        contact = contactRepository.save(contact);
        Email email = new Email();
        email.setContact(contact);
        email.setAddress("test@example.com");
        Email savedEmail = emailRepository.save(email);

        Email updatedEmail = new Email();
        updatedEmail.setId(savedEmail.getId());
        updatedEmail.setAddress("new@example.com");

        Optional<Email> result = emailService.updateEmail(savedEmail.getId(), updatedEmail);

        assertTrue(result.isPresent());
        assertEquals(savedEmail.getId(), result.get().getId());
        assertEquals("new@example.com", result.get().getAddress());
    }

    @Test
    void testUpdateEmail_nonexistentEmail() {
        Email updatedEmail = new Email();
        updatedEmail.setId(1L);
        updatedEmail.setAddress("new@example.com");

        Optional<Email> result = emailService.updateEmail(1L, updatedEmail);

        assertFalse(result.isPresent());
    }
}