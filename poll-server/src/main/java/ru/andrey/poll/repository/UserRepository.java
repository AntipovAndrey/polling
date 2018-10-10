package ru.andrey.poll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andrey.poll.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    List<User> findByIdIn(List<Long> ids);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}
