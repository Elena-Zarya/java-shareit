package ru.practicum.shareit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Slf4j
public class Pages {
    public static Pageable getPage(int from, int size) {
//        if (from < 0 || size < 1) {
//            log.info("invalid parameters for pagination");
//            throw new ValidationException("invalid parameters for pagination");
//        }
        return PageRequest.of(from > 0 ? from / size : 0, size);
    }

    public static PageRequest getPage(int from, int size, org.springframework.data.domain.Sort sort) {
//
//        if (from < 0 || size < 1) {
//            log.info("invalid parameters for pagination");
//            throw new ValidationException("invalid parameters for pagination");
//        }
        return PageRequest.of(from > 0 ? from / size : 0, size, sort);
    }
}
