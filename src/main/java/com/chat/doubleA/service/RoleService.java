package com.chat.doubleA.service;


import com.chat.doubleA.entities.Role;

import java.util.List;

public interface RoleService {
    Role create(Role role);

    Role readById(String id);
}
