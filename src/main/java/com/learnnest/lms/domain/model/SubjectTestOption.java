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
@Table(name = "subject_test_options")
public class SubjectTestOption {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "option_text", nullable = false, length = 1000)
    private String optionText;

    @Column(name = "is_correct", nullable = false)
    private boolean correct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private SubjectTestQuestion question;

    protected SubjectTestOption() {
    }

    public SubjectTestOption(String optionText, boolean correct) {
        this.optionText = optionText;
        this.correct = correct;
    }

    public UUID getId() {
        return id;
    }

    public String getOptionText() {
        return optionText;
    }

    public boolean isCorrect() {
        return correct;
    }

    public SubjectTestQuestion getQuestion() {
        return question;
    }

    void setQuestion(SubjectTestQuestion question) {
        this.question = question;
    }
}
