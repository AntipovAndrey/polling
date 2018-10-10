package ru.andrey.poll.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.andrey.poll.model.Role;
import ru.andrey.poll.model.RoleName;
import ru.andrey.poll.repository.RoleRepository;

@Component
public class PopulateData {

    private final RoleRepository repository;

    @Autowired
    public PopulateData(RoleRepository repository) {
        this.repository = repository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void insertRoles() {
        if (!repository.findByName(RoleName.ROLE_USER).isPresent() &&
                !repository.findByName(RoleName.ROLE_ADMIN).isPresent()) {
            repository.save(new Role(RoleName.ROLE_USER));
            repository.save(new Role(RoleName.ROLE_ADMIN));
        }
    }
}
