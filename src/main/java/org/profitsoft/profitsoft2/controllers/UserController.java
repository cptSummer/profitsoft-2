package org.profitsoft.profitsoft2.controllers;

import lombok.RequiredArgsConstructor;
import org.profitsoft.profitsoft2.database.dto.UserDto;
import org.profitsoft.profitsoft2.database.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public void createUser(@RequestParam("username") String username,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password) {
        userService.createUser(username, email, password);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Integer id) {
        userService.deleteUser(id);
    }

    @PutMapping("/{id}")
    public void updateUser(@PathVariable("id") Integer id,
                           @RequestParam(value = "username", required = false) String username,
                           @RequestParam(value = "email", required = false) String email,
                           @RequestParam(value = "password", required = false) String password) {
        userService.updateUser(id, username, email, password);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }
}
