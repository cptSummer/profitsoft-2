package org.profitsoft.profitsoft2.database.dto;

import jakarta.validation.constraints.*;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link org.profitsoft.profitsoft2.database.entity.User}
 */
@Value
public class UserDto implements Serializable {
    @Positive
    @NotNull
    Integer id;
    @NotBlank
    String username;
    @NotBlank
    String password;
    @Email
    @NotBlank
    String email;
    @PastOrPresent
    LocalDate joinDate;
    @NotBlank
    String role;
}
