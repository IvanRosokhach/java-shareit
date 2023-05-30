package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@DataJpaTest
class RequestRepositoryIT {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    private User user;

    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        user = userRepository.save(User.builder()
                .name("name")
                .email("mail@mail.ru")
                .build());

        Item item = itemRepository.save(Item.builder()
                .name("name")
                .description("desc")
                .available(true)
                .owner(user)
                .build());

        itemRequest = requestRepository.save(ItemRequest.builder()
                .description("hammer")
                .requestor(user)
                .created(LocalDateTime.now())
                .build());
    }

    @Test
    void findAllByRequestorId() {
        Collection<ItemRequest> allByRequestorId = requestRepository.findAllByRequestorId(user.getId());

        Assertions.assertEquals(List.of(itemRequest), allByRequestorId);
    }

    @Test
    void findAllByRequestorIdIsNot() {
        List<ItemRequest> allByRequestorIdIsNot = requestRepository.findAllByRequestorIdIsNot(user.getId(), Pageable.unpaged());

        Assertions.assertTrue(allByRequestorIdIsNot.isEmpty());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        requestRepository.deleteAll();
    }

}