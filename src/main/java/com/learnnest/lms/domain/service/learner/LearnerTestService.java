package com.learnnest.lms.domain.service.learner;

import com.learnnest.lms.api.learner.dto.LearnerTestAnswerRequest;
import com.learnnest.lms.api.learner.dto.LearnerTestAnswerReview;
import com.learnnest.lms.api.learner.dto.LearnerTestOptionResponse;
import com.learnnest.lms.api.learner.dto.LearnerTestQuestionResponse;
import com.learnnest.lms.api.learner.dto.LearnerTestResponse;
import com.learnnest.lms.api.learner.dto.LearnerTestResultResponse;
import com.learnnest.lms.api.learner.dto.LearnerTestSubmitRequest;
import com.learnnest.lms.domain.model.SubjectTest;
import com.learnnest.lms.domain.model.SubjectTestOption;
import com.learnnest.lms.domain.model.SubjectTestQuestion;
import com.learnnest.lms.domain.model.TestAttempt;
import com.learnnest.lms.domain.model.TestAttemptAnswer;
import com.learnnest.lms.domain.model.auth.UserAccount;
import com.learnnest.lms.domain.repository.SubjectTestRepository;
import com.learnnest.lms.domain.repository.TestAttemptRepository;
import com.learnnest.lms.domain.repository.auth.UserAccountRepository;
import com.learnnest.lms.domain.repository.enrollment.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class LearnerTestService {
    private final SubjectTestRepository subjectTestRepository;
    private final UserAccountRepository userAccountRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final TestAttemptRepository testAttemptRepository;

    public LearnerTestService(
            SubjectTestRepository subjectTestRepository,
            UserAccountRepository userAccountRepository,
            EnrollmentRepository enrollmentRepository,
            TestAttemptRepository testAttemptRepository
    ) {
        this.subjectTestRepository = subjectTestRepository;
        this.userAccountRepository = userAccountRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.testAttemptRepository = testAttemptRepository;
    }

    @Transactional(readOnly = true)
    public LearnerTestResponse getTest(UUID testId, String email) {
        SubjectTest test = subjectTestRepository.findWithQuestionsById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));

        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UUID courseId = test.getSubject().getCourse().getId();
        if (!enrollmentRepository.existsByCourseIdAndUserId(courseId, user.getId())) {
            throw new IllegalArgumentException("You are not enrolled in this course");
        }

        return new LearnerTestResponse(
                test.getId(),
                test.getTitle(),
                test.getTotalQuestions(),
                test.getPassingScore(),
                test.getQuestions().stream()
                        .map(question -> new LearnerTestQuestionResponse(
                                question.getId(),
                                question.getQuestionText(),
                                question.getDifficulty(),
                                question.getOptions().stream()
                                        .map(option -> new LearnerTestOptionResponse(option.getId(), option.getOptionText()))
                                        .toList()
                        ))
                        .toList()
        );
    }

    @Transactional
    public LearnerTestResultResponse submit(UUID testId, String email, LearnerTestSubmitRequest request) {
        SubjectTest test = subjectTestRepository.findWithQuestionsById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));

        UserAccount user = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UUID courseId = test.getSubject().getCourse().getId();
        if (!enrollmentRepository.existsByCourseIdAndUserId(courseId, user.getId())) {
            throw new IllegalArgumentException("You are not enrolled in this course");
        }

        Map<UUID, UUID> submitted = new HashMap<>();
        for (LearnerTestAnswerRequest answer : request.answers()) {
            submitted.put(answer.questionId(), answer.selectedOptionId());
        }

        List<LearnerTestAnswerReview> reviews = test.getQuestions().stream()
                .map(question -> {
                    UUID selectedOptionId = submitted.get(question.getId());
                    SubjectTestOption selectedOption = null;
                    SubjectTestOption correctOption = question.getOptions().stream()
                            .filter(SubjectTestOption::isCorrect)
                            .findFirst()
                            .orElse(null);

                    if (selectedOptionId != null) {
                        selectedOption = question.getOptions().stream()
                                .filter(option -> option.getId().equals(selectedOptionId))
                                .findFirst()
                                .orElseThrow(() -> new IllegalArgumentException("Invalid option selected"));
                    }

                    boolean correct = selectedOption != null && selectedOption.isCorrect();
                    return new LearnerTestAnswerReview(
                            question.getId(),
                            selectedOptionId,
                            correctOption != null ? correctOption.getId() : null,
                            correct,
                            question.getExplanation()
                    );
                })
                .toList();

        int score = 0;
        for (LearnerTestAnswerReview review : reviews) {
            if (review.correct()) {
                score += 1;
            } else if (review.selectedOptionId() != null) {
                SubjectTestQuestion question = test.getQuestions().stream()
                        .filter(q -> q.getId().equals(review.questionId()))
                        .findFirst()
                        .orElseThrow();
                score += question.getNegativeMark();
            }
        }
        boolean passed = score >= test.getPassingScore();
        TestAttempt attempt = new TestAttempt(test, user, score, test.getTotalQuestions(), passed);

        for (LearnerTestAnswerReview review : reviews) {
            SubjectTestQuestion question = test.getQuestions().stream()
                    .filter(q -> q.getId().equals(review.questionId()))
                    .findFirst()
                    .orElseThrow();

            SubjectTestOption selectedOption = null;
            if (review.selectedOptionId() != null) {
                selectedOption = question.getOptions().stream()
                        .filter(option -> option.getId().equals(review.selectedOptionId()))
                        .findFirst()
                        .orElse(null);
            }

            attempt.addAnswer(new TestAttemptAnswer(question, selectedOption, review.correct()));
        }

        testAttemptRepository.save(attempt);

        return new LearnerTestResultResponse(
                test.getId(),
                score,
                test.getTotalQuestions(),
                test.getPassingScore(),
                passed,
                reviews
        );
    }
}
