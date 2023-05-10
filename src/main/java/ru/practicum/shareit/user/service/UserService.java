package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User create(UserDto userDto);

    User read(long userId);

    Collection<User> readAll();

    User update(long userId, UserDto userDto);

    void delete(long userId);

}
