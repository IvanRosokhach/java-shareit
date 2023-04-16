package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserService {

    User create(User user);

    User read(long id);

    Collection<User> readAll();

    User update(long id, User user);

    void delete(long id);

}
