package com.recommend.server.repository;

import com.recommend.server.model.History;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Integer> {
    List<History> findByUserIdOrderByAccessedAtDesc(Integer userId);
}
