package com.learnnest.lms.domain.service.admin;

import com.learnnest.lms.api.admin.dto.EnrollmentRequest;
import com.learnnest.lms.domain.model.Course;
import com.learnnest.lms.domain.model.auth.UserAccount;
import com.learnnest.lms.domain.model.enrollment.Enrollment;
import com.learnnest.lms.domain.repository.CourseRepository;
import com.learnnest.lms.domain.repository.auth.UserAccountRepository;
import com.learnnest.lms.domain.repository.enrollment.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminEnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserAccountRepository userAccountRepository;

    public AdminEnrollmentService(
            EnrollmentRepository enrollmentRepository,
            CourseRepository courseRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userAccountRepository = userAccountRepository;
    }

    @Transactional
    public Enrollment enroll(EnrollmentRequest request) {
        if (enrollmentRepository.existsByCourseIdAndUserId(request.courseId(), request.userId())) {
            throw new IllegalArgumentException("Learner is already enrolled");
        }

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        UserAccount user = userAccountRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("Learner not found"));

        if (!"ROLE_STUDENT".equals(user.getRole())) {
            throw new IllegalArgumentException("Only learners can be enrolled");
        }

        Enrollment enrollment = new Enrollment(course, user);
        return enrollmentRepository.save(enrollment);
    }
}
