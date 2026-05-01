package com.recommend.server.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.dto.LoginRequest;
import com.recommend.server.dto.RegisterRequest;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
class AuthControllerTest {

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

    private String uniqueEmail() {
        return "ryan" + System.currentTimeMillis() + "@email.com";
    }

    @Test
    @DisplayName("Should register a new user")
    void shouldRegisterUser() throws Exception {

        RegisterRequest request =
                new RegisterRequest("Ryan", uniqueEmail(), "123456", new Coordinates(32.0, 32.0));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("Should login with valid credentials")
    void shouldLoginSuccessfully() throws Exception {

        String email = uniqueEmail();

        RegisterRequest register =
                new RegisterRequest("Ryan", email, "123456", new Coordinates(32.0, 32.0));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest request =
                new LoginRequest(email, "123456");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("Should refresh JWT token")
    void shouldRefreshToken() throws Exception {

        String email = uniqueEmail();

        RegisterRequest register =
                new RegisterRequest("Ryan", email, "123456", new Coordinates(32.0, 32.0));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login =
                new LoginRequest(email, "123456");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        String token = JsonPath.read(response, "$.token");

        mockMvc.perform(post("/auth/refresh")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("Should return 400 when token is invalid (business rule)")
    void shouldReturnBadRequestWhenTokenInvalid() throws Exception {

        String fakeToken = "Bearer token.valido.mas.falso";

        mockMvc.perform(get("/auth/me")
                        .header("Authorization", fakeToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return error message when email already exists")
    void shouldReturnMessageWhenEmailExists() throws Exception {

        String email = uniqueEmail();

        RegisterRequest request =
                new RegisterRequest("Ryan", email, "123456", new Coordinates(10.0, 10.0));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("email")));
    }

    @Test
    @DisplayName("Should update user coordinates successfully")
    void shouldUpdateCoordinatesSuccessfully() throws Exception {

        String email = uniqueEmail();

        RegisterRequest register =
                new RegisterRequest("Ryan", email, "123456", new Coordinates(0.0, 0.0));

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isCreated());

        LoginRequest login =
                new LoginRequest(email, "123456");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String token = JsonPath.read(
                loginResult.getResponse().getContentAsString(),
                "$.token"
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Coordinates newCoordinates = new Coordinates(-23.55, -46.63);

        mockMvc.perform(post("/auth/coordinates")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCoordinates)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.locale").exists())
                .andExpect(jsonPath("$.locale.lat").value(-23.55))
                .andExpect(jsonPath("$.locale.lon").value(-46.63));
    }
    @Test
    @DisplayName("Should return 400 when body is invalid")
    void shouldFailWithInvalidBody() throws Exception {

        String email = uniqueEmail();

        RegisterRequest register =
                new RegisterRequest("Ryan", email, "123456", new Coordinates(0.0, 0.0));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login =
                new LoginRequest(email, "123456");

        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String token = JsonPath.read(result.getResponse().getContentAsString(), "$.token");

        mockMvc.perform(post("/auth/coordinates")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ invalid json }"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when course id is missing")
    void shouldFailWhenCourseIdIsMissing() throws Exception {

        String email = uniqueEmail();

        RegisterRequest register =
                new RegisterRequest("Ryan", email, "123456", new Coordinates(0.0, 0.0));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = new LoginRequest(email, "123456");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String token = JsonPath.read(
                loginResult.getResponse().getContentAsString(),
                "$.token"
        );

        mockMvc.perform(post("/auth/history")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should add course to user history")
    @Disabled("Temporarily disabled")
    void shouldAddHistory() throws Exception {

        String email = uniqueEmail();

        RegisterRequest register =
                new RegisterRequest("Ryan", email, "123456", new Coordinates(0.0, 0.0));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = new LoginRequest(email, "123456");

        MvcResult loginResult = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        String token = JsonPath.read(
                loginResult.getResponse().getContentAsString(),
                "$.token"
        );

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(email, null, null);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Integer courseId = 1;

        mockMvc.perform(post("/auth/history")
                        .header("Authorization", "Bearer " + token)
                        .param("idCourse", courseId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.course").exists());
    }
}