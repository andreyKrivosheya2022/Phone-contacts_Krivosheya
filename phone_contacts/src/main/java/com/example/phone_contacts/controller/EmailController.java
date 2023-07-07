package com.example.phone_contacts.controller;

import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.model.Email;
import com.example.phone_contacts.service.ContactService;
import com.example.phone_contacts.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contacts/{contactId}/emails")
public class EmailController {
    private final EmailService emailService;
    private final ContactService contactService;

    public EmailController(EmailService emailService, ContactService contactService) {
        this.emailService = emailService;
        this.contactService = contactService;
    }

    // Get all emails of a contact
    @GetMapping
    public ResponseEntity<List<Email>> getAllEmails(@PathVariable Long contactId) {
        Optional<Contact> contact = contactService.getContactById(contactId);
        if (contact.isPresent()) {
            List<Email> emails = emailService.getAllEmails();
            return ResponseEntity.ok(emails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update an email
    @PutMapping("/{id}")
    public ResponseEntity<Optional<Email>> updateEmail(@PathVariable Long id,
                                                       @Valid @RequestBody Email email,
                                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Optional<Email>> updatedEmail = Optional.ofNullable(emailService.updateEmail(id, email));
        return updatedEmail.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a new email for a contact
    @PostMapping
    public ResponseEntity<Email> createEmail(@PathVariable Long contactId,
                                             @Valid @RequestBody Email email,
                                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<Contact> contact = contactService.getContactById(contactId);
        if (contact.isPresent()) {
            email.setContact(contact.get());
            Email createdEmail = emailService.createEmail(email);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdEmail);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete an email
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmail(@PathVariable Long id) {
        Optional<Email> existingEmail = emailService.getEmailById(id);
        if (existingEmail.isPresent()) {
            emailService.deleteEmail(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
