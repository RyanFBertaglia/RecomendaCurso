package com.recommend.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommend.server.dto.CollegeDTO;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.dto.CourseDTO;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class CollegeImageTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    private int createDefaultCollege() throws Exception {
        String courseJson = objectMapper.writeValueAsString(List.of(
                new CourseDTO("Engenharia", Map.of('D', 0.5, 'I', 1.0, 'S', 0.2, 'C', 0.8), null)
        ));

        String courseResponse = mockMvc.perform(post("/model/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(courseJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        int courseId = objectMapper.readTree(courseResponse).get(0).get("id").asInt();

        CollegeDTO collegeDTO = new CollegeDTO(
                "UNICAMP", "Universidade Estadual de Campinas",
                new Coordinates(-22.82, -47.07),
                List.of(), null
        );

        String collegeJson = objectMapper.writeValueAsString(List.of(collegeDTO));

        String collegeResponse = mockMvc.perform(post("/college")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(collegeJson))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(collegeResponse).get(0).get("id").asInt();
    }

    @Test
    @DisplayName("Should upload image to existing college and return college with image field")
    void shouldUploadImageToCollege() throws Exception {
        int collegeId = createDefaultCollege();

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "college.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes()
        );

        mockMvc.perform(multipart("/college/{id}/image", collegeId)
                        .file(image))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(collegeId))
                .andExpect(jsonPath("$.name").value("UNICAMP"))
                .andExpect(jsonPath("$.image").isString())
                .andExpect(jsonPath("$.description").value("Universidade Estadual de Campinas"));
    }

    @Test
    @DisplayName("Should retrieve uploaded image via storage endpoint")
    void shouldRetrieveUploadedImage() throws Exception {
        int collegeId = createDefaultCollege();

        MockMultipartFile image = new MockMultipartFile(
                "image",
                "college.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes()
        );

        String response = mockMvc.perform(multipart("/college/{id}/image", collegeId)
                        .file(image))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String imageId = objectMapper.readTree(response).get("image").asText();

        mockMvc.perform(get("/storage/{id}", imageId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes("fake-image-content".getBytes()));
    }

    @Test
    @DisplayName("Should return 404 when college does not exist")
    void shouldReturn404WhenCollegeNotFound() throws Exception {
        MockMultipartFile image = new MockMultipartFile(
                "image",
                "college.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake-image-content".getBytes()
        );

        mockMvc.perform(multipart("/college/{id}/image", 99999)
                        .file(image))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should not change image when file is empty")
    void shouldNotUpdateImageWhenFileIsEmpty() throws Exception {
        int collegeId = createDefaultCollege();

        MockMultipartFile emptyImage = new MockMultipartFile(
                "image",
                "",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );

        mockMvc.perform(multipart("/college/{id}/image", collegeId)
                        .file(emptyImage))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image").doesNotExist());
    }

    @Test
    @DisplayName("Should replace existing image with a new one")
    void shouldReplaceExistingImage() throws Exception {
        int collegeId = createDefaultCollege();

        MockMultipartFile image1 = new MockMultipartFile(
                "image",
                "first.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "first-image".getBytes()
        );

        String response1 = mockMvc.perform(multipart("/college/{id}/image", collegeId)
                        .file(image1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String firstImageId = objectMapper.readTree(response1).get("image").asText();

        MockMultipartFile image2 = new MockMultipartFile(
                "image",
                "second.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "second-image".getBytes()
        );

        String response2 = mockMvc.perform(multipart("/college/{id}/image", collegeId)
                        .file(image2))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String secondImageId = objectMapper.readTree(response2).get("image").asText();

        assert !firstImageId.equals(secondImageId) : "Image ID should change after update";
    }
}
