package com.example.phone_contacts.controller;

import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.model.PhoneNumber;
import com.example.phone_contacts.service.ContactService;
import com.example.phone_contacts.service.PhoneNumberService;
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

import java.util.Optional;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PhoneNumberControllerTest {
    private PhoneNumberController phoneNumberController;

    private Validator validator;

    @Mock
    private PhoneNumberService phoneNumberService;

    @Mock
    private ContactService contactService;

    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validatorFactory = new LocalValidatorFactoryBean();
        validatorFactory.afterPropertiesSet();
        validator = validatorFactory.getValidator();
        MockitoAnnotations.initMocks(this);
        phoneNumberController = new PhoneNumberController(phoneNumberService, contactService);
    }

    @Test
    void testCreatePhoneNumber_validPhoneNumber() {
        Long contactId = 1L;
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("+380123456789");

        Contact contact = new Contact();
        contact.setId(contactId);

        when(contactService.getContactById(contactId)).thenReturn(Optional.of(contact));
        when(phoneNumberService.createPhoneNumber(phoneNumber)).thenReturn(phoneNumber);

        BindingResult bindingResult = new BeanPropertyBindingResult(phoneNumber, "phoneNumber");
        validator.validate(phoneNumber);

        ResponseEntity<PhoneNumber> responseEntity = phoneNumberController.createPhoneNumber(contactId, phoneNumber, bindingResult);

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals(phoneNumber, responseEntity.getBody());
        verify(contactService, times(1)).getContactById(contactId);
        verify(phoneNumberService, times(1)).createPhoneNumber(phoneNumber);
    }

    @Test
    void testCreatePhoneNumber_invalidPhoneNumber() {
        Long contactId = 1L;
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("invalid");

        Contact contact = new Contact();
        contact.setId(contactId);

        BindingResult bindingResult = new BeanPropertyBindingResult(phoneNumber, "phoneNumber");
        validator.validate(phoneNumber);

        when(contactService.getContactById(contactId)).thenReturn(Optional.of(contact));

        if (bindingResult.hasErrors()) {
            ResponseEntity<PhoneNumber> responseEntity = phoneNumberController.createPhoneNumber(contactId, phoneNumber, bindingResult);

            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
            verify(contactService, times(1)).getContactById(contactId);
            verify(phoneNumberService, never()).createPhoneNumber(phoneNumber);
        } else {
            fail("PhoneNumber should be invalid");
        }
    }

    @Test
    void testDeletePhoneNumber_existingPhoneNumber() {
        Long phoneNumberId = 1L;
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setId(phoneNumberId);

        when(phoneNumberService.getPhoneNumberById(phoneNumberId)).thenReturn(Optional.of(phoneNumber));

        ResponseEntity<Void> responseEntity = phoneNumberController.deletePhoneNumber(phoneNumberId);

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(phoneNumberService, times(1)).getPhoneNumberById(phoneNumberId);
        verify(phoneNumberService, times(1)).deletePhoneNumber(phoneNumberId);
    }

    @Test
    void testDeletePhoneNumber_nonexistentPhoneNumber() {
        Long phoneNumberId = 1L;

        when(phoneNumberService.getPhoneNumberById(phoneNumberId)).thenReturn(Optional.empty());

        ResponseEntity<Void> responseEntity = phoneNumberController.deletePhoneNumber(phoneNumberId);

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(phoneNumberService, times(1)).getPhoneNumberById(phoneNumberId);
        verify(phoneNumberService, never()).deletePhoneNumber(phoneNumberId);
    }
}