package com.youtube.services.impl;

import com.youtube.dtos.RoleDto;
import com.youtube.entities.Role;
import com.youtube.repositories.RoleRepository;
import com.youtube.services.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDto createRole(RoleDto roleDto) {
        var role = Role.builder()
                .roleName(roleDto.getRoleName())
                .build();

        Role savedRole = roleRepository.save(role);

        return RoleDto.builder()
                .roleId(savedRole.getRoleId())
                .roleName(savedRole.getRoleName())
                .build();
    }

    @Override
    public List<RoleDto> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(role ->
                        RoleDto.builder()
                .roleId(role.getRoleId())
                .roleName(role.getRoleName())
                .build()).toList();
    }
}
