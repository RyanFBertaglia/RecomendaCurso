package com.recommend.server.service;

import com.recommend.server.dto.CollegeDTO;
import com.recommend.server.dto.CourseDTO;
import com.recommend.server.dto.CourseImpDTO;
import com.recommend.server.model.College;
import com.recommend.server.model.Course;
import com.recommend.server.model.CourseImp;
import com.recommend.server.repository.CollegeRepository;
import com.recommend.server.repository.CourseImpRepository;
import com.recommend.server.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DataService {

    private final CourseRepository courseRepository;
    private final CollegeRepository collegeRepository;
    private final CourseImpRepository courseImpRepository;

    @Autowired
    public DataService(CourseRepository courseRepository, CollegeRepository collegeRepository,
                       CourseImpRepository courseImpRepository) {
        this.collegeRepository = collegeRepository;
        this.courseRepository = courseRepository;
        this.courseImpRepository = courseImpRepository;
    }

    public List<Course> insertCourses(List<CourseDTO> courseList) {
        List<Course> courses = courseList.stream()
                .map(dto -> {
                    Course course = new Course();
                    course.setName(dto.name());
                    course.setAbilities(dto.abilities());
                    course.setCantBe(dto.cantBe());
                    course.setDescription(dto.description());
                    return course;
                })
                .toList();

        return courseRepository.saveAll(courses);
    }

    public List<CourseImp> insertCoursesImp(List<CourseImpDTO> courseImpDTOList) {
        List<CourseImp> courses = courseImpDTOList.stream()
                .map(dto -> {
                    CourseImp courseImp = new CourseImp();
                    courseImp.setName(dto.name());
                    courseImp.setNote(dto.note());
                    courseImp.setDetails(dto.details());
                    courseImp.setFees(dto.fees());
                    courseImp.setLocale(dto.locale());

                    Course course = courseRepository.findById(dto.courseId())
                            .orElseThrow(() -> new RuntimeException("Course not found: " + dto.courseId()));
                    College college = collegeRepository.findById(dto.collegeId())
                            .orElseThrow(() -> new RuntimeException("Course not found: " + dto.courseId()));
                    courseImp.setCourse(course);
                    courseImp.setCollege(college);
                    return courseImp;
                })
                .toList();
        return courseImpRepository.saveAll(courses);
    }

    public List<College> insertColleges(List<CollegeDTO> colleges) {
        return colleges.stream().map(collegeDTO -> {
            College college = new College();
            college.setName(collegeDTO.name());
            college.setDescription(collegeDTO.description());
            college.setLocale(collegeDTO.locale());

            List<CourseImp> courseImps = collegeDTO.courses().stream().map(impDTO -> {
                CourseImp courseImp = new CourseImp();
                courseImp.setName(impDTO.name());

                courseImp.setCourse(courseRepository.getReferenceById(impDTO.courseId()));

                courseImp.setCollege(college);
                courseImp.setNote(impDTO.note());
                courseImp.setDetails(impDTO.details());
                courseImp.setFees(impDTO.fees());
                courseImp.setLocale(impDTO.locale());
                return courseImp;
            }).toList();

            college.setCourses(courseImps);
            return collegeRepository.save(college);
        }).toList();
    }

    @Transactional
    public CourseImp addCourseImpToCollege(Integer collegeId, CourseImpDTO dto) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new RuntimeException("College not found with ID: " + collegeId));

        CourseImp courseImp = new CourseImp();
        courseImp.setName(dto.name());
        courseImp.setNote(dto.note());
        courseImp.setDetails(dto.details());
        courseImp.setFees(dto.fees());
        courseImp.setLocale(dto.locale());
        courseImp.setCourse(courseRepository.getReferenceById(dto.courseId()));

        courseImp.setCollege(college);
        return courseImpRepository.save(courseImp);
    }

    public List<Course> findAllModelCourses() {
        return courseRepository.findAll();
    }
    public List<College> findAllColleges() { return collegeRepository.findAll(); }
    public List<CourseImp> findAllCourses() { return courseImpRepository.findAll(); }
    public College findOneCollege(Integer id) { return collegeRepository.findById(id).orElse(null); }
    public CourseImp findOneCourseImp(Integer id) { return courseImpRepository.findById(id).orElse(null); }
    public Course findOneCourse(Integer id) { return courseRepository.findById(id).orElse(null); }
    public CourseImp findOneCourseImpByCourseIdAndCollegeId(Integer courseId, Integer collegeId) { return courseImpRepository.findByCourseIdAndCollegeId(courseId, collegeId); }
}
