package com.example.phone_contacts.repository;

import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.model.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhoneNumberRepository extends JpaRepository<PhoneNumber, Long> {
    Optional<PhoneNumber> findByNumberAndContact(String number, Contact contact);
}
