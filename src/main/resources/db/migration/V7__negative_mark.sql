ALTER TABLE subject_test_questions
    ADD COLUMN IF NOT EXISTS negative_mark INT NOT NULL DEFAULT -1;
