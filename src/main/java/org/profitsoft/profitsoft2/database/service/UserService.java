package org.profitsoft.profitsoft2.database.service;

import org.profitsoft.profitsoft2.database.dto.UserDto;
import org.profitsoft.profitsoft2.database.entity.User;
import org.profitsoft.profitsoft2.database.repository.UserRepository;
import org.profitsoft.profitsoft2.utils.DtoMapper;
import org.profitsoft.profitsoft2.utils.UtilService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService extends DefaultService<User, UserRepository> {

    private static final UtilService utilService = new UtilService();
    private static final DtoMapper dtoMapper = new DtoMapper();

    /**
     * Creates a new user with the given username, email, and password.
     *
     * @param username The username for the new user.
     * @param email The email for the new user.
     * @param password The password for the new user.
     */
    public void createUser(String username, String email, String password) {
        User user = new User();

        user.setEmail(email);
        user.setJoinDate(LocalDate.parse(utilService.getCurrentDate()));
        user.setPassword(password);
        user.setRole("ROLE_USER");
        user.setUsername(username);
        save(user);
    }

    public void deleteUser(Integer id) {
        jpaRepository.deleteById(id);
    }

    /**
     * Updates the user with the given id by setting the username, email, and password.
     *
     * @param id The id of the user to update.
     * @param username The new username for the user.
     * @param email The new email for the user.
     * @param password The new password for the user.
     */
    public void updateUser(Integer id, String username, String email, String password) {
        getById(id).ifPresent(user -> {
            if (utilService.isNotEmptyOrNull(username)) {
                user.setUsername(username);
            }
            if (utilService.isNotEmptyOrNull(email)) {
                user.setEmail(email);
            }
            if (utilService.isNotEmptyOrNull(password)) {
                user.setPassword(password);
            }
            save(user);
        });
    }

    public List<UserDto> getAllUsers() {
        return findAll().stream().map(dtoMapper::mapUserToDto).toList();
    }
}
