package com.example.phone_contacts.service;

import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.model.PhoneNumber;
import com.example.phone_contacts.repository.ContactRepository;
import com.example.phone_contacts.repository.PhoneNumberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PhoneNumberServiceIntegrationTest {

    @Autowired
    private PhoneNumberRepository phoneNumberRepository;

    @Autowired
    private ContactRepository contactRepository;

    private PhoneNumberService phoneNumberService;

    @BeforeEach
    void setUp() {
        phoneNumberService = new PhoneNumberService(phoneNumberRepository);
    }

    @Test
    void testCreatePhoneNumber() {
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact = contactRepository.save(contact);

        PhoneNumber phoneNumberToSave = new PhoneNumber();
        phoneNumberToSave.setContact(contact);
        phoneNumberToSave.setNumber("+12-345-6789012");

        PhoneNumber createdPhoneNumber = phoneNumberService.createPhoneNumber(phoneNumberToSave);

        assertNotNull(createdPhoneNumber.getId());
        assertEquals("+12-345-6789012", createdPhoneNumber.getNumber());
    }

    @Test
    void testDeletePhoneNumber() {
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact = contactRepository.save(contact);

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setContact(contact);
        phoneNumber.setNumber("+12-345-6789012");
        PhoneNumber savedPhoneNumber = phoneNumberRepository.save(phoneNumber);

        phoneNumberService.deletePhoneNumber(savedPhoneNumber.getId());

        Optional<PhoneNumber> deletedPhoneNumber = phoneNumberRepository.findById(savedPhoneNumber.getId());
        assertFalse(deletedPhoneNumber.isPresent());
    }

    @Test
    void testGetPhoneNumberById_existingPhoneNumber() {
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact = contactRepository.save(contact);

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setContact(contact);
        phoneNumber.setNumber("+12-345-6789012");
        PhoneNumber savedPhoneNumber = phoneNumberRepository.save(phoneNumber);

        Optional<PhoneNumber> foundPhoneNumber = phoneNumberService.getPhoneNumberById(savedPhoneNumber.getId());

        assertTrue(foundPhoneNumber.isPresent());
        assertEquals(savedPhoneNumber.getId(), foundPhoneNumber.get().getId());
        assertEquals("+12-345-6789012", foundPhoneNumber.get().getNumber());
    }

    @Test
    void testGetPhoneNumberById_nonexistentPhoneNumber() {
        Optional<PhoneNumber> foundPhoneNumber = phoneNumberService.getPhoneNumberById(1L);

        assertFalse(foundPhoneNumber.isPresent());
    }

    @Test
    void testGetPhoneNumberByNumberAndContact_existingPhoneNumber() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("+12-345-6789012");

        Contact contact = new Contact();
        contact.setName("John Doe");
        contact = contactRepository.save(contact);

        phoneNumber.setContact(contact);
        PhoneNumber savedPhoneNumber = phoneNumberRepository.save(phoneNumber);

        Optional<PhoneNumber> foundPhoneNumber = phoneNumberService.getPhoneNumberByNumberAndContact("+12-345-6789012", contact);

        assertTrue(foundPhoneNumber.isPresent());
        assertEquals(savedPhoneNumber.getId(), foundPhoneNumber.get().getId());
        assertEquals("+12-345-6789012", foundPhoneNumber.get().getNumber());
    }

    @Test
    void testGetPhoneNumberByNumberAndContact_nonexistentPhoneNumber() {
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact = contactRepository.save(contact);

        Optional<PhoneNumber> foundPhoneNumber = phoneNumberService.getPhoneNumberByNumberAndContact("+12-345-6789012", contact);

        assertFalse(foundPhoneNumber.isPresent());
    }
    @Test
    void testGetAllPhoneNumbers() {
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact = contactRepository.save(contact);

        PhoneNumber phoneNumber1 = new PhoneNumber();
        phoneNumber1.setNumber("+12-345-6789012");
        phoneNumber1.setContact(contact);

        PhoneNumber phoneNumber2 = new PhoneNumber();
        phoneNumber2.setNumber("+34-567-8901234");
        phoneNumber2.setContact(contact);

        phoneNumberRepository.save(phoneNumber1);
        phoneNumberRepository.save(phoneNumber2);

        Iterable<PhoneNumber> allPhoneNumbers = phoneNumberService.getAllPhoneNumbers();

        int count = 0;
        for (PhoneNumber phoneNumber : allPhoneNumbers) {
            count++;
        }

        assertEquals(2, count);
    }

    @Test
    void testUpdatePhoneNumber_existingPhoneNumber() {
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact = contactRepository.save(contact);

        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setContact(contact);
        phoneNumber.setNumber("+12-345-6789012");

        PhoneNumber savedPhoneNumber = phoneNumberRepository.save(phoneNumber);

        PhoneNumber updatedPhoneNumber = new PhoneNumber();
        updatedPhoneNumber.setId(savedPhoneNumber.getId());
        updatedPhoneNumber.setNumber("+34-567-8901234");

        Optional<PhoneNumber> result = phoneNumberService.updatePhoneNumber(savedPhoneNumber.getId(), updatedPhoneNumber);

        assertTrue(result.isPresent());
        assertEquals(savedPhoneNumber.getId(), result.get().getId());
        assertEquals("+34-567-8901234", result.get().getNumber());
    }

    @Test
    void testUpdatePhoneNumber_nonexistentPhoneNumber() {
        PhoneNumber updatedPhoneNumber = new PhoneNumber();
        updatedPhoneNumber.setId(1L);
        updatedPhoneNumber.setNumber("+12-345-6789012");

        Optional<PhoneNumber> result = phoneNumberService.updatePhoneNumber(1L, updatedPhoneNumber);

        assertFalse(result.isPresent());
    }
}