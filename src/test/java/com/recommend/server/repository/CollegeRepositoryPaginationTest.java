package com.recommend.server.repository;

import com.recommend.server.model.College;
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
class CollegeRepositoryPaginationTest {
    @Mock
    CollegeRepository collegeRepository;

    @Test
    void shouldReturnPaginatedColleges() {
        when(collegeRepository.findAll(any(PageRequest.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of()));

        PageRequest pageRequest = PageRequest.of(0, 20, Sort.by("name").ascending());
        Page<College> page = collegeRepository.findAll(pageRequest);

        assertNotNull(page);
        verify(collegeRepository).findAll(any(PageRequest.class));
    }
}