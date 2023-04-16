package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserServiceImpl userService;

    @PostMapping
    public UserDto create(@RequestBody @Validated User user) {
        return UserMapper.toUserDto(userService.create(user));
    }

    @GetMapping("/{id}")
    public UserDto read(@PathVariable long id) {
        return UserMapper.toUserDto(userService.read(id));
    }

    @GetMapping
    public Collection<UserDto> readAll() {
        return userService.readAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable long id, @RequestBody User user) {
        return UserMapper.toUserDto(userService.update(id, user));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }

}
