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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class DataService {

    private final CourseRepository courseRepository;
    private final CollegeRepository collegeRepository;
    private final CourseImpRepository courseImpRepository;
    private final ImageStorageService imageStorageService;

    @Autowired
    public DataService(CourseRepository courseRepository, CollegeRepository collegeRepository,
                       CourseImpRepository courseImpRepository, ImageStorageService imageStorageService) {
        this.collegeRepository = collegeRepository;
        this.courseRepository = courseRepository;
        this.courseImpRepository = courseImpRepository;
        this.imageStorageService = imageStorageService;
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

    @Transactional
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
                            .orElseThrow(() -> new RuntimeException("College not found: " + dto.collegeId()));
                    courseImp.setCourse(course);
                    courseImp.setCollege(college);
                    return courseImp;
                })
                .toList();
        return courseImpRepository.saveAll(courses);
    }

    @Transactional
    public List<College> insertColleges(List<CollegeDTO> colleges) {
        return colleges.stream().map(collegeDTO -> {
            College college = new College();
            college.setName(collegeDTO.name());
            college.setDescription(collegeDTO.description());
            college.setLocale(collegeDTO.locale());

            if (collegeDTO.image() != null && !collegeDTO.image().isEmpty()) {
                String imageId = saveImage(collegeDTO.image());
                college.setImage(imageId);
            }

            List<CourseImpDTO> list = collegeDTO.courses();
            List<CourseImp> courseImps = list.stream().map(impDTO -> {
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

    @Transactional
    public String saveImage(MultipartFile file) {
        return imageStorageService.saveImage(file);
    }

    @Transactional
    public College updateCollegeImage(Integer collegeId, MultipartFile imageFile) {
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new RuntimeException("College not found with ID: " + collegeId));

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageId = saveImage(imageFile);
            college.setImage(imageId);
            return collegeRepository.save(college);
        }

        return college;
    }

    public List<Course> findAllModelCourses() {
        return courseRepository.findAll();
    }

    public void clean() {
        courseImpRepository.deleteAll();
        courseRepository.deleteAll();
        collegeRepository.deleteAll();
        imageStorageService.deleteAll();
    }

    public Page<Course> findAllModelCourses(Pageable pageable) {
        return courseRepository.findAll(pageable);
    }

    public List<College> findAllColleges() { return collegeRepository.findAll(); }
    public Page<College> findAllColleges(Pageable pageable) { return collegeRepository.findAll(pageable); }
    public List<CourseImp> findAllCourses() { return courseImpRepository.findAll(); }
    public Page<CourseImp> findAllCourses(Pageable pageable) { return courseImpRepository.findAll(pageable); }
    public College findOneCollege(Integer id) { return collegeRepository.findById(id).orElse(null); }
    public CourseImp findOneCourseImp(Integer id) { return courseImpRepository.findById(id).orElse(null); }
    public Course findOneCourse(Integer id) { return courseRepository.findById(id).orElse(null); }
    public CourseImp findOneCourseImpByCourseIdAndCollegeId(Integer courseId, Integer collegeId) { return courseImpRepository.findByCourseIdAndCollegeId(courseId, collegeId); }
}
