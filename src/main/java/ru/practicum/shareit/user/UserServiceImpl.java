package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl {
    private final UserDao userDao;

    public User create(User user) {
        return userDao.create(user);
    }

    public User read(long id) {
        return userDao.read(id);
    }

    public Collection<User> readAll() {
        return userDao.readAll();
    }

    public User update(long id, User user) {
        return userDao.update(id, user);
    }

    public void delete(long id) {
        userDao.delete(id);
    }

}
