package com.cinema.booking_app.common.base.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagingResponse<T> {
    List<T> contents = new ArrayList<>();
    PageableData paging;

    public static <T> PagingResponse<T> from(Page<T> page) {
        final var response = new PagingResponse<T>();
        response.setContents(page.getContent());
        response.setPaging(PageableData.from(page));
        return response;
    }
}
