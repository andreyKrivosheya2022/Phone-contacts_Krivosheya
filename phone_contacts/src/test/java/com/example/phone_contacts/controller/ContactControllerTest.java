package com.example.phone_contacts.controller;

import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.model.Email;
import com.example.phone_contacts.model.PhoneNumber;
import com.example.phone_contacts.service.ContactService;
import com.example.phone_contacts.service.EmailService;
import com.example.phone_contacts.service.PhoneNumberService;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.io.IOException;
import java.util.*;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContactControllerTest {
    private ContactController contactController;

    private EmailController emailController;

    private PhoneNumberController phoneNumberController;

    private Validator validator;

    @Mock
    private ContactService contactService;

    @Mock
    private EmailService emailService;

    @Mock
    private PhoneNumberService phoneNumberService;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.afterPropertiesSet();
        validator = validatorFactory.getValidator();

        MockitoAnnotations.initMocks(this);
        contactController = new ContactController(contactService);
        emailController = new EmailController(emailService, contactService);
        phoneNumberController = new PhoneNumberController(phoneNumberService, contactService);
    }

    @Test
    void testCreateContact() {
        Email validEmail = new Email(10L, "val@gmail.com");
        PhoneNumber validPhoneNumber = new PhoneNumber("+123456789");

        // Создание BindingResult для проверки ошибок валидации
        BindingResult bindingResultEmail = new BeanPropertyBindingResult(validEmail, "validEmail");
        validator.validate(validEmail);

        BindingResult bindingResultPhoneNumber = new BeanPropertyBindingResult(validPhoneNumber, "validPhoneNumber");
        validator.validate(validPhoneNumber);

        // Проверка отсутствия ошибок валидации
        if (!bindingResultPhoneNumber.hasErrors() && !bindingResultEmail.hasErrors()) {
            Contact createdContact = new Contact();
            createdContact.setId(1L);
            createdContact.setName("John Doe");
            createdContact.setEmails(List.of(validEmail));
            createdContact.setPhoneNumbers(List.of(validPhoneNumber));

            BindingResult bindingResult = new BeanPropertyBindingResult(createdContact, "contact");

            when(contactService.createContact(createdContact)).thenReturn(createdContact);

            if (!bindingResult.hasErrors()) {
                ResponseEntity<Contact> responseEntity = contactController.createContact(createdContact, bindingResult);

                assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
                assertEquals(createdContact, responseEntity.getBody());
                verify(contactService, times(1)).createContact(createdContact);
            } else {
                fail("Contact should not be invalid");
            }
        } else {
            fail("Email or PhoneNumber should not be invalid");
        }
    }


    @Test
    void testUpdateContact() {
        Long contactId = 1L;

        Contact existingContact = new Contact();
        existingContact.setId(contactId);
        existingContact.setName("John Doe");

        Contact updatedContact = new Contact();
        updatedContact.setId(contactId);
        updatedContact.setName("Updated Name");

        Email validEmail = new Email(10L, "valid@gmail.com");
        PhoneNumber validPhoneNumber = new PhoneNumber("+123456789");

        BindingResult bindingResultEmail = new BeanPropertyBindingResult(validEmail, "validEmail");
        validator.validate(validEmail);

        BindingResult bindingResultPhoneNumber = new BeanPropertyBindingResult(validPhoneNumber, "validPhoneNumber");
        validator.validate(validPhoneNumber);

        if (!bindingResultPhoneNumber.hasErrors() && !bindingResultEmail.hasErrors()) {
            fail("Email and PhoneNumber should not be invalid");
        }

        updatedContact.setEmails(List.of(validEmail));
        updatedContact.setPhoneNumbers(List.of(validPhoneNumber));

        when(contactService.getContactById(contactId)).thenReturn(Optional.of(existingContact));

        when(contactService.updateContact(contactId, updatedContact)).thenReturn(updatedContact);

        BindingResult bindingResult = new BeanPropertyBindingResult(updatedContact, "updatedContact");

        if (!bindingResult.hasErrors()) {
            ResponseEntity<Contact> responseEntity = contactController.updateContact(contactId, updatedContact, bindingResult);

            assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
            assertEquals(updatedContact, responseEntity.getBody());
            verify(contactService, times(1)).updateContact(contactId, updatedContact);
        } else {
            fail("Contact should not be invalid");
        }
    }

    @Test
    void testDeleteContact_existingContact() {
        Long contactId = 1L;
        Contact contact = new Contact();
        contact.setId(contactId);
        contact.setName("John Doe");

        Optional<Contact> existingContact = Optional.of(contact);

        when(contactService.getContactById(contactId)).thenReturn(existingContact);

        ResponseEntity<Void> responseEntity = contactController.deleteContact(contactId);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(contactService, times(1)).getContactById(contactId);
        verify(contactService, times(1)).deleteContact(contactId);
    }

    @Test
    void testDeleteContact_nonExistingContact() {
        Long contactId = 1L;

        Optional<Contact> existingContact = Optional.empty();

        when(contactService.getContactById(contactId)).thenReturn(existingContact);

        ResponseEntity<Void> responseEntity = contactController.deleteContact(contactId);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(contactService, times(1)).getContactById(contactId);
        verify(contactService, never()).deleteContact(contactId);
    }

    @Test
    void testGetAllContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(1L, "John Doe"));
        contacts.add(new Contact(2L, "Jane Smith"));

        when(contactService.getAllContacts()).thenReturn(contacts);

        ResponseEntity<List<Contact>> responseEntity = contactController.getAllContacts();

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(contacts, responseEntity.getBody());
        verify(contactService, times(1)).getAllContacts();
    }

    @Test
    void exportContacts_ReturnsExportedData() throws IOException {
        List<Contact> contacts = Arrays.asList(
                new Contact(1L, "John Doe", new ArrayList<>(), new ArrayList<>(), null),
                new Contact(2L, "Jane Smith", new ArrayList<>(), new ArrayList<>(), null)
        );

        String exportedData = "[{\"id\":1,\"name\":\"John Doe\",\"emails\":[],\"phoneNumbers\":[]},{\"id\":2,\"name\":\"Jane Smith\",\"emails\":[],\"phoneNumbers\":[]}]";

        when(contactService.getAllContacts()).thenReturn(contacts);
        when(contactService.convertContactsToJson(contacts)).thenReturn(exportedData);

        ResponseEntity<Resource> response = contactController.exportContacts();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON_VALUE, Objects.requireNonNull(response.getHeaders().getContentType()).toString());
        assertEquals("attachment; filename=contacts.json", response.getHeaders().get(HttpHeaders.CONTENT_DISPOSITION).get(0));
        assertEquals(exportedData, new String(((ByteArrayResource) Objects.requireNonNull(response.getBody())).getByteArray()));
        verify(contactService, times(1)).getAllContacts();
        verify(contactService, times(1)).convertContactsToJson(contacts);
    }

    @Test
    void importContacts_ValidFile_ImportsContacts() throws Exception {
        List<Contact> importedContacts = Arrays.asList(
                new Contact(1L, "John Doe", new ArrayList<>(), new ArrayList<>(), null),
                new Contact(2L, "Jane Smith", new ArrayList<>(), new ArrayList<>(), null)
        );

        String importedData = "[{\"id\":1,\"name\":\"John Doe\",\"emails\":[],\"phoneNumbers\":[]},{\"id\":2,\"name\":\"Jane Smith\",\"emails\":[],\"phoneNumbers\":[]}]";
        MockMultipartFile file = new MockMultipartFile(
                "file", "contacts.json", MediaType.APPLICATION_JSON_VALUE, importedData.getBytes()
        );

        when(contactService.parseJsonToContacts(importedData)).thenReturn(importedContacts); // Add this line

        ResponseEntity<Void> response = contactController.importContacts(file);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(contactService, times(importedContacts.size())).createContact(any(Contact.class));
    }

    @Test
    void importContacts_InvalidFile_ReturnsInternalServerError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "contacts.json", MediaType.APPLICATION_JSON_VALUE, "invalid data".getBytes()
        );

        when(contactService.parseJsonToContacts(anyString())).thenThrow(new IOException()); // Add this line

        ResponseEntity<Void> response = contactController.importContacts(file);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }
}