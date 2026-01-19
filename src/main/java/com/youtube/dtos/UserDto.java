package com.youtube.dtos;

import com.youtube.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserDto {
    private Long userId;

    @NotBlank(message = "Name cannot be blank")
    @Length(min = 2)
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please enter a valid email address")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password;

    private String s3KeyProfileImage;

    private String profileImageUrl;

    private Role role;

    private Boolean isVerified;

    private String verifyCode;

    private Instant verifyCodeExpiry;

    private String refreshToken;

    private Instant refreshTokenExpiry;

    private Instant createdAt;

    private Instant updatedAt;
}
