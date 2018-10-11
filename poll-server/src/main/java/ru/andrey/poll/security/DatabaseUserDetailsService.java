package ru.andrey.poll.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.andrey.poll.repository.UserRepository;

@Service
@Primary
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public DatabaseUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .map(UserPrincipal::create)
                .orElseThrow(() -> usernameNotFound(usernameOrEmail));
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        return userRepository.findById(id)
                .map(UserPrincipal::create)
                .orElseThrow(() -> idNotFound(id));
    }

    private UsernameNotFoundException usernameNotFound(String usernameOrEmail) {
        return new UsernameNotFoundException("Username not found by: " + usernameOrEmail);
    }

    private UsernameNotFoundException idNotFound(Long id) {
        return new UsernameNotFoundException("No user with given id: " + id);
    }
}
