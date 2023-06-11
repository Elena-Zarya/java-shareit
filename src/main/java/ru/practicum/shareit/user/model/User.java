package ru.practicum.shareit.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Data
public class User {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @JsonIgnore
    private final List<Long> itemsList = new ArrayList<>();
}
