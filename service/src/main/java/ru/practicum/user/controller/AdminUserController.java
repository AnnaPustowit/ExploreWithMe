package ru.practicum.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exeption.ConflictException;
import ru.practicum.exeption.InvalidParameterException;
import ru.practicum.exeption.EntityNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class AdminUserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody @Valid User user) throws InvalidParameterException, ConflictException {
        User newUser = userService.createUser(user);
        log.info("Создание пользователя: {}: ", user);
        return newUser;
    }

    @GetMapping
    public List<User> getAllUsers(@RequestParam(required = false) String ids,
                                  @RequestParam(defaultValue = "10") @Positive Integer size,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer from) {
        List<User> users = userService.getAllUsers(ids, from, size);
        log.info("Получение списка пользователей");
        return users;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public User deleteUserById(@Positive @PathVariable Long id) throws EntityNotFoundException, InvalidParameterException {
        User removeUser = userService.deleteUserById(id);
        log.info("Удаление пользователя по id {}: ", id);
        return removeUser;
    }
}
