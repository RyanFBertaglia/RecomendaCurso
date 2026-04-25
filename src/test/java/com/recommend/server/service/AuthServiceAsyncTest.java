package com.recommend.server.service;

import com.recommend.server.model.Course;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Async;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceAsyncTest {

    @Test
    void addHistoryShouldBeAsyncAnnotated() throws NoSuchMethodException {
        Method method = AuthService.class.getMethod("addHistory", Course.class);
        var asyncAnnotation = method.getAnnotation(Async.class);
        assertNotNull(asyncAnnotation, "addHistory should have @Async annotation");
    }
}