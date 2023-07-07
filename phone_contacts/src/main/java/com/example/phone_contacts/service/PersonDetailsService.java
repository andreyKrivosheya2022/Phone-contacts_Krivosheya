package com.example.phone_contacts.service;

import com.example.phone_contacts.model.Person;
import com.example.phone_contacts.repository.PersonRepository;
import com.example.phone_contacts.security.PersonDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonDetailsService implements UserDetailsService {
    private final PersonRepository personRepository;

    /**
     * Loads the user details by username.
     *
     * @param username The username of the person.
     * @return The UserDetails object containing the person's details.
     * @throws UsernameNotFoundException If the person with the specified username is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Person> personInfo = Optional.ofNullable(personRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username)));
        return new PersonDetails(personInfo.get());
    }
}
