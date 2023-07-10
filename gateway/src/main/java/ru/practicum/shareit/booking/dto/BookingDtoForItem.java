package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDtoForItem {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime end;
    private Long bookerId;
    private BookingState status;
}
