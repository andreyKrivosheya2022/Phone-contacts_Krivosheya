package com.example.phone_contacts.service;

import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.repository.ContactRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;


@Service
public class ContactService {
    private final ContactRepository contactRepository;

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    /**
     * Creates a new contact.
     *
     * @param contact The contact to be created.
     * @return The created contact.
     */
    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    /**
     * Updates an existing contact.
     *
     * @param id            The ID of the contact to be updated.
     * @param updatedContact The updated contact information.
     * @return The updated contact if it exists, null otherwise.
     */
    public Contact updateContact(Long id, Contact updatedContact) {
        Optional<Contact> existingContactOptional = contactRepository.findById(id);
        if (existingContactOptional.isPresent()) {
            Contact existingContact = existingContactOptional.get();
            existingContact.setName(updatedContact.getName());
            existingContact.setEmails(updatedContact.getEmails());
            existingContact.setPhoneNumbers(updatedContact.getPhoneNumbers());
            existingContact.setImage(updatedContact.getImage());

            return contactRepository.save(existingContact);
        }
        return null;
    }

    /**
     * Deletes a contact by its ID.
     *
     * @param id The ID of the contact to be deleted.
     */
    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }

    /**
     * Retrieves a list of all contacts.
     *
     * @return A list of all contacts.
     */
    public List<Contact> getAllContacts() {
        return contactRepository.findAll();
    }

    /**
     * Retrieves a contact by its ID.
     *
     * @param id The ID of the contact.
     * @return The contact with the specified ID if it exists, otherwise empty optional.
     */
    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    /**
     * Retrieves a contact by its name.
     *
     * @param name The name of the contact.
     * @return The contact with the specified name if it exists, otherwise empty optional.
     */
    public Optional<Contact> getContactByName(String name) {
        return contactRepository.findByName(name);
    }

    /**
     * Converts a list of contacts to JSON format.
     *
     * @param contacts The list of contacts to be converted.
     * @return The JSON representation of the contacts.
     * @throws JsonProcessingException if an error occurs during JSON processing.
     */
    public String convertContactsToJson(List<Contact> contacts) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(contacts);
    }

    /**
     * Parses JSON data into a list of contacts.
     *
     * @param json The JSON data to be parsed.
     * @return The list of contacts parsed from the JSON data.
     * @throws IOException if an error occurs during JSON parsing.
     */
    public List<Contact> parseJsonToContacts(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Contact.class));
    }

    /**
     * Processes and saves an image file for a contact.
     *
     * @param imageFile The image file to be processed.
     * @return The file path of the saved image.
     * @throws IOException if an error occurs during file processing.
     */
    public String processImage(MultipartFile imageFile) throws IOException {
        if (imageFile.isEmpty()) {
            return null;
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(imageFile.getOriginalFilename()));
        String fileExtension = getFileExtension(fileName);
        String uniqueFileName = generateUniqueFileName(fileExtension);

        Path uploadDir = Paths.get("uploads");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(uniqueFileName);
        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    /**
     * Retrieves the file extension from a file name.
     *
     * @param fileName The file name.
     * @return The file extension.
     */
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return null;
    }

    /**
     * Generates a unique file name with the specified file extension.
     *
     * @param fileExtension The file extension.
     * @return The unique file name.
     */
    private String generateUniqueFileName(String fileExtension) {
        String uniqueId = UUID.randomUUID().toString();
        return uniqueId + "." + fileExtension;
    }
}
