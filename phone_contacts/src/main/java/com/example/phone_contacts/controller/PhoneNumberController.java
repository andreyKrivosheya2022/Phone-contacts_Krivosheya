package com.example.phone_contacts.controller;

import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.model.PhoneNumber;
import com.example.phone_contacts.service.ContactService;
import com.example.phone_contacts.service.PhoneNumberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/contacts/{contactId}/phoneNumbers")
public class PhoneNumberController {
    private final PhoneNumberService phoneNumberService;
    private final ContactService contactService;

    // Create a new phone number for a contact
    @PostMapping
    public ResponseEntity<PhoneNumber> createPhoneNumber(@PathVariable Long contactId,
                                                         @Valid @RequestBody PhoneNumber phoneNumber,
                                                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Contact> contact = contactService.getContactById(contactId);
        if (contact.isPresent()) {
            phoneNumber.setContact(contact.get());
            PhoneNumber createdPhoneNumber = phoneNumberService.createPhoneNumber(phoneNumber);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPhoneNumber);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a phone number
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhoneNumber(@PathVariable Long id) {
        Optional<PhoneNumber> existingPhoneNumber = phoneNumberService.getPhoneNumberById(id);
        if (existingPhoneNumber.isPresent()) {
            phoneNumberService.deletePhoneNumber(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
