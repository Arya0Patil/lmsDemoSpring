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
@Table(name = "subject_tests")
public class SubjectTest {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private int passingScore;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<SubjectTestQuestion> questions = new LinkedHashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    protected SubjectTest() {
    }

    public SubjectTest(String title, int totalQuestions, int passingScore) {
        this.title = title;
        this.totalQuestions = totalQuestions;
        this.passingScore = passingScore;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public Set<SubjectTestQuestion> getQuestions() {
        return questions;
    }

    public Subject getSubject() {
        return subject;
    }

    void setSubject(Subject subject) {
        this.subject = subject;
    }

    public void addQuestion(SubjectTestQuestion question) {
        questions.add(question);
        question.setTest(this);
        this.totalQuestions = questions.size();
    }

    public void removeQuestion(SubjectTestQuestion question) {
        questions.remove(question);
        this.totalQuestions = questions.size();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPassingScore(int passingScore) {
        this.passingScore = passingScore;
    }
}
