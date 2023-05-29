package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private final long id = 1L;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("name")
                .email("mail@mail.ru")
                .build();
    }

    @Test
    void createWhenInvokeThenSavedUser() {
        User user = UserMapper.toUser(userDto);
        when(userRepository.save(user)).thenReturn(user);

        UserDto actual = userService.create(userDto);

        verify(userRepository).save(user);
        assertEquals(userDto, actual);
    }

    @Test
    void readWhenUserFoundThenReturnUser() {
        User user = UserMapper.toUser(userDto);
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        UserDto actual = userService.read(id);

        assertEquals(userDto, actual);
    }

    @Test
    void readWhenUserNotFoundThenNotFoundExceptionThrown() {
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.read(id));
    }

    @Test
    void readAllWhenInvokeThenReturnUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User()));

        Collection<UserDto> actual = userService.readAll();

        assertEquals(1, actual.size());
    }

    @Test
    void updateWhenUserExistThenUserUpdatedAndSave() {
        User oldUser = UserMapper.toUser(userDto);
        UserDto newUser = new UserDto(0L, "nameUpdated", "updated@mail.ru");
        when(userRepository.findById(id)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(oldUser)).thenReturn(oldUser);

        UserDto updated = userService.update(id, newUser);

        verify(userRepository).save(oldUser);
        assertEquals(newUser.getName(), updated.getName());
        assertEquals(newUser.getEmail(), updated.getEmail());
    }

    @Test
    void updateWhenUserExistThenUserUpdatedEmailAndSave() {
        User oldUser = UserMapper.toUser(userDto);
        UserDto newUser = new UserDto(0L, null, "updated@mail.ru");
        when(userRepository.findById(id)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(oldUser)).thenReturn(oldUser);

        UserDto updated = userService.update(id, newUser);

        verify(userRepository).save(oldUser);
        assertEquals(oldUser.getName(), updated.getName());
        assertEquals(newUser.getEmail(), updated.getEmail());
    }

    @Test
    void updateWhenUserExistThenUserUpdatedNameAndSave() {
        User oldUser = UserMapper.toUser(userDto);
        UserDto newUser = new UserDto(0L, "nameUpdated", null);
        when(userRepository.findById(id)).thenReturn(Optional.of(oldUser));
        when(userRepository.save(oldUser)).thenReturn(oldUser);

        UserDto updated = userService.update(id, newUser);

        verify(userRepository).save(oldUser);
        assertEquals(newUser.getName(), updated.getName());
        assertEquals(oldUser.getEmail(), updated.getEmail());
    }

    @Test
    void deleteWhenUserExistThenUserDelete() {
        when(userRepository.existsById(id)).thenReturn(true);

        userService.delete(id);

        verify(userRepository).deleteById(id);
    }

    @Test
    void deleteWhenUserNotExistThenNotFoundExceptionThrow() {
        when(userRepository.existsById(id)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.delete(id));
    }

    @Test
    void getUserByIdWhenUserNotExistThenNotFoundExceptionThrow() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUserById(id));
    }

}