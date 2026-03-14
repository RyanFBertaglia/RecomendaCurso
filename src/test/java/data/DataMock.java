package data;

import com.recommend.server.dto.Coordinates;
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

    public static List<CourseImp> courseImpsList() {
        List<CourseImp> courses = new ArrayList<>();

        // 649m
        courses.add(create(1, "Logística", 201, 10, 800.0, -22.905, -47.060, "Próximo à estação rodoviária"));
        // 13222m
        courses.add(create(2, "Engenharia de Computação", 202, 11, 2500.0, -22.816, -47.069, "Foco em hardware e software"));
        // 29257m
        courses.add(create(3, "Análise de Sistemas", 203, 12, 950.0, -23.088, -47.218, "Unidade Indaiatuba - Noturno"));
        // 29047m
        courses.add(create(4, "Administração", 204, 13, 700.0, -22.820, -47.266, "Foco em gestão industrial"));
        // 10197m
        courses.add(create(5, "Marketing Digital", 205, 14, 1100.0, -22.970, -46.996, "Unidade Valinhos - Semipresencial"));

        return courses;
    }

    public static CourseImp create(Integer id, String name, Integer idCourse, Integer idCollege,
                                   Double fees, Double lat, Double lon, String details) {
        CourseImp course = new CourseImp();
        course.setId(id);
        course.setName(name);
        course.setCourse(null);
        course.setCollege(null);
        course.setFees(fees);
        course.setDetails(details);
        course.setLocale(new Coordinates(lat, lon));
        course.setNote(null);
        return course;
    }

    // User lives downtown
    public static Coordinates defaultUser() {
        return new Coordinates(-22.9059, -47.0590);
    }

}
