package com.recommend.server.repository;

import com.recommend.server.model.College;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CollegeRepository extends JpaRepository<College, Integer> {
}
