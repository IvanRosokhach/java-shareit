package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryIT {

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("name")
                .email("mail@mail.ru")
                .build());
    }

    @Test
    void findUserById() {
        Optional<User> actual = userRepository.findById(user.getId());

        assertTrue(actual.isPresent());
        assertEquals(user.getId(), actual.get().getId());
        assertEquals(user.getName(), actual.get().getName());
        assertEquals(user.getEmail(), actual.get().getEmail());
    }

    @AfterEach
    void deleteUsers() {
        userRepository.deleteAll();
    }

}