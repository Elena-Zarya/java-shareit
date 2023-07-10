package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    private long itemId;
    @FutureOrPresent
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime start;
    @Future
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime end;
}
