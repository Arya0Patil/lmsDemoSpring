package com.learnnest.lms.domain.service.learner;

import com.learnnest.lms.api.learner.dto.LearnerCourseJourneyResponse;
import com.learnnest.lms.api.learner.dto.LearnerCourseSummaryResponse;
import com.learnnest.lms.api.learner.dto.LearnerSubjectJourneyResponse;
import com.learnnest.lms.api.learner.dto.LearnerTestJourneyResponse;
import com.learnnest.lms.domain.model.Course;
import com.learnnest.lms.domain.model.Subject;
import com.learnnest.lms.domain.model.SubjectTest;
import com.learnnest.lms.domain.model.TestAttempt;
import com.learnnest.lms.domain.model.auth.UserAccount;
import com.learnnest.lms.domain.model.enrollment.Enrollment;
import com.learnnest.lms.domain.repository.TestAttemptRepository;
import com.learnnest.lms.domain.repository.auth.UserAccountRepository;
import com.learnnest.lms.domain.repository.enrollment.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LearnerCourseService {
    private final EnrollmentRepository enrollmentRepository;
    private final UserAccountRepository userAccountRepository;
    private final TestAttemptRepository testAttemptRepository;

    public LearnerCourseService(
            EnrollmentRepository enrollmentRepository,
            UserAccountRepository userAccountRepository,
            TestAttemptRepository testAttemptRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.userAccountRepository = userAccountRepository;
        this.testAttemptRepository = testAttemptRepository;
    }

    @Transactional(readOnly = true)
    public List<LearnerCourseSummaryResponse> listCourses(String email) {
        UserAccount user = findUser(email);
        List<Enrollment> enrollments = enrollmentRepository.findAllByUserId(user.getId());
        List<LearnerCourseSummaryResponse> summaries = new ArrayList<>();

        for (Enrollment enrollment : enrollments) {
            Course course = enrollment.getCourse();
            CourseProgress progress = buildCourseProgress(course, user.getId());
            summaries.add(new LearnerCourseSummaryResponse(
                    course.getId(),
                    course.getTitle(),
                    course.getDescription(),
                    progress.totalTests(),
                    progress.completedTests(),
                    progress.progressPercent()
            ));
        }

        return summaries;
    }

    @Transactional(readOnly = true)
    public LearnerCourseJourneyResponse getCourseJourney(UUID courseId, String email) {
        UserAccount user = findUser(email);
        Enrollment enrollment = enrollmentRepository.findByCourseIdAndUserId(courseId, user.getId())
                .orElseThrow(() -> new IllegalArgumentException("You are not enrolled in this course"));
        Course course = enrollment.getCourse();
        CourseProgress progress = buildCourseProgress(course, user.getId());

        return new LearnerCourseJourneyResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                progress.totalTests(),
                progress.completedTests(),
                progress.progressPercent(),
                progress.nextTestId(),
                mapSubjects(course.getSubjects(), progress.latestAttemptByTestId())
        );
    }

    private List<LearnerSubjectJourneyResponse> mapSubjects(Iterable<Subject> subjects, Map<UUID, TestAttempt> attempts) {
        List<LearnerSubjectJourneyResponse> responses = new ArrayList<>();
        for (Subject subject : subjects) {
            List<LearnerTestJourneyResponse> tests = new ArrayList<>();
            for (SubjectTest test : subject.getTests()) {
                TestAttempt attempt = attempts.get(test.getId());
                tests.add(new LearnerTestJourneyResponse(
                        test.getId(),
                        test.getTitle(),
                        test.getTotalQuestions(),
                        test.getPassingScore(),
                        attempt != null,
                        attempt != null && attempt.isPassed(),
                        attempt == null ? null : attempt.getScore()
                ));
            }
            responses.add(new LearnerSubjectJourneyResponse(
                    subject.getId(),
                    subject.getTitle(),
                    subject.getSummary(),
                    subject.getContentBody(),
                    tests
            ));
        }
        return responses;
    }

    private CourseProgress buildCourseProgress(Course course, UUID userId) {
        List<SubjectTest> tests = new ArrayList<>();
        for (Subject subject : course.getSubjects()) {
            tests.addAll(subject.getTests());
        }

        if (tests.isEmpty()) {
            return new CourseProgress(0, 0, 0, Map.of(), null);
        }

        List<UUID> testIds = tests.stream().map(SubjectTest::getId).toList();
        List<TestAttempt> attempts = testAttemptRepository.findAllByUserIdAndTestIdIn(userId, testIds);
        Map<UUID, TestAttempt> latestAttemptByTestId = pickLatestAttempts(attempts);

        int totalTests = tests.size();
        int completedTests = latestAttemptByTestId.size();
        int progressPercent = (int) Math.round((completedTests * 100.0) / totalTests);
        UUID nextTestId = findNextTestId(tests, latestAttemptByTestId);

        return new CourseProgress(totalTests, completedTests, progressPercent, latestAttemptByTestId, nextTestId);
    }

    private Map<UUID, TestAttempt> pickLatestAttempts(List<TestAttempt> attempts) {
        Map<UUID, TestAttempt> latestByTest = new HashMap<>();
        for (TestAttempt attempt : attempts) {
            TestAttempt current = latestByTest.get(attempt.getTest().getId());
            if (current == null || attempt.getSubmittedAt().isAfter(current.getSubmittedAt())) {
                latestByTest.put(attempt.getTest().getId(), attempt);
            }
        }
        return latestByTest;
    }

    private UUID findNextTestId(List<SubjectTest> tests, Map<UUID, TestAttempt> attempts) {
        return tests.stream()
                .filter(test -> !attempts.containsKey(test.getId()))
                .map(SubjectTest::getId)
                .findFirst()
                .orElse(null);
    }

    private UserAccount findUser(String email) {
        return userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private record CourseProgress(
            int totalTests,
            int completedTests,
            int progressPercent,
            Map<UUID, TestAttempt> latestAttemptByTestId,
            UUID nextTestId
    ) {
    }
}
