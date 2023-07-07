package com.example.phone_contacts.controller;

import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.model.Email;
import com.example.phone_contacts.service.ContactService;
import com.example.phone_contacts.service.EmailService;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EmailControllerTest {
    private EmailController emailController;

    private Validator validator;

    @Mock
    private EmailService emailService;

    @Mock
    private ContactService contactService;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.afterPropertiesSet();
        validator = validatorFactory.getValidator();
        MockitoAnnotations.initMocks(this);
        emailController = new EmailController(emailService, contactService);
    }

    @Test
    void testCreateEmail() {
        Long contactId = 1L;
        Contact contact = new Contact();
        contact.setId(contactId);

        Email email = new Email();
        email.setAddress("test@example.com");
        email.setContact(contact);

        Email createdEmail = new Email();
        createdEmail.setId(1L);
        createdEmail.setAddress("test@example.com");
        createdEmail.setContact(contact);

        BindingResult bindingResult = new BeanPropertyBindingResult(email, "email");
        validator.validate(email);

        when(contactService.getContactById(contactId)).thenReturn(Optional.of(contact));
        when(emailService.createEmail(email)).thenReturn(createdEmail);

        if (!bindingResult.hasErrors()) {

            ResponseEntity<Email> responseEntity = emailController.createEmail(contactId, email, bindingResult);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(createdEmail, responseEntity.getBody());
        verify(contactService, times(1)).getContactById(contactId);
        verify(emailService, times(1)).createEmail(email);
        } else {
            fail("Email should not be invalid");
        }
    }

    @Test
    void testUpdateEmail_existingEmail() {
        Long emailId = 1L;
        Email email = new Email();
        email.setId(emailId);
        email.setAddress("test@example.com");

        Optional<Email> existingEmail = Optional.of(email);

        Email updatedEmail = new Email();
        updatedEmail.setId(emailId);
        updatedEmail.setAddress("updated_test@example.com");

        BindingResult bindingResult = new BeanPropertyBindingResult(email, "email");
        validator.validate(email);

        when(emailService.getEmailById(emailId)).thenReturn(existingEmail);
        when(emailService.updateEmail(emailId, email)).thenReturn(Optional.of(updatedEmail));

        if (!bindingResult.hasErrors()) {

            ResponseEntity<Optional<Email>> responseEntity = emailController.updateEmail(emailId, email, bindingResult);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(Optional.of(updatedEmail), responseEntity.getBody());
        verify(emailService, times(1)).updateEmail(emailId, email);
        } else {
            fail("Email should not be invalid");
        }
    }

    @Test
    void testUpdateEmail_nonExistingEmail() {
        Long emailId = 1L;
        Email email = new Email();
        email.setId(emailId);
        email.setAddress("test@example.com");

        BindingResult bindingResult = new BeanPropertyBindingResult(email, "email");
        validator.validate(email);

        Optional<Email> existingEmail = Optional.empty();

        when(emailService.getEmailById(emailId)).thenReturn(existingEmail);

        if (bindingResult.hasErrors()) {

            ResponseEntity<Optional<Email>> responseEntity = emailController.updateEmail(emailId, email, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        verify(emailService, times(1)).getEmailById(emailId);
        verify(emailService, never()).updateEmail(emailId, email);
        } else {
            fail("Email should be invalid");
        }
    }

    @Test
    void testDeleteEmail_existingEmail() {
        Long emailId = 1L;
        Email email = new Email();
        email.setId(emailId);
        email.setAddress("test@example.com");

        Optional<Email> existingEmail = Optional.of(email);

        when(emailService.getEmailById(emailId)).thenReturn(existingEmail);

        ResponseEntity<Void> responseEntity = emailController.deleteEmail(emailId);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(emailService, times(1)).getEmailById(emailId);
        verify(emailService, times(1)).deleteEmail(emailId);
    }

    @Test
    void testDeleteEmail_nonExistingEmail() {
        Long emailId = 1L;

        Optional<Email> existingEmail = Optional.empty();

        when(emailService.getEmailById(emailId)).thenReturn(existingEmail);

        ResponseEntity<Void> responseEntity = emailController.deleteEmail(emailId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(emailService, times(1)).getEmailById(emailId);
        verify(emailService, never()).deleteEmail(emailId);
    }

    @Test
    void testGetAllEmails() {
        Long contactId = 1L;
        Contact contact = new Contact();
        contact.setId(contactId);

        List<Email> emails = new ArrayList<>();
        emails.add(new Email(1L, "test1@example.com", contact));
        emails.add(new Email(2L, "test2@example.com", contact));

        when(contactService.getContactById(contactId)).thenReturn(Optional.of(contact));
        when(emailService.getAllEmails()).thenReturn(emails);

        ResponseEntity<List<Email>> responseEntity = emailController.getAllEmails(contactId);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(emails, responseEntity.getBody());
        verify(contactService, times(1)).getContactById(contactId);
        verify(emailService, times(1)).getAllEmails();
    }
}
