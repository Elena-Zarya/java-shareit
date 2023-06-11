package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.List;

@Data
public class UserDto {
    private long id;
    private String name;
    @Email
    private String email;
    @JsonIgnore
    private final List<Long> itemsList = new ArrayList<>();
}
