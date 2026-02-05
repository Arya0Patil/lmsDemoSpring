package com.learnnest.lms.api.admin;

import com.learnnest.lms.api.admin.dto.EnrollmentRequest;
import com.learnnest.lms.api.admin.dto.EnrollmentResponse;
import com.learnnest.lms.api.admin.dto.LearnerResponse;
import com.learnnest.lms.domain.model.enrollment.Enrollment;
import com.learnnest.lms.domain.repository.auth.UserAccountRepository;
import com.learnnest.lms.domain.repository.enrollment.EnrollmentRepository;
import com.learnnest.lms.domain.service.admin.AdminEnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/learners")
@PreAuthorize("hasRole('ADMIN')")
public class AdminLearnerController {
    private final UserAccountRepository userAccountRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AdminEnrollmentService adminEnrollmentService;

    public AdminLearnerController(
            UserAccountRepository userAccountRepository,
            EnrollmentRepository enrollmentRepository,
            AdminEnrollmentService adminEnrollmentService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.adminEnrollmentService = adminEnrollmentService;
    }

    @GetMapping
    public List<LearnerResponse> listLearners() {
        return userAccountRepository.findAllByRoleOrderByCreatedAtDesc("ROLE_STUDENT")
                .stream()
                .map(user -> new LearnerResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    @GetMapping("/courses/{courseId}")
    public List<EnrollmentResponse> listLearnersForCourse(@PathVariable UUID courseId) {
        return enrollmentRepository.findAllByCourseId(courseId)
                .stream()
                .map(this::toEnrollmentResponse)
                .toList();
    }

    @PostMapping("/enrollments")
    @ResponseStatus(HttpStatus.CREATED)
    public EnrollmentResponse enroll(@Valid @RequestBody EnrollmentRequest request) {
        return toEnrollmentResponse(adminEnrollmentService.enroll(request));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex) {
        return ex.getMessage();
    }

    private EnrollmentResponse toEnrollmentResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getCourse().getId(),
                enrollment.getCourse().getTitle(),
                enrollment.getUser().getId(),
                enrollment.getUser().getFullName(),
                enrollment.getUser().getEmail(),
                enrollment.getEnrolledAt()
        );
    }
}
