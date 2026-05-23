package com.recommend.server.controller;

import com.recommend.server.dto.*;
import com.recommend.server.service.AuthService;
import com.recommend.server.service.FafylService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
    public ResponseEntity<Page<Fafyl>> findAllFafyl(
            @Valid @RequestBody Quiz quiz,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(defaultValue = "score") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        Page<Fafyl> fafyl = fafylService.findAllFafyl(quiz, PageRequest.of(page, size, sort));
        return ResponseEntity.ok(fafyl);
    }

    // If the user is passing another address is because he wants to filter by distance (mandatory)
    @PostMapping("/fafyl/courses")
    public ResponseEntity<List<CourseImpDTO>> findAllCourses(
            @Valid @RequestBody CoursesRequest coursesRequest
    ) {
        List<CourseImpDTO> possible;
        if(coursesRequest.location() != null && coursesRequest.distance() != null) {
            possible =  fafylService.findInDistance(coursesRequest.courses(),
                    coursesRequest.distance(), coursesRequest.location());
            return ResponseEntity.ok(possible);
        }
        Coordinates userLocation = authService.getUserDTO().locale();
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
