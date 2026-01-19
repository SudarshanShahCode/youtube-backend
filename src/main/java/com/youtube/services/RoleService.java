package com.youtube.services;

import com.youtube.dtos.RoleDto;

import java.util.List;

public interface RoleService {

    RoleDto createRole(RoleDto roleDto);

    List<RoleDto> getAllRoles();
}
