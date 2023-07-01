package ru.practicum.user.service;

import ru.practicum.exeption.InvalidParameterException;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {
    User createUser(User user) throws InvalidParameterException;

    List<User> getAllUsers(String ids, Integer size, Integer from);

    User deleteUserById(Long id);
}
