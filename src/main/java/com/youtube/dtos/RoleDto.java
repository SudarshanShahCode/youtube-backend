package com.youtube.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class RoleDto {
    private Long roleId;
    @NotBlank(message = "Role name cannot be blank")
    private String roleName;
}
