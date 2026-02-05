CREATE TABLE enrollments (
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    enrolled_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT enrollments_course_user_unique UNIQUE (course_id, user_id)
);

CREATE INDEX enrollments_course_idx ON enrollments(course_id);
CREATE INDEX enrollments_user_idx ON enrollments(user_id);
