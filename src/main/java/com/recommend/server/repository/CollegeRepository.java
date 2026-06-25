package com.recommend.server.repository;

import com.recommend.server.model.College;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CollegeRepository extends JpaRepository<College, Integer> {

    @Query(value = """
            SELECT c.* FROM college c
            WHERE (
                6371000 * acos(
                    cos(radians(:lat)) *
                    cos(radians(c.lat)) *
                    cos(radians(c.lon) - radians(:lon)) +
                    sin(radians(:lat)) *
                    sin(radians(c.lat))
                )
            ) <= :maxDistance
            """,
            countQuery = """
            SELECT count(*) FROM college c
            WHERE (
                6371000 * acos(
                    cos(radians(:lat)) *
                    cos(radians(c.lat)) *
                    cos(radians(c.lon) - radians(:lon)) +
                    sin(radians(:lat)) *
                    sin(radians(c.lat))
                )
            ) <= :maxDistance
            """,
            nativeQuery = true)
    Page<College> findCollegesNearby(
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("maxDistance") double maxDistance,
            Pageable pageable
    );
}
