package com.learnnest.lms.api.admin;

import com.learnnest.lms.api.admin.dto.AdminTestDetailResponse;
import com.learnnest.lms.api.admin.dto.AdminTestOptionResponse;
import com.learnnest.lms.api.admin.dto.AdminTestQuestionRequest;
import com.learnnest.lms.api.admin.dto.AdminTestQuestionResponse;
import com.learnnest.lms.domain.model.SubjectTest;
import com.learnnest.lms.domain.model.SubjectTestQuestion;
import com.learnnest.lms.domain.repository.SubjectTestRepository;
import com.learnnest.lms.domain.service.admin.AdminCourseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/admin/tests")
@PreAuthorize("hasRole('ADMIN')")
public class AdminTestController {
    private final AdminCourseService adminCourseService;
    private final SubjectTestRepository subjectTestRepository;

    public AdminTestController(AdminCourseService adminCourseService, SubjectTestRepository subjectTestRepository) {
        this.adminCourseService = adminCourseService;
        this.subjectTestRepository = subjectTestRepository;
    }

    @GetMapping("/{testId}")
    public AdminTestDetailResponse getTest(@PathVariable UUID testId) {
        SubjectTest test = subjectTestRepository.findWithQuestionsById(testId)
                .orElseThrow(() -> new IllegalArgumentException("Test not found"));

        return new AdminTestDetailResponse(
                test.getId(),
                test.getTitle(),
                test.getTotalQuestions(),
                test.getPassingScore(),
                test.getQuestions().stream()
                        .map(question -> new AdminTestQuestionResponse(
                                question.getId(),
                                question.getQuestionText(),
                                question.getDifficulty(),
                                question.getNegativeMark(),
                                question.getExplanation(),
                                question.getOptions().stream()
                                        .map(option -> new AdminTestOptionResponse(
                                                option.getId(),
                                                option.getOptionText(),
                                                option.isCorrect()
                                        ))
                                        .toList()
                        ))
                        .toList()
        );
    }

    @PostMapping("/{testId}/questions")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminTestQuestionResponse addQuestion(
            @PathVariable UUID testId,
            @Valid @RequestBody AdminTestQuestionRequest request
    ) {
        SubjectTestQuestion question = adminCourseService.addQuestion(testId, request);
        return new AdminTestQuestionResponse(
                question.getId(),
                question.getQuestionText(),
                question.getDifficulty(),
                question.getNegativeMark(),
                question.getExplanation(),
                question.getOptions().stream()
                        .map(option -> new AdminTestOptionResponse(
                                option.getId(),
                                option.getOptionText(),
                                option.isCorrect()
                        ))
                .toList()
        );
    }

    @PutMapping("/questions/{questionId}")
    public AdminTestQuestionResponse updateQuestion(
            @PathVariable UUID questionId,
            @Valid @RequestBody AdminTestQuestionRequest request
    ) {
        SubjectTestQuestion question = adminCourseService.updateQuestion(questionId, request);
        return new AdminTestQuestionResponse(
                question.getId(),
                question.getQuestionText(),
                question.getDifficulty(),
                question.getNegativeMark(),
                question.getExplanation(),
                question.getOptions().stream()
                        .map(option -> new AdminTestOptionResponse(
                                option.getId(),
                                option.getOptionText(),
                                option.isCorrect()
                        ))
                        .toList()
        );
    }

    @DeleteMapping("/questions/{questionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(@PathVariable UUID questionId) {
        adminCourseService.deleteQuestion(questionId);
    }

    @PostMapping(value = "/{testId}/questions/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String importQuestions(@PathVariable UUID testId, @RequestParam("file") MultipartFile file)
            throws java.io.IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is required");
        }
        int imported = adminCourseService.addQuestionsFromCsv(testId, file.getInputStream());
        return "Imported " + imported + " questions";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}
