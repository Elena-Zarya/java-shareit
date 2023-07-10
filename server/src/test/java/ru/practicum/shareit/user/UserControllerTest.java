package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.excrption.EmailAlreadyExistException;
import ru.practicum.shareit.excrption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;
    private UserDto userDto;

    @BeforeEach
    void start() {
        userDto = new UserDto(1L, "user", "user@email.com");
    }

    @Test
    void createUser_shouldReturnStatusOkWithUserDto() throws Exception {
        when(userService.addUser(any()))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void createUser_whenDuplicateEmail_shouldReturnStatusConflict() throws Exception {
        when(userService.addUser(any()))
                .thenThrow(new EmailAlreadyExistException("email already exist"));

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(new UserDto(null, "olga", "user@email.com")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void updateUser_shouldReturnStatusOkWithUserDto() throws Exception {
        long userId = 1L;

        when(userService.updateUser(any(), anyLong()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void updateUser_whenUserNotExist_shouldReturnStatusNotFound() throws Exception {
        long userId = 999L;

        when(userService.updateUser(any(), anyLong()))
                .thenThrow(new NotFoundException("user not found"));

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(new UserDto(null, "olga", "user@email.com")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_whenDuplicateEmail_shouldReturnStatusConflict() throws Exception {
        long userId = 1L;

        when(userService.updateUser(any(), anyLong()))
                .thenThrow(new EmailAlreadyExistException("email already exist"));

        mvc.perform(patch("/users/{userId}", userId)
                        .content(mapper.writeValueAsString(new UserDto(1L, "olga", "user@email.com")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void getUserById_shouldReturnStatusOkWithUserDto() throws Exception {
        long userId = 1L;

        when(userService.getUserById(anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void getUserById_whenUserNotExist_shouldReturnStatusNotFound() throws Exception {
        long userId = 999L;

        when(userService.getUserById(anyLong()))
                .thenThrow(new NotFoundException("user not found"));

        mvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldReturnStatusOk() throws Exception {
        long userId = 1L;

        mvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
    }

    @Test
    void findAll() throws Exception {
        when(userService.getAllUsers())
                .thenReturn(List.of(userDto));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())));
    }
}