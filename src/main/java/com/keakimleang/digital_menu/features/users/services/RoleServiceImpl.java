package com.keakimleang.digital_menu.features.users.services;


import com.keakimleang.digital_menu.exceptions.*;
import com.keakimleang.digital_menu.features.users.entities.*;
import com.keakimleang.digital_menu.features.users.enums.*;
import com.keakimleang.digital_menu.features.users.payloads.response.*;
import com.keakimleang.digital_menu.features.users.repos.*;
import java.util.*;
import java.util.stream.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleServiceImpl {
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public Role findByName(AuthRole name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name: " + name));
    }

    @Transactional(readOnly = true)
    public List<Role> findByNames(List<AuthRole> names) {
        return roleRepository.findByNameIn(names);
    }

    @Transactional(readOnly = true)
    public List<Role> findByIds(List<UUID> ids) {
        return roleRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getRolesBasedOnUserRole(User user) {
        List<Role> roles = roleRepository.findAll();
        Set<AuthRole> userRoles = user.getRoles().stream().map(Role::getName).collect(Collectors.toSet());
        if (userRoles.contains(AuthRole.admin)) {
            return RoleResponse.fromEntities(roles);
        } else if (userRoles.contains(AuthRole.manager)) {
            return RoleResponse.fromEntitiesExclude(roles, AuthRole.admin);
        }
        return null;
    }

    public Optional<Role> findById(UUID id) {
        return roleRepository.findById(id);
    }
}