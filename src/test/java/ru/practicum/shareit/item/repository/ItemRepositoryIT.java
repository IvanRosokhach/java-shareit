package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ItemRepositoryIT {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    private Item item;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("name")
                .email("mail@mail.ru")
                .build());

        item = itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .available(true)
                .owner(user)
                .build());
    }

    @Test
    void search() {
        List<Item> searched = itemRepository.search("DeSc", Pageable.unpaged());

        assertEquals(List.of(item), searched);
    }

    @Test
    void searchWhenNotExistParam() {
        List<Item> searched = itemRepository.search("item", Pageable.unpaged());

        assertEquals(0, searched.size());
    }

    @Test
    void findAllByOwnerId() {
        List<Item> allByOwnerId = itemRepository.findAllByOwnerId(user.getId(), Pageable.unpaged());

        assertEquals(List.of(item), allByOwnerId);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

}