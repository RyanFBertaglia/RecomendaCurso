package com.recommend.server.controller;

import com.recommend.server.model.Course;
import com.recommend.server.model.CourseImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class FafylController {

    // /course?distance=3000
    @GetMapping("/course")
    public ResponseEntity<?> findAllCourses(
            @RequestParam(required = false) Double distance
    ) {
        List<CourseImp> possible = new ArrayList<>();
        if(distance != null) {
            // courses.findInDistance(distance, idCourse)
            return ResponseEntity.ok(possible);
        }
        return ResponseEntity.ok(possible);
    }

    @GetMapping("/course/{id}")
    public ResponseEntity<?> findOneCourse(
            @PathVariable Long id
    ) {
        // return ResponseEntity.ok(coursesImp.findOne(id)
        return ResponseEntity.ok("");
    }

    @GetMapping("/model/course/{id}")
    public ResponseEntity<?> findOneCourseModel(
            @PathVariable Long id
    ) {
        // Course course = courses.findOneCourseModel(id);
        return ResponseEntity.ok().body("");
    }


    /*
        /register
        /login
        /fafyl   (list of courses recommended)
        /course  (list of the courses (in the college) can be more than one, it's where the filter comes
        /mascote
        /course?page={n}&limit={limit}
        /course/{id}
        /model/course
        /model/course/{id}

    */
}
