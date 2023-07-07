package com.example.phone_contacts.repository;

import com.example.phone_contacts.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    Optional<Contact> findByName(String name);
}
