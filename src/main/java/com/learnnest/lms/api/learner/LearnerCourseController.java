package com.learnnest.lms.api.learner;

import com.learnnest.lms.api.learner.dto.LearnerCourseJourneyResponse;
import com.learnnest.lms.api.learner.dto.LearnerCourseSummaryResponse;
import com.learnnest.lms.domain.service.learner.LearnerCourseService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learner/courses")
public class LearnerCourseController {
    private final LearnerCourseService learnerCourseService;

    public LearnerCourseController(LearnerCourseService learnerCourseService) {
        this.learnerCourseService = learnerCourseService;
    }

    @GetMapping
    public List<LearnerCourseSummaryResponse> listCourses(Authentication authentication) {
        return learnerCourseService.listCourses(authentication.getName());
    }

    @GetMapping("/{courseId}")
    public LearnerCourseJourneyResponse getCourse(@PathVariable UUID courseId, Authentication authentication) {
        try {
            return learnerCourseService.getCourseJourney(courseId, authentication.getName());
        } catch (IllegalArgumentException ex) {
            HttpStatus status = ex.getMessage() != null && ex.getMessage().contains("enrolled")
                    ? HttpStatus.FORBIDDEN
                    : HttpStatus.NOT_FOUND;
            throw new ResponseStatusException(status, ex.getMessage());
        }
    }
}
