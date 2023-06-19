package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Collection<Item> findAllByNameContainingIgnoreCaseAndAvailable(String text, boolean available);

    Collection<Item> findAllByDescriptionContainingIgnoreCaseAndAvailable(String text, boolean available);

    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId);
}