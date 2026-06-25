package com.recommend.server.audit;

import com.recommend.server.exception.CollegeNotFound;
import com.recommend.server.service.DataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class DataControllerNpeTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private DataService dataService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    @DisplayName("FIXED CRIT-2: findOneCollege now throws CollegeNotFound instead of returning null")
    void findOneCollegeShouldThrowForNonExistentId() {
        assertThrows(CollegeNotFound.class, () -> dataService.findOneCollege(99999));
    }

    @Test
    @DisplayName("FIXED CRIT-2: GET /college/{id}/course now returns 404 instead of NPE")
    void getCollegeCoursesWithNonExistentCollegeShouldReturn404() throws Exception {
        mockMvc.perform(get("/college/99999/course"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("FIXED: GET /college/{id} now returns 404 instead of 200 with null body")
    void getNonExistentCollegeShouldReturn404() throws Exception {
        mockMvc.perform(get("/college/99999"))
                .andExpect(status().isNotFound());
    }
}
