package com.recommend.server.repository;

import com.recommend.server.model.CourseImp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseImpRepository extends JpaRepository<CourseImp, Integer> {
    CourseImp findByCourseIdAndCollegeId(Integer courseId, Integer collegeId);
    List<CourseImp> findByCourseIdIn(List<Integer> courses);
}
