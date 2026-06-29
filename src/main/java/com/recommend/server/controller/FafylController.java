package com.recommend.server.controller;

import com.recommend.server.dto.*;
import com.recommend.server.service.AuthService;
import com.recommend.server.service.ChatService;
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

    @Autowired
    ChatService chatService;

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
        boolean hasLocation = coursesRequest.location() != null && coursesRequest.distance() != null;
        boolean hasFees = coursesRequest.minFees() != null || coursesRequest.maxFees() != null;
        boolean hasPeriod = coursesRequest.period() != null && !coursesRequest.period().isBlank();
        boolean hasDistance = coursesRequest.distance() != null;

        Coordinates userLocation = hasLocation ? coursesRequest.location() : authService.getUserDTO().locale();

        if (hasDistance && (hasFees || hasPeriod)) {
            List<CourseImpDTO> possible = fafylService.findInDistanceFiltered(
                    coursesRequest.courses(),
                    coursesRequest.distance(),
                    userLocation,
                    coursesRequest.minFees(),
                    coursesRequest.maxFees(),
                    coursesRequest.period()
            );
            return ResponseEntity.ok(possible);
        }

        if (hasDistance) {
            List<CourseImpDTO> possible = fafylService.findInDistance(
                    coursesRequest.courses(),
                    coursesRequest.distance(),
                    userLocation
            );
            return ResponseEntity.ok(possible);
        }

        return ResponseEntity.ok(fafylService.findWithoutDistance(coursesRequest.courses()));
    }

    @GetMapping("/chatbot")
    @CrossOrigin("*")
    public ResponseEntity<String> chatbot(@RequestParam String question) {
        return ResponseEntity.ok(chatService.respond(question));
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
