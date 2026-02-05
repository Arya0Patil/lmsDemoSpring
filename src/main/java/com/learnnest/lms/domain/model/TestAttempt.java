package com.learnnest.lms.domain.model;

import com.learnnest.lms.domain.model.auth.UserAccount;
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

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "test_attempts")
public class TestAttempt {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_id", nullable = false)
    private SubjectTest test;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Column(nullable = false)
    private int score;

    @Column(name = "total_questions", nullable = false)
    private int totalQuestions;

    @Column(nullable = false)
    private boolean passed;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt = Instant.now();

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<TestAttemptAnswer> answers = new LinkedHashSet<>();

    protected TestAttempt() {
    }

    public TestAttempt(SubjectTest test, UserAccount user, int score, int totalQuestions, boolean passed) {
        this.test = test;
        this.user = user;
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.passed = passed;
    }

    public UUID getId() {
        return id;
    }

    public SubjectTest getTest() {
        return test;
    }

    public UserAccount getUser() {
        return user;
    }

    public int getScore() {
        return score;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public boolean isPassed() {
        return passed;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }

    public Set<TestAttemptAnswer> getAnswers() {
        return answers;
    }

    public void addAnswer(TestAttemptAnswer answer) {
        answers.add(answer);
        answer.setAttempt(this);
    }
}
