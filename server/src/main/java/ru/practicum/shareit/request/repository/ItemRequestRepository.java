package ru.practicum.shareit.request.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;
import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    Collection<ItemRequest> findAllByRequestorIdOrderByCreatedDesc(long userId);

    @Query(value = "select * " +
            "from requests " +
            "where requestor_id > ?1 Or requestor_id < ?1 " +
            "order by created DESC", nativeQuery = true)
    List<ItemRequest> findAll(long userId, PageRequest page);
}
