CREATE TABLE test_attempts (
    id UUID PRIMARY KEY,
    test_id UUID NOT NULL REFERENCES subject_tests(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    score INT NOT NULL,
    total_questions INT NOT NULL,
    passed BOOLEAN NOT NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE test_attempt_answers (
    id UUID PRIMARY KEY,
    attempt_id UUID NOT NULL REFERENCES test_attempts(id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES subject_test_questions(id) ON DELETE CASCADE,
    selected_option_id UUID REFERENCES subject_test_options(id) ON DELETE SET NULL,
    correct BOOLEAN NOT NULL
);

CREATE INDEX test_attempts_test_idx ON test_attempts(test_id);
CREATE INDEX test_attempts_user_idx ON test_attempts(user_id);
CREATE INDEX test_attempt_answers_attempt_idx ON test_attempt_answers(attempt_id);
