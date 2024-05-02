package org.profitsoft.profitsoft2.database.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Value;
import org.profitsoft.profitsoft2.database.entity.Photo;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link Photo}
 */
@Value
public class PhotoDto implements Serializable {
    @NotNull
    @Positive
    Integer id;
    @NotBlank
    String photoName;
    @NotBlank
    String photoFormat;
    @NotBlank
    String photoPath;
    String photoDescription;
    String photoTags;
    @PastOrPresent
    LocalDate uploadDate;
    @NotNull
    UserDto user;

}
