package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperImplTest {

    @InjectMocks
    private UserMapperImpl userMapper;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void start() {
        user = new User(1L, "name", "user@email.com");
        userDto = new UserDto(1L, "name", "user@email.com");
    }

    @Test
    void dtoToUser() {
        User userSaved = userMapper.dtoToUser(userDto);
        assertEquals(1L, userSaved.getId());
        assertEquals("name", userSaved.getName());
        assertEquals("user@email.com", userSaved.getEmail());
    }

    @Test
    void userToDto() {
        UserDto userDtoSaved = userMapper.userToDto(user);
        assertEquals(1L, userDtoSaved.getId());
        assertEquals("name", userDtoSaved.getName());
        assertEquals("user@email.com", userDtoSaved.getEmail());
    }
}