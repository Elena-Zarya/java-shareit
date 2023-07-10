package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentTest {

    @InjectMocks
    private Comment comment;
    private User user;
    private Item item;
    private LocalDateTime created;

    @BeforeEach
    void start() {
        user = new User(1L, "name", "user@email.com");
        item = new Item(1L, "name", "description", true, user, null);
        created = LocalDateTime.of(2023, 6, 7, 15, 0);
    }

    @Test
    void getId() {
        comment.setId(1L);
        Long id = comment.getId();
        assertEquals(1L, id);
    }

    @Test
    void getText() {
        comment.setText("comment");
        String text = comment.getText();
        assertEquals("comment", text);
    }

    @Test
    void getItem() {
        comment.setItem(item);
        Item item2 = comment.getItem();
        assertEquals(item, item2);
    }

    @Test
    void getAuthor() {
        comment.setAuthor(user);
        User user2 = comment.getAuthor();
        assertEquals(user, user2);
    }

    @Test
    void getCreated() {
        comment.setCreated(created);
        LocalDateTime created2 = comment.getCreated();
        assertEquals(created, created2);
    }

    @Test
    void equals() {
        Comment comment1 = new Comment();
        comment1.setId(comment.getId());
        comment1.setCreated(comment.getCreated());
        comment1.setAuthor(comment.getAuthor());
        comment1.setText(comment.getText());
        comment1.setItem(comment.getItem());

        assertTrue(comment.equals(comment1));
    }

    @Test
    void testHashCode() {
        comment.setId(1L);
        assertTrue(comment.hashCode() > 0);
    }
}