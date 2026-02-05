package com.learnnest.lms.api.admin;

import com.learnnest.lms.api.admin.dto.AdminCourseRequest;
import com.learnnest.lms.api.admin.dto.AdminSubjectRequest;
import com.learnnest.lms.api.admin.dto.AdminSubjectTestRequest;
import com.learnnest.lms.api.admin.dto.AdminSubjectUpdateRequest;
import com.learnnest.lms.api.dto.CourseResponse;
import com.learnnest.lms.application.mapper.CourseMapper;
import com.learnnest.lms.domain.model.Subject;
import com.learnnest.lms.domain.model.SubjectTest;
import com.learnnest.lms.domain.repository.CourseRepository;
import com.learnnest.lms.domain.service.admin.AdminCourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/courses")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCourseController {
    private final AdminCourseService adminCourseService;
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    public AdminCourseController(
            AdminCourseService adminCourseService,
            CourseRepository courseRepository,
            CourseMapper courseMapper
    ) {
        this.adminCourseService = adminCourseService;
        this.courseRepository = courseRepository;
        this.courseMapper = courseMapper;
    }

    @GetMapping
    public List<CourseResponse> listCourses() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toResponse)
                .toList();
    }

    @GetMapping("/{courseId}")
    public CourseResponse getCourse(@PathVariable UUID courseId) {
        return courseRepository.findById(courseId)
                .map(courseMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(@Valid @RequestBody AdminCourseRequest request) {
        return courseMapper.toResponse(adminCourseService.createCourse(request));
    }

    @PostMapping("/{courseId}/subjects")
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse addSubject(@PathVariable UUID courseId, @Valid @RequestBody AdminSubjectRequest request) {
        Subject subject = adminCourseService.addSubject(courseId, request);
        return courseRepository.findById(subject.getCourse().getId())
                .map(courseMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    }

    @PutMapping("/subjects/{subjectId}")
    public CourseResponse updateSubject(@PathVariable UUID subjectId, @Valid @RequestBody AdminSubjectUpdateRequest request) {
        Subject subject = adminCourseService.updateSubject(subjectId, request);
        return courseRepository.findById(subject.getCourse().getId())
                .map(courseMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    }

    @PostMapping("/subjects/{subjectId}/tests")
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse addTest(@PathVariable UUID subjectId, @Valid @RequestBody AdminSubjectTestRequest request) {
        SubjectTest test = adminCourseService.addTest(subjectId, request);
        return courseRepository.findById(test.getSubject().getCourse().getId())
                .map(courseMapper::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}
