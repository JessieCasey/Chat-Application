package com.chat.doubleA.service.impl;

import com.chat.doubleA.entities.Role;
import com.chat.doubleA.exceptions.NullEntityReferenceException;
import com.chat.doubleA.repositories.RoleRepository;
import com.chat.doubleA.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role create(Role role) {
        if (role != null) {
            return roleRepository.save(role);
        }
        throw new NullEntityReferenceException("Role cannot be 'null'");
    }

    @Override
    public Role readByName(String name) {
        return roleRepository.findByName(name).orElseThrow(
                () -> new IllegalArgumentException("Role with name '" + name + "' was not found"));
    }
}
