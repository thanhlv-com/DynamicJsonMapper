package com.thanhlv.DynamicJsonMapper;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginatedListMapperTest {

    @Test
    void paginate_shouldReturnExpectedSliceForNormalInput() {
        List<Integer> source = List.of(1, 2, 3, 4, 5);

        PaginatedListMapper.PaginationResult result = PaginatedListMapper.paginate(source, 2, 2);

        assertEquals(2, result.page());
        assertEquals(2, result.size());
        assertEquals(5, result.total());
        assertEquals(3, result.totalPages());
        assertEquals(List.of(3, 4), result.items());
    }

    @Test
    void paginate_shouldNormalizeInvalidPageAndSize() {
        List<Integer> source = List.of(10, 20, 30);

        PaginatedListMapper.PaginationResult result = PaginatedListMapper.paginate(source, 0, 0);

        assertEquals(1, result.page());
        assertEquals(1, result.size());
        assertEquals(3, result.total());
        assertEquals(3, result.totalPages());
        assertEquals(List.of(10), result.items());
    }

    @Test
    void paginate_shouldReturnEmptyItemsWhenPageExceedsRange() {
        List<Integer> source = List.of(1, 2, 3);

        PaginatedListMapper.PaginationResult result = PaginatedListMapper.paginate(source, 5, 2);

        assertEquals(5, result.page());
        assertEquals(2, result.size());
        assertEquals(3, result.total());
        assertEquals(2, result.totalPages());
        assertTrue(result.items().isEmpty());
    }

    @Test
    void paginate_shouldHandleEmptySource() {
        List<Integer> source = List.of();

        PaginatedListMapper.PaginationResult result = PaginatedListMapper.paginate(source, 1, 10);

        assertEquals(1, result.page());
        assertEquals(10, result.size());
        assertEquals(0, result.total());
        assertEquals(1, result.totalPages());
        assertTrue(result.items().isEmpty());
    }
}
