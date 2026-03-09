package com.recommend.server.service;

import com.recommend.server.dto.Coordinates;
import com.recommend.server.exception.LocationNotFound;
import com.recommend.server.model.CourseImp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class Location {

    @Value("${geoapify.api.key}")
    private String API_KEY;

    public List<CourseImp> filterByLocation(List<CourseImp> courses, Double max, Coordinates user) {
        return courses.stream()
                .filter(course -> course.getLocale() != null)
                .filter(course -> {
                    Double distance = distance(course.getLocale(), user);
                    return distance <= max;
                })
                .toList();
    }

    public Double distance(Coordinates college, Coordinates user) {
        String url = "https://api.geoapify.com/v1/routing"
                + "?waypoints=" + user.lat() + "," + user.lon() + "|"
                + college.lat() + "," + college.lon()
                + "&mode=drive"
                + "&apiKey=" + API_KEY;

        String json = new RestTemplate().getForObject(url, String.class);

        Pattern pattern = Pattern.compile("\"distance\":\\s*(\\d+(?:\\.\\d+)?)\\s*,\\s*\"time\":\\s*(\\d+(?:\\.\\d+)?)");
        if(json==null) throw new LocationNotFound("Location not found");
        Matcher matcher = pattern.matcher(json);

        if (matcher.find()) {
            return Double.parseDouble(matcher.group(1));
        } else {
            throw new LocationNotFound("Location not found");
        }
    }
}
