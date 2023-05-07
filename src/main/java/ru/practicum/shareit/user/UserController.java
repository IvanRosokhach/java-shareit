package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@RequestBody @Validated UserDto userDto) {
        log.debug("Create");
        return UserMapper.toUserDto(userService.create(userDto));
    }

    @GetMapping("/{userId}")
    public UserDto read(@PathVariable long userId) {
        log.debug("Read({})", userId);
        return UserMapper.toUserDto(userService.read(userId));
    }

    @GetMapping
    public Collection<UserDto> readAll() {
        log.debug("ReadAll");
        return userService.readAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId,
                          @RequestBody UserDto userDto) {
        log.debug("Update({})", userId);
        return UserMapper.toUserDto(userService.update(userId, userDto));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.debug("Delete({})", userId);
        userService.delete(userId);
    }

}
