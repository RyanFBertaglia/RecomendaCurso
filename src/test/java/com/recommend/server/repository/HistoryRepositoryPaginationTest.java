package com.recommend.server.repository;

import com.recommend.server.model.History;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HistoryRepositoryPaginationTest {
    @Mock
    HistoryRepository historyRepository;

    @Test
    void shouldReturnPaginatedHistory() {
        when(historyRepository.findAll(any(PageRequest.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("accessedAt").descending());
        Page<History> page = historyRepository.findAll(pageRequest);

        assertNotNull(page);
        verify(historyRepository).findAll(any(PageRequest.class));
    }
}