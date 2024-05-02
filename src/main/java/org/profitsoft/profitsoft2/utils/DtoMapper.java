package org.profitsoft.profitsoft2.utils;

import org.profitsoft.profitsoft2.database.dto.PhotoDto;
import org.profitsoft.profitsoft2.database.dto.UserDto;
import org.profitsoft.profitsoft2.database.entity.Photo;
import org.profitsoft.profitsoft2.database.entity.User;

public class DtoMapper {

    public PhotoDto mapPhotoToDto(Photo photo) {
        return new PhotoDto(photo.getId(),
                photo.getPhotoName(),
                photo.getPhotoFormat(),
                photo.getPhotoPath(),
                photo.getPhotoDescription(),
                photo.getPhotoTags(),
                photo.getUploadDate(),
                mapUserToDto(photo.getUser()));
    }

    public PhotoDto mapPhotoToShortDto(Photo photo) {
        return new PhotoDto(photo.getId(),
                photo.getPhotoName(),
                photo.getPhotoFormat(),
                null,
                null,
                null,
                photo.getUploadDate(),
                mapUserToDto(photo.getUser()));
    }

    public UserDto mapUserToDto(User user) {
        return new UserDto(user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getJoinDate(), user.getRole());
    }
}
