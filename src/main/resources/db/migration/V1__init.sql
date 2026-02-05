CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE courses (
    id UUID PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(2000) NOT NULL
);

CREATE TABLE subjects (
    id UUID PRIMARY KEY,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    summary VARCHAR(2000) NOT NULL
);

CREATE TABLE topics (
    id UUID PRIMARY KEY,
    subject_id UUID NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    content_summary VARCHAR(2000) NOT NULL
);

CREATE TABLE topic_tests (
    id UUID PRIMARY KEY,
    topic_id UUID NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    total_questions INT NOT NULL,
    passing_score INT NOT NULL
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO courses (id, title, description)
VALUES (gen_random_uuid(), 'Computer Science Fundamentals', 'Start with core concepts across algorithms and data structures.');

INSERT INTO subjects (id, course_id, title, summary)
SELECT gen_random_uuid(), c.id, 'Programming Foundations', 'Core programming concepts and problem-solving.'
FROM courses c
WHERE c.title = 'Computer Science Fundamentals';

INSERT INTO topics (id, subject_id, title, content_summary)
SELECT gen_random_uuid(), s.id, 'Variables', 'Types, scope, and immutability.'
FROM subjects s
WHERE s.title = 'Programming Foundations';

INSERT INTO topics (id, subject_id, title, content_summary)
SELECT gen_random_uuid(), s.id, 'Functions', 'Pure functions and composition.'
FROM subjects s
WHERE s.title = 'Programming Foundations';

INSERT INTO topic_tests (id, topic_id, title, total_questions, passing_score)
SELECT gen_random_uuid(), t.id, 'Variables Quiz', 10, 7
FROM topics t
WHERE t.title = 'Variables';

INSERT INTO topic_tests (id, topic_id, title, total_questions, passing_score)
SELECT gen_random_uuid(), t.id, 'Functions Quiz', 12, 8
FROM topics t
WHERE t.title = 'Functions';
