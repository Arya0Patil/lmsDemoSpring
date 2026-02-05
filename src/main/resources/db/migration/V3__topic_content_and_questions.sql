ALTER TABLE topics
    ADD COLUMN content_body VARCHAR(10000);

CREATE TABLE topic_test_questions (
    id UUID PRIMARY KEY,
    test_id UUID NOT NULL REFERENCES topic_tests(id) ON DELETE CASCADE,
    question_text VARCHAR(2000) NOT NULL,
    explanation VARCHAR(2000)
);

CREATE TABLE topic_test_options (
    id UUID PRIMARY KEY,
    question_id UUID NOT NULL REFERENCES topic_test_questions(id) ON DELETE CASCADE,
    option_text VARCHAR(1000) NOT NULL,
    is_correct BOOLEAN NOT NULL
);

CREATE INDEX topic_test_questions_test_idx ON topic_test_questions(test_id);
CREATE INDEX topic_test_options_question_idx ON topic_test_options(question_id);
