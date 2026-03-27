package com.recommend.server.repository;

import com.recommend.server.dto.CourseImpDTO;
import com.recommend.server.model.CourseImp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseImpRepository extends JpaRepository<CourseImp, Integer> {
    CourseImp findByCourseIdAndCollegeId(Integer courseId, Integer collegeId);

    // List<CourseImp> findByCourseIdIn(List<Integer> courses);

    @Query(value = """
            SELECT
                c.name          AS name,
                c.id_course     AS courseId,
                c.id_college    AS collegeId,
                c.note          AS note,
                c.details       AS details,
                c.fees          AS fees,
                c.lat           AS lat,
                c.lon           AS lon
            FROM course_imp c
            WHERE c.id_course IN (:courses)
            AND (
                6371000 * acos(
                    cos(radians(:lat)) *
                    cos(radians(c.lat)) *
                    cos(radians(c.lon) - radians(:lon)) +
                    sin(radians(:lat)) *
                    sin(radians(c.lat))
                )
            ) <= :distance
            """, nativeQuery = true)
    List<CourseImpProjection> findNearbyCourses(
            @Param("courses") List<Integer> courses,
            @Param("lat") double lat,
            @Param("lon") double lon,
            @Param("distance") double distance
    );

    @Query("""
    SELECT new com.recommend.server.dto.CourseImpDTO(
        c.name,
        c.course.id,
        c.college.id,
        c.note,
        c.details,
        c.fees,
        c.locale
    )
    FROM CourseImp c
    """)
    List<CourseImpDTO> findAllCourseImpDTO();

    interface CourseImpProjection {
        String getName();
        Integer getCourseId();
        Integer getCollegeId();
        String getNote();      // JSON comes as String with native projection
        String getDetails();
        Double getFees();
        Double getLat();
        Double getLon();
    }
}