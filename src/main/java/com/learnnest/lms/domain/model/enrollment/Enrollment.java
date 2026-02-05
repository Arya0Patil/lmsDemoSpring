package com.learnnest.lms.domain.model.enrollment;

import com.learnnest.lms.domain.model.Course;
import com.learnnest.lms.domain.model.auth.UserAccount;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    private Instant enrolledAt = Instant.now();

    protected Enrollment() {
    }

    public Enrollment(Course course, UserAccount user) {
        this.course = course;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public Course getCourse() {
        return course;
    }

    public UserAccount getUser() {
        return user;
    }

    public Instant getEnrolledAt() {
        return enrolledAt;
    }
}
