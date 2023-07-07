package com.example.phone_contacts.service;

import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.model.Email;
import com.example.phone_contacts.repository.EmailRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmailService {
    private final EmailRepository emailRepository;

    /**
     * Creates a new email.
     *
     * @param email The email to be created.
     * @return The created email.
     */
    public Email createEmail(Email email) {
        return emailRepository.save(email);
    }

    /**
     * Deletes an email by its ID.
     *
     * @param id The ID of the email to be deleted.
     */
    public void deleteEmail(Long id) {
        emailRepository.deleteById(id);
    }

    /**
     * Retrieves an email by its ID.
     *
     * @param id The ID of the email.
     * @return The email with the specified ID if it exists, otherwise empty optional.
     */
    public Optional<Email> getEmailById(Long id) {
        return emailRepository.findById(id);
    }

    /**
     * Retrieves an email by its address and associated contact.
     *
     * @param address The email address.
     * @param contact The associated contact.
     * @return The email with the specified address and contact if it exists, otherwise empty optional.
     */
    public Optional<Email> getEmailByAddressAndContact(String address, Contact contact) {
        return emailRepository.findByAddressAndContact(address, contact);
    }

    /**
     * Retrieves a list of all emails.
     *
     * @return A list of all emails.
     */
    public List<Email> getAllEmails() {
        return emailRepository.findAll();
    }

    /**
     * Updates an existing email.
     *
     * @param id            The ID of the email to be updated.
     * @param updatedEmail The updated email information.
     * @return The updated email if it exists, otherwise empty optional.
     */
    public Optional<Email> updateEmail(Long id, Email updatedEmail) {
        Optional<Email> existingEmail = emailRepository.findById(id);
        if (existingEmail.isPresent()) {
            Email email = existingEmail.get();
            email.setId(updatedEmail.getId());
            email.setAddress(updatedEmail.getAddress());
            email.setContact(updatedEmail.getContact());
            return Optional.of(emailRepository.save(email));
        } else {
            return Optional.empty();
        }
    }
}
