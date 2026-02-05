package com.learnnest.lms.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "test_attempt_answers")
public class TestAttemptAnswer {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    private TestAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private SubjectTestQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private SubjectTestOption selectedOption;

    @Column(nullable = false)
    private boolean correct;

    protected TestAttemptAnswer() {
    }

    public TestAttemptAnswer(SubjectTestQuestion question, SubjectTestOption selectedOption, boolean correct) {
        this.question = question;
        this.selectedOption = selectedOption;
        this.correct = correct;
    }

    public UUID getId() {
        return id;
    }

    public TestAttempt getAttempt() {
        return attempt;
    }

    void setAttempt(TestAttempt attempt) {
        this.attempt = attempt;
    }

    public SubjectTestQuestion getQuestion() {
        return question;
    }

    public SubjectTestOption getSelectedOption() {
        return selectedOption;
    }

    public boolean isCorrect() {
        return correct;
    }
}
