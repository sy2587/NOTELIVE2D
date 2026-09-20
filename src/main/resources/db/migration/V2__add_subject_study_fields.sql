ALTER TABLE subjects
    ADD COLUMN study_goal VARCHAR(100) NULL AFTER icon,
    ADD COLUMN progress INT NOT NULL DEFAULT 0 AFTER study_goal,
    ADD COLUMN description VARCHAR(255) NULL AFTER progress;
