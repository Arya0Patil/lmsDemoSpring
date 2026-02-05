package com.learnnest.lms.api.learner;

import com.learnnest.lms.api.learner.dto.LearnerTestResponse;
import com.learnnest.lms.api.learner.dto.LearnerTestResultResponse;
import com.learnnest.lms.api.learner.dto.LearnerTestSubmitRequest;
import com.learnnest.lms.domain.service.learner.LearnerTestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/learner/tests")
public class LearnerTestController {
    private final LearnerTestService learnerTestService;

    public LearnerTestController(LearnerTestService learnerTestService) {
        this.learnerTestService = learnerTestService;
    }

    @GetMapping("/{testId}")
    public LearnerTestResponse getTest(@PathVariable UUID testId, Authentication authentication) {
        return learnerTestService.getTest(testId, authentication.getName());
    }

    @PostMapping("/{testId}/submit")
    public LearnerTestResultResponse submit(
            @PathVariable UUID testId,
            @Valid @RequestBody LearnerTestSubmitRequest request,
            Authentication authentication
    ) {
        return learnerTestService.submit(testId, authentication.getName(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}
