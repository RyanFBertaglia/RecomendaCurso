package data;

import com.recommend.server.dto.Coordinates;
import com.recommend.server.model.Course;
import com.recommend.server.model.CourseImp;
import com.recommend.server.service.Location;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = com.recommend.server.Main.class)
public class TestMock {

    @Autowired
    Location location;

    @Test
    void seeDistance() {
        List<CourseImp> courseImps = DataMock.courseImpsList();
        Coordinates user = DataMock.defaultUser();

        for (CourseImp course : courseImps) {
            double distance = location.distance(course.getLocale(), user);
            IO.println("Distance between user and the" + course.getName() + "course is: " + distance);
        }
    }
}
