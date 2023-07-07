package com.example.phone_contacts.repository;

import com.example.phone_contacts.model.Contact;
import com.example.phone_contacts.model.Email;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailRepository extends JpaRepository<Email, Long> {
    Optional<Email> findByAddressAndContact(String address, Contact contact);
}
