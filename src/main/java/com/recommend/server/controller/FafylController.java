package com.recommend.server.controller;

import com.recommend.server.dto.Fafyl;
import com.recommend.server.dto.Quiz;
import com.recommend.server.model.Course;
import com.recommend.server.model.CourseImp;
import com.recommend.server.service.FafylService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
public class FafylController {


    @Autowired
    FafylService fafylService;

    @GetMapping("/fafyl")
    public ResponseEntity<List<Fafyl>> findAllFafyl(
            @RequestBody Quiz quiz) {

        List<Fafyl> fafyl = fafylService.findAllFafyl(quiz);
        return ResponseEntity.ok(fafyl);
    }
    /*
    // /course?distance=3000
    @GetMapping("/fafyl/courses")
    public ResponseEntity<List<CourseImp>> findAllCourses(
            @RequestBody List<Integer> courses,
            @RequestParam(required = false) Double distance) {
        if(distance != null) {
            fafyl = fafylService.findAllFafyl(distance, idCourse);
            return ResponseEntity.ok(possible);
        }

        return ResponseEntity.ok(fafylService.findAllCourses());
    }

    @GetMapping("/fafyl/courses/{id}")
    public ResponseEntity<CourseImp> findCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(fafylService.findCourseById(id));
    }


    // /fafyl
    @GetMapping("/fafyl")
    public ResponseEntity<?> findAllFafyl() {
        return ResponseEntity.ok(dataService.findAllFafyl());
    }

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

*/

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
