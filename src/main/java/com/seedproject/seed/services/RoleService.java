package com.seedproject.seed.services;

import com.seedproject.seed.models.entities.Role;
import com.seedproject.seed.repositories.RoleRepository;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service
public class RoleService {
    @Inject
    RoleRepository roleRepository;

    public List<Role> getRoles() {
        return roleRepository.findAll();
    }
}
