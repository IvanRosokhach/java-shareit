package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto read(long userId);

    Collection<UserDto> readAll();

    UserDto update(long userId, UserDto userDto);

    void delete(long userId);

}
