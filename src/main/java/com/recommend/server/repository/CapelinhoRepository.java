package com.recommend.server.repository;

import com.recommend.server.model.Capelinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CapelinhoRepository extends JpaRepository<Capelinho, Integer> {
}
