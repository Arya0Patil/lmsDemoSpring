package com.learnnest.lms.api;

import com.learnnest.lms.api.dto.CourseResponse;
import com.learnnest.lms.application.mapper.CourseMapper;
import com.learnnest.lms.domain.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {
    private final CourseService courseService;
    private final CourseMapper courseMapper;

    public CourseController(CourseService courseService, CourseMapper courseMapper) {
        this.courseService = courseService;
        this.courseMapper = courseMapper;
    }

    @GetMapping
    public List<CourseResponse> listCourses() {
        return courseService.listCourses()
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public CourseResponse getCourse(@PathVariable UUID id) {
        try {
            return courseMapper.toResponse(courseService.getCourse(id));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }
}
