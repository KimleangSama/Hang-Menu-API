package io.sovann.hang.api.features.users.services;


import io.sovann.hang.api.exceptions.ResourceNotFoundException;
import io.sovann.hang.api.features.users.entities.Role;
import io.sovann.hang.api.features.users.entities.User;
import io.sovann.hang.api.features.users.enums.AuthRole;
import io.sovann.hang.api.features.users.payloads.response.RoleResponse;
import io.sovann.hang.api.features.users.repos.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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