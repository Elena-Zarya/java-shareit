package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByNameContainingIgnoreCaseAndAvailable(String text, boolean available, Pageable page);

    List<Item> findAllByDescriptionContainingIgnoreCaseAndAvailable(String text, boolean available, Pageable page);

    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId, Pageable page);

    List<Item> findAllByRequestIdOrderById(long requestId);
}