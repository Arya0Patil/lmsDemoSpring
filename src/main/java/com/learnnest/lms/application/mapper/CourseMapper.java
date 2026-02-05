package com.learnnest.lms.application.mapper;

import com.learnnest.lms.api.dto.CourseResponse;
import com.learnnest.lms.api.dto.SubjectResponse;
import com.learnnest.lms.api.dto.SubjectTestResponse;
import com.learnnest.lms.domain.model.Course;
import com.learnnest.lms.domain.model.Subject;
import com.learnnest.lms.domain.model.SubjectTest;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
public class CourseMapper {
    public CourseResponse toResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                mapSubjects(course.getSubjects())
        );
    }

    private List<SubjectResponse> mapSubjects(Collection<Subject> subjects) {
        return subjects.stream()
                .map(subject -> new SubjectResponse(
                        subject.getId(),
                        subject.getTitle(),
                        subject.getSummary(),
                        subject.getContentBody(),
                        mapTests(subject.getTests())
                ))
                .toList();
    }

    private List<SubjectTestResponse> mapTests(Collection<SubjectTest> tests) {
        return tests.stream()
                .map(test -> new SubjectTestResponse(
                        test.getId(),
                        test.getTitle(),
                        test.getTotalQuestions(),
                        test.getPassingScore()
                ))
                .toList();
    }
}
