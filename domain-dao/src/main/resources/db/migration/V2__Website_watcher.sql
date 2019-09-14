ALTER TABLE events
    ADD contentHash VARCHAR(255) NULL;
ALTER TABLE events
    ADD lastContentCheck DATETIME NULL;
