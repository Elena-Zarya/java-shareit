//package ru.practicum.shareit.request.repository;
//
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Sort;
//import org.springframework.test.context.jdbc.Sql;
//import ru.practicum.shareit.request.model.ItemRequest;
//import ru.practicum.shareit.user.model.User;
//import ru.practicum.shareit.user.repository.UserRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@DataJpaTest
//@Sql({"/schema.sql"})
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.AUTO_CONFIGURED)
//class ItemRequestRepositoryTest {
//
//    @Autowired
//    private ItemRequestRepository itemRequestRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    @Test
//    void findAllShouldReturnItemRequestsId1AndId2() {
//        int from = 0;
//        int size = 2;
//
//        User user = new User(1L, "name", "user@email.com");
//        User userSaved = userRepository.save(user);
//
//        LocalDateTime created = LocalDateTime.of(2023, 6, 15, 15, 0);
//        ItemRequest itemRequest = new ItemRequest(1L, "description", userSaved, created);
//        ItemRequest itemRequestSaved = itemRequestRepository.save(itemRequest);
//
//        LocalDateTime created2 = LocalDateTime.of(2023, 7, 15, 15, 0);
//        ItemRequest itemRequest2 = new ItemRequest(2L, "description", userSaved, created2);
//        ItemRequest itemRequest2Saved = itemRequestRepository.save(itemRequest2);
//
//        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
//        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sortByCreated);
//
//
//        List<ItemRequest> itemRequestList = itemRequestRepository.findAll(0L, page);
//
//        assertEquals(2, itemRequestList.size());
//        assertEquals(itemRequest2Saved.getId(), itemRequestList.get(0).getId());
//        assertEquals(itemRequestSaved.getId(), itemRequestList.get(1).getId());
//        assertEquals("description", itemRequestList.get(1).getDescription());
//        assertEquals(userSaved, itemRequestList.get(1).getRequestor());
//        assertEquals(created, itemRequestList.get(1).getCreated());
//    }
//
//    @Test
//    void findAllShouldReturnItemRequestId1() {
//        int from = 1;
//        int size = 1;
//
//        User user = new User(1L, "name", "user@email.com");
//        User userSaved = userRepository.save(user);
//
//        LocalDateTime created = LocalDateTime.of(2023, 6, 15, 15, 0);
//        ItemRequest itemRequest = new ItemRequest(1L, "description", userSaved, created);
//        ItemRequest itemRequestSaved = itemRequestRepository.save(itemRequest);
//
//        LocalDateTime created2 = LocalDateTime.of(2023, 7, 15, 15, 0);
//        ItemRequest itemRequest2 = new ItemRequest(2L, "description", userSaved, created2);
//        ItemRequest itemRequest2Saved = itemRequestRepository.save(itemRequest2);
//
//
//        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
//        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sortByCreated);
//
//        List<ItemRequest> itemRequestList = itemRequestRepository.findAll(0L, page);
//
//        assertEquals(1, itemRequestList.size());
//        assertEquals(itemRequestSaved.getId(), itemRequestList.get(0).getId());
//        assertEquals("description", itemRequestList.get(0).getDescription());
//        assertEquals(userSaved, itemRequestList.get(0).getRequestor());
//        assertEquals(created, itemRequestList.get(0).getCreated());
//    }
//
//    @Test
//    void findAllShouldReturnItemRequestId2() {
//        int from = 0;
//        int size = 1;
//
//        User user = new User(1L, "name", "user@email.com");
//        User userSaved = userRepository.save(user);
//
//        LocalDateTime created = LocalDateTime.of(2023, 6, 15, 15, 0);
//        ItemRequest itemRequest = new ItemRequest(1L, "description", userSaved, created);
//        ItemRequest itemRequestSaved = itemRequestRepository.save(itemRequest);
//
//        LocalDateTime created2 = LocalDateTime.of(2023, 7, 15, 15, 0);
//        ItemRequest itemRequest2 = new ItemRequest(2L, "description", userSaved, created2);
//        ItemRequest itemRequest2Saved = itemRequestRepository.save(itemRequest2);
//
//        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
//        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sortByCreated);
//
//        List<ItemRequest> itemRequestList = itemRequestRepository.findAll(0L, page);
//
//        assertEquals(1, itemRequestList.size());
//        assertEquals(itemRequest2Saved.getId(), itemRequestList.get(0).getId());
//        assertEquals("description", itemRequestList.get(0).getDescription());
//        assertEquals(userSaved, itemRequestList.get(0).getRequestor());
//        assertEquals(created2, itemRequestList.get(0).getCreated());
//    }
//
//    @Test
//    void findAllShouldReturnListIsEmpty() {
//        int from = 0;
//        int size = 1;
//
//        User user = new User(1L, "name", "user@email.com");
//        User userSaved = userRepository.save(user);
//
//        LocalDateTime created = LocalDateTime.of(2023, 6, 15, 15, 0);
//        ItemRequest itemRequest = new ItemRequest(1L, "description", userSaved, created);
//        ItemRequest itemRequestSaved = itemRequestRepository.save(itemRequest);
//
//        LocalDateTime created2 = LocalDateTime.of(2023, 7, 15, 15, 0);
//        ItemRequest itemRequest2 = new ItemRequest(2L, "description", userSaved, created2);
//        ItemRequest itemRequest2Saved = itemRequestRepository.save(itemRequest2);
//
//        Sort sortByCreated = Sort.by(Sort.Direction.DESC, "created");
//        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sortByCreated);
//
//        List<ItemRequest> itemRequestList = itemRequestRepository.findAll(userSaved.getId(), page);
//
//        assertEquals(0, itemRequestList.size());
//    }
//
//    @AfterEach
//    void deleteAll() {
//        itemRequestRepository.deleteAll();
//        userRepository.deleteAll();
//    }
//}