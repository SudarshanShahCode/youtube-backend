package com.youtube.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500, unique = true)
    @Email(message = "Please enter a valid email address")
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 1000)
    private String s3KeyProfileImage;

    @Column(columnDefinition = "TEXT")
    private String profileImageUrl;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @ToString.Exclude
    private Role role;

    private Boolean isVerified;

    private String verifyCode;

    private Instant verifyCodeExpiry;

    @Column(length = 1000)
    private String refreshToken;

    private Instant refreshTokenExpiry;

    @CreationTimestamp(source = SourceType.DB)
    private Instant createdAt;

    @UpdateTimestamp(source = SourceType.DB)
    private Instant updatedAt;
}
