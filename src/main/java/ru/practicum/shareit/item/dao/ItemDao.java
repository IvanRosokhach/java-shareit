package ru.practicum.shareit.item.dao;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ItemDao {
    private final Map<Long, Item> items = new HashMap<>();
    private long lastId = 0;

    public Item create(long userId, Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        return item;
    }

    public Item read(long id) {
        return items.get(id);
        //Информацию о вещи может просмотреть любой пользователь.
    }

    public Collection<Item> readAll(long userId) {
        return items.values().stream().filter(item -> item.getOwner() == userId).collect(Collectors.toList());
        //Просмотр владельцем списка всех его вещей с указанием названия и описания для каждой.
    }

    public Item update(long userId, long id, ItemDto itemDto) {
        isExist(id);
        Item updatedItem = items.get(id);
        if (userId != updatedItem.getOwner()) {
            throw new NotFoundException("Not owner");
        }
        if (itemDto.getName() != null) {
            updatedItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            updatedItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            updatedItem.setAvailable(Boolean.parseBoolean(itemDto.getAvailable()));
        }
        return updatedItem;
        //Изменить можно название, описание и статус доступа к аренде. Редактировать вещь может только её владелец.
    }

    public void delete(long id) {
        isExist(id);
        items.remove(id);
    }

    private long getId() {
//        long lastId = users.values().stream()
//                .mapToLong(User::getId)
//                .max()
//                .orElse(0);
        return ++lastId;
    }

    public void isExist(long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item not found");
        }
    }

    public List<Item> search(String text) {
        if (text.isBlank() || text.isEmpty()) {
            return List.of();
        }
        final String textS = text.toLowerCase();
        return items.values().stream()
                .filter(Item::isAvailable)
                .filter(item -> item.getName().toLowerCase().contains(textS) || item.getDescription().toLowerCase().contains(textS))
                .collect(Collectors.toList());
        //Система ищет вещи, содержащие этот текст в названии или описании.
        //Проверьте, что поиск возвращает только доступные для аренды вещи.
    }

}
