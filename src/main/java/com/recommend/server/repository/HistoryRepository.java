package com.recommend.server.repository;

import com.recommend.server.model.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Integer> {
    List<History> findByUserIdOrderByAccessedAtDesc(Integer userId);

    Page<History> findByUserId(Integer userId, Pageable pageable);
}
