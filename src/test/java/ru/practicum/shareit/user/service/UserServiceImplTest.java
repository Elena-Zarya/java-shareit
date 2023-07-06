package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userServiceImpl;
    @Mock
    private UserMapper userMapper;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;
    private UserDto userDto;
    private UserDto userDto2;

    @BeforeEach
    void start() {
        userDto = new UserDto(null, "user", "user@email.com");
        userDto2 = new UserDto(null, "olga", "olga@email.com");
    }

    @Test
    void addUserShouldReturnUserDto() {
        Mockito.when(userRepository.save(any())).thenReturn(new User(1L, "user", "user@email.com"));
        Mockito.when(userMapper.dtoToUser(any())).thenReturn(new User(null, "user", "user@email.com"));
        Mockito.when(userMapper.userToDto(any())).thenReturn(new UserDto(1L, "user", "user@email.com"));


        UserDto userDtoSaved = userServiceImpl.addUser(userDto);
        assertEquals(1L, userDtoSaved.getId());
        assertEquals("user", userDtoSaved.getName());
        assertEquals("user@email.com", userDtoSaved.getEmail());

        verify(userRepository, Mockito.times(1))
                .save(new User(null, "user", "user@email.com"));
    }

    @Test
    void updateUser_whenUpdateEmail_shouldReturnUserDto() {
        long userId = 1L;
        UserDto userDtoNew = new UserDto(null, null, "update@email.com");

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(1L, "user",
                "user@email.com")));

        UserDto userDtoSaved = userServiceImpl.updateUser(userDtoNew, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User userSaved = userArgumentCaptor.getValue();

        assertEquals(1L, userSaved.getId());
        assertEquals("user", userSaved.getName());
        assertEquals("update@email.com", userSaved.getEmail());

        verify(userRepository, Mockito.times(1))
                .save(new User(1L, "user", "update@email.com"));
    }

    @Test
    void updateUser_whenUpdateSameEmail_shouldReturnUserDto() {
        long userId = 1L;
        UserDto userDtoNew = new UserDto(null, null, "update@email.com");
        List<User> sourceUsers = List.of(
                new User(1L, "user", "update@email.com"),
                new User(2L, "user2", "user2@email.com"),
                new User(3L, "user3", "user3@email.com")
        );


        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(1L, "user",
                "update@email.com")));
        Mockito.when(userRepository.findAll()).thenReturn(sourceUsers);


        UserDto userDtoSaved = userServiceImpl.updateUser(userDtoNew, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User userSaved = userArgumentCaptor.getValue();

        assertEquals(1L, userSaved.getId());
        assertEquals("user", userSaved.getName());
        assertEquals("update@email.com", userSaved.getEmail());

        verify(userRepository, Mockito.times(1))
                .save(new User(1L, "user", "update@email.com"));
    }

    @Test
    void updateUser_whenUpdateName_thenReturnUserDto() {
        long userId = 1L;
        UserDto userDtoNew = new UserDto(null, "user2", null);

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(1L, "user",
                "user@email.com")));

        UserDto userDtoSaved = userServiceImpl.updateUser(userDtoNew, userId);

        verify(userRepository).save(userArgumentCaptor.capture());
        User userSaved = userArgumentCaptor.getValue();

        assertEquals(1L, userSaved.getId());
        assertEquals("user2", userSaved.getName());
        assertEquals("user@email.com", userSaved.getEmail());

        verify(userRepository, Mockito.times(1))
                .save(new User(1L, "user2", "user@email.com"));
    }

    @Test
    void updateUser_whenUserNotExist_thenReturnStatusNotFound() {
        long userId = 999L;
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userServiceImpl.updateUser(userDto, userId));
    }

    @Test
    void updateUser_whenDuplicateEmail_thenReturnEmailAlreadyExistException() {
        long userId = 1L;

        Mockito.when(userMapper.dtoToUser(any())).thenReturn(new User(null, "user", "user@email.com"));
        Mockito.when(userRepository.save(any())).thenReturn(new User(1L, "user", "user@email.com"));
        Mockito.when(userMapper.userToDto(any())).thenReturn(new UserDto(1L, "user", "user@email.com"));

        UserDto userDtoSaved = userServiceImpl.addUser(userDto);

        Mockito.when(userMapper.dtoToUser(any())).thenReturn(new User(null, "olga", "olga@email.com"));
        Mockito.when(userRepository.save(any())).thenReturn(new User(2L, "olga", "olga@email.com"));
        Mockito.when(userMapper.userToDto(any())).thenReturn(new UserDto(2L, "olga", "olga@email.com"));

        UserDto user2DtoSaved = userServiceImpl.addUser(userDto2);

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(1L, "user",
                "user@email.com")));
        Mockito.when(userRepository.findAll()).thenReturn(List.of(new User(1L, "user", "user@email.com"),
                new User(2L, "olga", "olga@email.com")));

        userDtoSaved.setEmail("olga@email.com");

        assertThrows(EmailAlreadyExistException.class, () -> userServiceImpl.updateUser(userDtoSaved, userId));
    }

    @Test
    void getUserByIdShouldReturnUserDto() {
        long userId = 1L;

        Mockito.when(userMapper.dtoToUser(any())).thenReturn(new User(null, "user", "user@email.com"));
        Mockito.when(userRepository.save(any())).thenReturn(new User(1L, "user", "user@email.com"));
        Mockito.when(userMapper.userToDto(any())).thenReturn(new UserDto(1L, "user", "user@email.com"));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(1L, "user",
                "user@email.com")));

        UserDto userDtoSaved = userServiceImpl.addUser(userDto);
        UserDto userDtoReceived = userServiceImpl.getUserById(userId);

        assertEquals(1L, userDtoReceived.getId());
        assertEquals("user", userDtoReceived.getName());
        assertEquals("user@email.com", userDtoReceived.getEmail());
    }

    @Test
    void getUserByIdShouldReturnNotFoundException() {
        long userId = 1000L;
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userServiceImpl.getUserById(userId));
    }

    @Test
    void deleteUserShouldBeOk() {
        long userId = 1L;

        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User(1L, "user",
                "user@email.com")));
        userServiceImpl.deleteUser(userId);
        verify(userRepository, Mockito.times(1)).deleteById(userId);
    }

    @Test
    void deleteUserShouldReturnNotFoundException() {
        long userId = 1000L;
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userServiceImpl.deleteUser(userId));
        verify(userRepository, never()).deleteById(userId);
    }

    @Test
    void getAllUsersShouldBeOk() {
        List<User> sourceUsers = List.of(
                new User(1L, "user", "user@email.com"),
                new User(2L, "user2", "user2@email.com"),
                new User(3L, "user3", "user3@email.com")
        );

        Mockito.when(userRepository.findAll()).thenReturn(sourceUsers);

        Collection<UserDto> targetUsers = userServiceImpl.getAllUsers();

        assertEquals(3, targetUsers.size());
        verify(userRepository, Mockito.times(1)).findAll();
    }
}