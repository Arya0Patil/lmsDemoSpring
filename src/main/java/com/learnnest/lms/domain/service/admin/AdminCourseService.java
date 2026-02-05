package com.learnnest.lms.domain.service.admin;

import com.learnnest.lms.api.admin.dto.AdminCourseRequest;
import com.learnnest.lms.api.admin.dto.AdminSubjectRequest;
import com.learnnest.lms.api.admin.dto.AdminSubjectTestRequest;
import com.learnnest.lms.api.admin.dto.AdminSubjectUpdateRequest;
import com.learnnest.lms.api.admin.dto.AdminTestQuestionRequest;
import com.learnnest.lms.domain.model.Course;
import com.learnnest.lms.domain.model.Subject;
import com.learnnest.lms.domain.model.SubjectTest;
import com.learnnest.lms.domain.model.SubjectTestOption;
import com.learnnest.lms.domain.model.SubjectTestQuestion;
import com.learnnest.lms.domain.repository.CourseRepository;
import com.learnnest.lms.domain.repository.SubjectRepository;
import com.learnnest.lms.domain.repository.SubjectTestQuestionRepository;
import com.learnnest.lms.domain.repository.SubjectTestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AdminCourseService {
    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectTestRepository subjectTestRepository;
    private final SubjectTestQuestionRepository subjectTestQuestionRepository;

    public AdminCourseService(
            CourseRepository courseRepository,
            SubjectRepository subjectRepository,
            SubjectTestRepository subjectTestRepository,
            SubjectTestQuestionRepository subjectTestQuestionRepository
    ) {
        this.courseRepository = courseRepository;
        this.subjectRepository = subjectRepository;
        this.subjectTestRepository = subjectTestRepository;
        this.subjectTestQuestionRepository = subjectTestQuestionRepository;
    }

    @Transactional
    public Course createCourse(AdminCourseRequest request) {
        Course course = new Course(request.title(), request.description());
        return courseRepository.save(course);
    }

    @Transactional
    public Subject addSubject(UUID courseId, AdminSubjectRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Subject subject = new Subject(request.title(), request.summary());
        subject.setContentBody(request.contentBody());
        course.addSubject(subject);
        courseRepository.save(course);
        return subject;
    }

    @Transactional
    public Subject updateSubject(UUID subjectId, AdminSubjectUpdateRequest request) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        subject.setTitle(request.title());
        subject.setSummary(request.summary());
        subject.setContentBody(request.contentBody());
        return subjectRepository.save(subject);
    }

    @Transactional
    public SubjectTest addTest(UUID subjectId, AdminSubjectTestRequest request) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        SubjectTest test = new SubjectTest(request.title(), 0, request.passingScore());
        subject.addTest(test);
        subjectRepository.save(subject);
        return test;
    }

    @Transactional
    public SubjectTestQuestion addQuestion(UUID testId, AdminTestQuestionRequest request) {
        SubjectTest test = subjectTestRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));

        boolean hasCorrect = request.options().stream().anyMatch(option -> option.correct());
        if (!hasCorrect) {
            throw new IllegalArgumentException("At least one option must be marked correct");
        }

        String difficulty = normalizeDifficulty(request.difficulty());
        int negativeMark = normalizeNegativeMark(request.negativeMark());
        SubjectTestQuestion question =
                new SubjectTestQuestion(request.questionText(), difficulty, negativeMark, request.explanation());
        request.options().forEach(option ->
                question.addOption(new SubjectTestOption(option.optionText(), option.correct()))
        );

        test.addQuestion(question);
        subjectTestRepository.save(test);
        return question;
    }

    @Transactional
    public SubjectTestQuestion updateQuestion(UUID questionId, AdminTestQuestionRequest request) {
        SubjectTestQuestion question = subjectTestQuestionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        String difficulty = normalizeDifficulty(request.difficulty());
        int negativeMark = normalizeNegativeMark(request.negativeMark());
        question.setQuestionText(request.questionText());
        question.setDifficulty(difficulty);
        question.setNegativeMark(negativeMark);
        question.setExplanation(request.explanation());
        question.clearOptions();
        request.options().forEach(option ->
                question.addOption(new SubjectTestOption(option.optionText(), option.correct()))
        );

        boolean hasCorrect = question.getOptions().stream().anyMatch(SubjectTestOption::isCorrect);
        if (!hasCorrect) {
            throw new IllegalArgumentException("At least one option must be marked correct");
        }

        return subjectTestQuestionRepository.save(question);
    }

    @Transactional
    public void deleteQuestion(UUID questionId) {
        SubjectTestQuestion question = subjectTestQuestionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        SubjectTest test = question.getTest();
        test.removeQuestion(question);
        subjectTestRepository.save(test);
    }

    @Transactional
    public int addQuestionsFromCsv(UUID testId, InputStream inputStream) {
        SubjectTest test = subjectTestRepository.findById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));

        List<SubjectTestQuestion> questions = parseCsvQuestions(inputStream);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("No questions found in CSV");
        }

        questions.forEach(test::addQuestion);
        subjectTestRepository.save(test);
        return questions.size();
    }

    private List<SubjectTestQuestion> parseCsvQuestions(InputStream inputStream) {
        List<SubjectTestQuestion> questions = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean headerSkipped = false;
            while ((line = reader.readLine()) != null) {
                if (!headerSkipped) {
                    headerSkipped = true;
                    continue;
                }
                if (line.isBlank()) {
                    continue;
                }
                List<String> columns = parseCsvLine(line);
                if (columns.size() < 6) {
                    throw new IllegalArgumentException("Invalid CSV format: expected at least 6 columns");
                }

                String questionText = columns.get(0).trim();
                String optionA = columns.get(1).trim();
                String optionB = columns.get(2).trim();
                String optionC = columns.get(3).trim();
                String optionD = columns.get(4).trim();
                String correct = columns.get(5).trim().toUpperCase();
                String explanation = columns.size() > 6 ? columns.get(6).trim() : "";
                int negativeMark = normalizeNegativeMark(columns.size() > 8 ? parseInteger(columns.get(8).trim()) : null);

                if (questionText.isBlank()) {
                    throw new IllegalArgumentException("Question text cannot be blank");
                }

                String difficulty = normalizeDifficulty(columns.size() > 7 ? columns.get(7).trim() : "");
                SubjectTestQuestion question = new SubjectTestQuestion(
                        questionText,
                        difficulty,
                        negativeMark,
                        explanation.isBlank() ? null : explanation
                );
                question.addOption(new SubjectTestOption(optionA, "A".equals(correct)));
                question.addOption(new SubjectTestOption(optionB, "B".equals(correct)));
                question.addOption(new SubjectTestOption(optionC, "C".equals(correct)));
                question.addOption(new SubjectTestOption(optionD, "D".equals(correct)));

                boolean hasCorrect = question.getOptions().stream().anyMatch(SubjectTestOption::isCorrect);
                if (!hasCorrect) {
                    throw new IllegalArgumentException("Correct option must be A, B, C, or D");
                }

                questions.add(question);
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to read CSV file");
        }
        return questions;
    }

    private String normalizeDifficulty(String value) {
        if (value == null || value.isBlank()) {
            return "MEDIUM";
        }
        String normalized = value.trim().toUpperCase();
        if (!normalized.equals("EASY") && !normalized.equals("MEDIUM") && !normalized.equals("HARD")) {
            throw new IllegalArgumentException("Difficulty must be EASY, MEDIUM, or HARD");
        }
        return normalized;
    }

    private int normalizeNegativeMark(Integer value) {
        if (value == null) {
            return -1;
        }
        return value;
    }

    private Integer parseInteger(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid negative_mark value");
        }
    }

    private List<String> parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '\"') {
                inQuotes = !inQuotes;
            } else if (ch == ',' && !inQuotes) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        result.add(current.toString());
        return result;
    }
}
