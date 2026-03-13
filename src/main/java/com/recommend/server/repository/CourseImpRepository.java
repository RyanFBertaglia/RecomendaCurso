package com.recommend.server.repository;

import com.recommend.server.model.CourseImp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseImpRepository extends JpaRepository<CourseImp, Integer> {
    CourseImp findByCourseIdAndCollegeId(Integer courseId, Integer collegeId);
}
