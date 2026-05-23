package data;

import com.recommend.server.dto.Coordinates;
import com.recommend.server.model.College;
import com.recommend.server.model.Course;
import com.recommend.server.model.CourseImp;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class DataMock {

    public static List<Course> listCourses() {
        List<Course> courses = new ArrayList<>();

        courses.add(buildCourse("Química", 0.3, 0.4, 0.6, 0.9));
        courses.add(buildCourse("Direito", 1.0, 0.5, 0.8, 0.2));
        courses.add(buildCourse("Medicina", 0.8, 0.2, 1.0, 0.5));
        courses.add(buildCourse("Engenharia", 0.5, 1.0, 0.2, 0.8));

        courses.add(buildCourse("Artes", 0.2, 0.9, 0.5, 0.7));
        courses.add(buildCourse("Design", 0.3, 0.8, 0.4, 0.9));
        courses.add(buildCourse("Gastronomia", 0.4, 0.7, 0.6, 0.3));
        courses.add(buildCourse("Ti", 0.6, 0.3, 0.4, 1.0));

        return courses;
    }

    private static Course buildCourse(String name, double d, double i, double s, double c) {
        Course course = new Course();
        course.setName(name);
        course.setDiscWeights(Map.of('D', d, 'I', i, 'S', s, 'C', c));
        return course;
    }

    public static College defaultCollege() {
        College college = new College();
        college.setName("Universidade Campinas");
        college.setDescription("Universidade pública de referência");
        college.setLocale(new Coordinates(-22.905, -47.060));
        return college;
    }

    public static List<CourseImp> courseImpsList() {
        List<Course> courses = listCourses();
        College college = defaultCollege();
        List<CourseImp> courseImps = new ArrayList<>();

        courseImps.add(createCourseImp(1, "Logística", courses.get(4), college, 800.0, -22.905, -47.060, "Próximo à estação rodoviária"));
        courseImps.add(createCourseImp(2, "Engenharia de Computação", courses.get(3), college, 2500.0, -22.816, -47.069, "Foco em hardware e software"));
        courseImps.add(createCourseImp(3, "Análise de Sistemas", courses.get(3), college, 950.0, -23.088, -47.218, "Unidade Indaiatuba - Noturno"));
        courseImps.add(createCourseImp(4, "Administração", courses.get(1), college, 700.0, -22.820, -47.266, "Foco em gestão industrial"));
        courseImps.add(createCourseImp(5, "Marketing Digital", courses.get(5), college, 1100.0, -22.970, -46.996, "Unidade Valinhos - Semipresencial"));

        return courseImps;
    }

    private static CourseImp createCourseImp(Integer id, String name, Course course, College college,
                                             Double fees, Double lat, Double lon, String details) {
        CourseImp courseImp = new CourseImp();
        courseImp.setId(id);
        courseImp.setName(name);
        courseImp.setCourse(course);
        courseImp.setCollege(college);
        courseImp.setFees(fees);
        courseImp.setDetails(details);
        courseImp.setLocale(new Coordinates(lat, lon));
        courseImp.setNote(null);
        return courseImp;
    }

    public static Coordinates defaultUser() {
        return new Coordinates(-22.9059, -47.0590);
    }

    public static List<Integer> courses() {
        return Arrays.asList(2, 3, 7, 5);
    }

}
