package com.recommend.server.controller;

import com.recommend.server.dto.CollegeDTO;
import com.recommend.server.dto.CourseDTO;
import com.recommend.server.dto.CourseImpDTO;
import com.recommend.server.model.College;
import com.recommend.server.model.Course;
import com.recommend.server.model.CourseImp;
import com.recommend.server.service.DataService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class DataController {
    private final DataService dataService;

    public DataController(DataService dataService) {
        this.dataService = dataService;
    }

    @GetMapping("/model/course")
    public ResponseEntity<Page<Course>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        return ResponseEntity.ok(dataService.findAllModelCourses(PageRequest.of(page, size, sort)));
    }

    @PostMapping("/model/course")
    public ResponseEntity<List<Course>> insertModelCourses(@RequestBody List<CourseDTO> courses) {
        return ResponseEntity.ok(dataService.insertCourses(courses));
    }

    @GetMapping("/course")
    public ResponseEntity<Page<CourseImp>> findAllCourses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        return ResponseEntity.ok(dataService.findAllCourses(PageRequest.of(page, size, sort)));
    }

    @PostMapping("/course")
    public ResponseEntity<List<CourseImp>> insertCourses(@RequestBody List<CourseImpDTO> courses) {
        return ResponseEntity.ok(dataService.insertCoursesImp(courses));
    }

    @GetMapping("/college")
    public ResponseEntity<Page<College>> findAllColleges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortBy);
        return ResponseEntity.ok(dataService.findAllColleges(PageRequest.of(page, size, sort)));
    }

    @PostMapping(value = "/college")
    public ResponseEntity<List<College>> insertColleges(@RequestBody List<CollegeDTO> colleges) {
        return ResponseEntity.ok(dataService.insertColleges(colleges));
    }

    @PostMapping("/college/{id}/image")
    public ResponseEntity<College> updateCollegeImage(@PathVariable Integer id, @RequestParam MultipartFile image) {
        return ResponseEntity.ok(dataService.updateCollegeImage(id, image));
    }

    @PostMapping("/college/{id}/course")
    public ResponseEntity<CourseImp> addCourseImpToCollege(@PathVariable Integer id, @RequestBody CourseImpDTO dto) {
        return ResponseEntity.ok(dataService.addCourseImpToCollege(id, dto));
    }

    @GetMapping("/college/{id}/course")
    public ResponseEntity<List<CourseImp>> findCoursesByCollegeId(@PathVariable Integer id) {
        return ResponseEntity.ok(dataService.findOneCollege(id).getCourses());
    }

    @GetMapping("/college/{id}")
    public ResponseEntity<College> findOneCollege(@PathVariable Integer id) {
        return ResponseEntity.ok(dataService.findOneCollege(id));
    }

    @GetMapping("/college/{id}/course/{courseId}")
    public ResponseEntity<CourseImp> findCourseByCollegeIdAndCourseId(@PathVariable Integer id, @PathVariable Integer courseId) {
        return ResponseEntity.ok(dataService.findOneCourseImpByCourseIdAndCollegeId(courseId, id));
    }
}
