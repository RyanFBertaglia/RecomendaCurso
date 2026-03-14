package com.recommend.server.filter;

import com.recommend.server.dto.Coordinates;
import com.recommend.server.model.CourseImp;
import com.recommend.server.service.Location;
import data.DataMock;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest
public class FilterCollegesTest {

    @Autowired
    Location location;

    static List<CourseImp> courseChoose;
    static Coordinates user;

    @BeforeAll
    static void setUp() {
        courseChoose = DataMock.courseImpsList();
        user = DataMock.defaultUser();
    }

    @Test
    void findNearCourses() {
        List<CourseImp> newList = location.filterByLocation(courseChoose, 15000.0, user);
        assertThat(newList, hasSize(3));
    }
}
