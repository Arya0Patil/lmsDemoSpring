package com.learnnest.lms.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "subject_test_questions")
public class SubjectTestQuestion {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(name = "question_text", nullable = false, length = 2000)
    private String questionText;

    @Column(nullable = false)
    private String difficulty = "MEDIUM";

    @Column(name = "negative_mark", nullable = false)
    private int negativeMark = -1;

    @Column(length = 2000)
    private String explanation;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SubjectTestOption> options = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private SubjectTest test;

    protected SubjectTestQuestion() {
    }

    public SubjectTestQuestion(String questionText, String difficulty, int negativeMark, String explanation) {
        this.questionText = questionText;
        this.difficulty = difficulty;
        this.negativeMark = negativeMark;
        this.explanation = explanation;
    }

    public UUID getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public int getNegativeMark() {
        return negativeMark;
    }

    public String getExplanation() {
        return explanation;
    }

    public Set<SubjectTestOption> getOptions() {
        return options;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public void setNegativeMark(int negativeMark) {
        this.negativeMark = negativeMark;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public SubjectTest getTest() {
        return test;
    }

    void setTest(SubjectTest test) {
        this.test = test;
    }

    public void addOption(SubjectTestOption option) {
        options.add(option);
        option.setQuestion(this);
    }

    public void clearOptions() {
        options.clear();
    }
}
