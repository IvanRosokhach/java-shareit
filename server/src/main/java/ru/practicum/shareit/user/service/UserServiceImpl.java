package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;

import static ru.practicum.shareit.exception.Constant.NOT_FOUND_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        log.debug("Пользователь создан: {}.", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto read(long userId) {
        User user = getUserById(userId);
        log.debug("Пользователь с id: {} найден.", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public Collection<UserDto> readAll() {
        List<User> allUsers = userRepository.findAll();
        log.debug("Всего пользователей найдено: {}.", allUsers.size());
        return UserMapper.listToUserDto(allUsers);
    }

    @Transactional
    @Override
    public UserDto update(long userId, UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        User userForUpdate = getUserById(userId);

        if (user.getEmail() != null) {
            userForUpdate.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userForUpdate.setName(user.getName());
        }
        User updated = userRepository.save(userForUpdate);
        log.debug("Пользователь с id: {} обновлен.", userId);
        return UserMapper.toUserDto(updated);
    }

    @Override
    public void delete(long userId) {
        userIsExist(userId);
        userRepository.deleteById(userId);
        log.debug("Пользователь с id: {} удален.", userId);
    }

    @Override
    public void userIsExist(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(NOT_FOUND_USER, userId));
        }
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(NOT_FOUND_USER, userId)));
    }

}
