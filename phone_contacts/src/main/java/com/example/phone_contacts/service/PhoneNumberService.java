package com.example.phone_contacts.service;

import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.model.PhoneNumber;
import com.example.phone_contacts.repository.PhoneNumberRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PhoneNumberService {
    private final PhoneNumberRepository phoneNumberRepository;

    public PhoneNumberService(PhoneNumberRepository phoneNumberRepository) {
        this.phoneNumberRepository = phoneNumberRepository;
    }

    /**
     * Creates a new phone number.
     *
     * @param phoneNumber The phone number to be created.
     * @return The created phone number.
     */
    public PhoneNumber createPhoneNumber(PhoneNumber phoneNumber) {
        return phoneNumberRepository.save(phoneNumber);
    }

    /**
     * Deletes a phone number by its ID.
     *
     * @param id The ID of the phone number to be deleted.
     */
    public void deletePhoneNumber(Long id) {
        phoneNumberRepository.deleteById(id);
    }

    /**
     * Retrieves a phone number by its ID.
     *
     * @param id The ID of the phone number.
     * @return An optional containing the phone number, or an empty optional if not found.
     */
    public Optional<PhoneNumber> getPhoneNumberById(Long id) {
        return phoneNumberRepository.findById(id);
    }

    /**
     * Retrieves a phone number by its number and contact.
     *
     * @param number  The phone number.
     * @param contact The associated contact.
     * @return An optional containing the phone number, or an empty optional if not found.
     */
    public Optional<PhoneNumber> getPhoneNumberByNumberAndContact(String number, Contact contact) {
        return phoneNumberRepository.findByNumberAndContact(number, contact);
    }

    /**
     * Retrieves all phone numbers.
     *
     * @return A list of all phone numbers.
     */
    public List<PhoneNumber> getAllPhoneNumbers() {
        return phoneNumberRepository.findAll();
    }

    /**
     * Updates a phone number.
     *
     * @param id                The ID of the phone number to be updated.
     * @param updatedPhoneNumber The updated phone number object.
     * @return An optional containing the updated phone number, or an empty optional if not found.
     */
    public Optional<PhoneNumber> updatePhoneNumber(Long id, PhoneNumber updatedPhoneNumber) {
        Optional<PhoneNumber> existingPhoneNumber = phoneNumberRepository.findById(id);
        if (existingPhoneNumber.isPresent()) {
            PhoneNumber phoneNumber = existingPhoneNumber.get();
            phoneNumber.setId(updatedPhoneNumber.getId());
            phoneNumber.setNumber(updatedPhoneNumber.getNumber());
            phoneNumber.setContact(updatedPhoneNumber.getContact());
            return Optional.of(phoneNumberRepository.save(phoneNumber));
        } else {
            return Optional.empty();
        }
    }
}
