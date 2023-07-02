package ru.practicum.user.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.util.Util;
import ru.practicum.exeption.ConflictException;
import ru.practicum.exeption.InvalidParameterException;
import ru.practicum.exeption.EntityNotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@Getter
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public User createUser(User user) {
        validateUser(user);
        if (userRepository.existsUserByEmail(user.getEmail())) {
            throw new ConflictException("Уже существует пользователь с email: " + user.getEmail());
        }
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUsers(String idsString, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from / size, size);
        List<Long> usersIds = Util.getListLongFromString(idsString);

        if (usersIds != null) {
            return userRepository.findAllByIdIn(usersIds, page);
        } else {
            return userRepository.findAll(page).stream().collect(Collectors.toList());
        }
    }

    @Transactional
    @Override
    public User deleteUserById(Long userId) {
        if (userId <= 0) {
            throw new InvalidParameterException("Неккоректный id - " + userId);
        }
        Optional<User> deleteUser = userRepository.findById(userId);
        if (deleteUser.isEmpty()) {
            throw new EntityNotFoundException("Нет пользователя с id: " + userId);
        }
        userRepository.deleteById(userId);
        return deleteUser.get();
    }

    public void validateUser(User user) throws InvalidParameterException {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new InvalidParameterException("поле email должно быть заполнено.");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            throw new InvalidParameterException("поле name должно быть заполнено.");
        }
    }
}
