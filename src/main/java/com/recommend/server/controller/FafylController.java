package com.recommend.server.controller;

import com.recommend.server.dto.*;
import com.recommend.server.model.CourseImp;
import com.recommend.server.service.AuthService;
import com.recommend.server.service.FafylService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
public class FafylController {

    @Autowired
    FafylService fafylService;

    @Autowired
    AuthService authService;

    @GetMapping("/fafyl")
    public ResponseEntity<List<Fafyl>> findAllFafyl(
            @RequestBody Quiz quiz) {
        List<Fafyl> fafyl = fafylService.findAllFafyl(quiz);
        return ResponseEntity.ok(fafyl);
    }

    // If the user is passing another address is because he wants to filter by distance (mandatory)
    @PostMapping("/fafyl/courses")
    public ResponseEntity<List<CourseImpDTO>> findAllCourses(
            @RequestBody CoursesRequest coursesRequest
    ) {
        List<CourseImpDTO> possible;
        if(coursesRequest.location() != null && coursesRequest.distance() != null) {
            possible =  fafylService.findInDistance(coursesRequest.courses(),
                    coursesRequest.distance(), coursesRequest.location());
            return ResponseEntity.ok(possible);
        }
        Coordinates userLocation = authService.getUser().locale();
        if(coursesRequest.distance() != null) {
            possible =  fafylService.findInDistance(coursesRequest.courses(),
                    coursesRequest.distance(), userLocation);
            return ResponseEntity.ok(possible);
        }
        return ResponseEntity.ok(fafylService.findWithoutDistance(coursesRequest.courses()));
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
