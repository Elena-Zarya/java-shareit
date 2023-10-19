package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;

@Slf4j
public class Pages {
    public static Pageable getPage(int from, int size) {
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

    public static PageRequest getPage(int from, int size, org.springframework.data.domain.Sort sort) {
        return PageRequest.of(from > 0 ? from / size : 0, size, sort);
    }
}
