package com.example.phone_contacts.service;
import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.repository.ContactRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContactServiceTest {
    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactService contactService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createContact_ValidContact_ReturnsSavedContact() {
        Contact contactToCreate = new Contact();
        contactToCreate.setName("John Doe");

        Contact savedContact = new Contact();
        savedContact.setId(1L);
        savedContact.setName("John Doe");

        when(contactRepository.save(any())).thenReturn(savedContact);

        Contact createdContact = contactService.createContact(contactToCreate);

        assertNotNull(createdContact.getId());
        assertEquals(savedContact.getName(), createdContact.getName());
    }

    @Test
    void updateContact_ValidContact_ReturnsUpdatedContact() {
        Long contactId = 1L;

        Contact existingContact = new Contact();
        existingContact.setId(contactId);
        existingContact.setName("John Doe");

        Contact updatedContact = new Contact();
        updatedContact.setId(contactId);
        updatedContact.setName("Jane Doe");

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));
        when(contactRepository.save(any())).thenReturn(updatedContact);

        Contact resultContact = contactService.updateContact(contactId, updatedContact);

        assertEquals(updatedContact.getName(), resultContact.getName());
    }

    @Test
    void deleteContact_ExistingContact_DeletesContact() {
        Long contactId = 1L;

        Contact existingContact = new Contact();
        existingContact.setId(contactId);
        existingContact.setName("John Doe");

        when(contactRepository.findById(contactId)).thenReturn(Optional.of(existingContact));

        contactService.deleteContact(contactId);

        verify(contactRepository, times(1)).deleteById(contactId);
    }

    @Test
    void getAllContacts_ExistingContacts_ReturnsListOfContacts() {
        List<Contact> contacts = new ArrayList<>();
        contacts.add(new Contact(1L, "John Doe", new ArrayList<>(), new ArrayList<>(), null));
        contacts.add(new Contact(2L, "Jane Smith", new ArrayList<>(), new ArrayList<>(), null));

        when(contactRepository.findAll()).thenReturn(contacts);

        List<Contact> resultContacts = contactService.getAllContacts();

        assertEquals(contacts.size(), resultContacts.size());
        assertTrue(resultContacts.containsAll(contacts));
    }
}