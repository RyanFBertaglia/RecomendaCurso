package data;

import com.recommend.server.dto.Coordinates;
import com.recommend.server.model.College;
import com.recommend.server.model.Course;
import com.recommend.server.model.CourseImp;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataMock {

    public static List<Course> listCourses() {
        List<Course> courses = new ArrayList<>();

        courses.add(new Course("Química", Arrays.asList("Matemática", "Curioso", "Sério")));
        courses.add(new Course("Direito", Arrays.asList("Focado", "Sério", "Falar em público")));
        courses.add(new Course("Medicina", Arrays.asList("Focado", "Sério", "Biologia")));
        courses.add(new Course("Engenharia", Arrays.asList("Matemática", "Raciocínio Lógico", "Sério")));

        courses.add(new Course("Artes", Arrays.asList("Criatividade", "Descontraido")));
        courses.add(new Course("Design", Arrays.asList("Criatividade", "Desenho", "Focado")));
        courses.add(new Course("Gastronomia", Arrays.asList("Cozinha", "Descontraido", "Prática")));
        courses.add(new Course("Ti", Arrays.asList("Matemática", "Lógica", "Trabalho remoto")));

        return courses;
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
