package com.transport.dto;

import java.util.List;

/**
 * Industry-standard paginated API response wrapper.
 * Frontend receives: { data, page, size, totalElements, totalPages, hasNext }
 */
public record PagedResponse<T>(
        List<T> data,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrev
) {
    public static <T> PagedResponse<T> of(org.springframework.data.domain.Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}
