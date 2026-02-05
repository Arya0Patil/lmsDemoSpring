ALTER TABLE subjects
    ADD COLUMN IF NOT EXISTS content_body VARCHAR(10000);

DROP TABLE IF EXISTS topic_test_options CASCADE;
DROP TABLE IF EXISTS topic_test_questions CASCADE;
DROP TABLE IF EXISTS topic_tests CASCADE;
DROP TABLE IF EXISTS topics CASCADE;

CREATE TABLE subject_tests (
    id UUID PRIMARY KEY,
    subject_id UUID NOT NULL REFERENCES subjects(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    total_questions INT NOT NULL,
    passing_score INT NOT NULL
);

CREATE TABLE subject_test_questions (
    id UUID PRIMARY KEY,
    test_id UUID NOT NULL REFERENCES subject_tests(id) ON DELETE CASCADE,
    question_text VARCHAR(2000) NOT NULL,
    difficulty VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    explanation VARCHAR(2000)
);

CREATE TABLE subject_test_options (
    id UUID PRIMARY KEY,
    question_id UUID NOT NULL REFERENCES subject_test_questions(id) ON DELETE CASCADE,
    option_text VARCHAR(1000) NOT NULL,
    is_correct BOOLEAN NOT NULL
);

CREATE INDEX subject_tests_subject_idx ON subject_tests(subject_id);
CREATE INDEX subject_test_questions_test_idx ON subject_test_questions(test_id);
CREATE INDEX subject_test_options_question_idx ON subject_test_options(question_id);
