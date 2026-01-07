package com.youtube.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "role_master")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Column(nullable = false, unique = true)
    private String roleName;
}
