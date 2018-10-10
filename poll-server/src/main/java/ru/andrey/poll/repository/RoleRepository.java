package ru.andrey.poll.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.andrey.poll.model.Role;
import ru.andrey.poll.model.RoleName;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(RoleName name);
}
