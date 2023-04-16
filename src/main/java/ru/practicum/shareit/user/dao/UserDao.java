package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class UserDao {
    private long lastId = 0;
    private final Map<Long, User> users = new HashMap<>();

    public User create(User user) {
        checkEmail(user);
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    public User read(long id) {
        return users.get(id);
    }

    public Collection<User> readAll() {
        return users.values();
    }

    public User update(long id, User user) {
        isExist(id);
        User updatedUser = users.get(id);

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            users.remove(id);
//            try {
//                checkEmail(user);
//            } catch (EmailExistException e) {
            if (checkEmailBool(user)) {
                users.put(updatedUser.getId(), updatedUser);
                throw new EmailExistException("Email duplicate error");
            }
            updatedUser.setEmail(user.getEmail());
            users.put(updatedUser.getId(), updatedUser);
        }
        return updatedUser;
    }

    public void delete(long id) {
        isExist(id);
        users.remove(id);
    }

    private long getId() {
//        long lastId = users.values().stream()
//                .mapToLong(User::getId)
//                .max()
//                .orElse(0);
        return ++lastId;
    }

    public void isExist(long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User not found");
        }
    }

    private void checkEmail(User user) throws RuntimeException {
        if (users.values()
                .stream().map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()))) {
            throw new EmailExistException("Email error");
        }
    }

    private boolean checkEmailBool(User user) {
        return users.values()
                .stream().map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()));
    }

}
