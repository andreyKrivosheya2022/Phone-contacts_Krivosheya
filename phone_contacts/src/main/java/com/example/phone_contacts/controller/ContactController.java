package com.example.phone_contacts.controller;

import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.service.ContactService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import jakarta.validation.Valid;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    // Create a new contact
    @PostMapping
    public ResponseEntity<Contact> createContact(@Valid @RequestBody Contact contact,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Contact createdContact = contactService.createContact(contact);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContact);
    }

    // Update an existing contact
    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable Long id,
                                                 @Valid @RequestBody Contact updatedContact,
                                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Contact savedContact = contactService.updateContact(id, updatedContact);
        if (savedContact != null) {
            return ResponseEntity.ok(savedContact);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a contact
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        Optional<Contact> existingContact = contactService.getContactById(id);
        if (existingContact.isPresent()) {
            contactService.deleteContact(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get all contacts
    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts() {
        List<Contact> contacts = contactService.getAllContacts();
        return ResponseEntity.ok(contacts);
    }

    // Get the image of a contact
    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getContactImage(@PathVariable Long id) {
        Optional<Contact> existingContact = contactService.getContactById(id);
        if (existingContact.isPresent()) {
            Contact contact = existingContact.get();
            if (contact.getImage() != null) {
                ByteArrayResource resource = new ByteArrayResource(contact.getImage().getBytes());

                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE);

                return ResponseEntity.ok()
                        .headers(headers)
                        .body(resource);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // Upload the image of a contact
    @PostMapping("/{id}/image/upload")
    public ResponseEntity<Void> uploadContactImage(@PathVariable Long id,
                                                   @RequestParam("image") MultipartFile imageFile) {
        try {
            Optional<Contact> existingContact = contactService.getContactById(id);
            if (existingContact.isPresent()) {
                Contact contact = existingContact.get();

                String image = contactService.processImage(imageFile);

                contact.setImage(image);
                contactService.updateContact(id, contact);

                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Export contacts to JSON
    @GetMapping("/export")
    public ResponseEntity<Resource> exportContacts() throws IOException {
        List<Contact> contacts = contactService.getAllContacts();

        String exportedData = contactService.convertContactsToJson(contacts);

        ByteArrayResource resource = new ByteArrayResource(exportedData.getBytes());

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contacts.json");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .body(resource);
    }

    // Import contacts from JSON
    @PostMapping("/import")
    public ResponseEntity<Void> importContacts(@RequestParam("file") MultipartFile file) {
        try {
            String importedData = new String(file.getBytes());

            List<Contact> importedContacts = contactService.parseJsonToContacts(importedData);

            for (Contact contact : importedContacts) {
                contactService.createContact(contact);
            }

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
