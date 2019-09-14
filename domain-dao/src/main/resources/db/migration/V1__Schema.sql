-- auto-generated definition
CREATE TABLE   IF NOT EXISTS  organizations (
    sherdogUrl VARCHAR(1000) NOT NULL
        PRIMARY KEY,
    lastUpdate DATETIME      NULL,
    name       VARCHAR(255)  NULL
);

CREATE  TABLE  IF NOT EXISTS  events (
    sherdogUrl      VARCHAR(1000) NOT NULL
        PRIMARY KEY,
    date            DATETIME      NULL,
    organization_id VARCHAR(1000) NULL,
    name            VARCHAR(255)  NULL,
    location        VARCHAR(255)  NULL,
    lastUpdate      DATETIME      NULL
);

-- auto-generated definition
CREATE TABLE   IF NOT EXISTS  fights (
    id          BIGINT AUTO_INCREMENT
        PRIMARY KEY,
    fighter1_id VARCHAR(1000)             NULL,
    fighter2_id VARCHAR(1000)             NULL,
    event_id    VARCHAR(1000)             NULL,
    date        DATETIME                  NULL,
    result      VARCHAR(100)              NULL,
    winMethod   VARCHAR(255)              NULL,
    winTime     VARCHAR(255)              NULL,
    winRound    INT                       NULL,
    lastUpdate  DATETIME                  NULL,
    fight_type  VARCHAR(50) DEFAULT 'PRO' NULL,
    CONSTRAINT primary_key
        UNIQUE (fighter1_id, fighter2_id, event_id)
);

CREATE TABLE   IF NOT EXISTS  fighters (
    sherdogUrl  VARCHAR(1000)         NOT NULL
        PRIMARY KEY,
    lastUpdate  DATETIME              NULL,
    name        VARCHAR(255)          NULL,
    birthday    DATE                  NULL,
    draws       INT                   NULL,
    losses      INT                   NULL,
    wins        INT                   NULL,
    weight      VARCHAR(255)          NULL,
    height      VARCHAR(255)          NULL,
    nickname    VARCHAR(255)          NULL,
    nc          INT                   NULL,
    search_rank INT(6) DEFAULT 999999 NULL,
    winKo       INT(4) DEFAULT 0      NULL,
    winSub      INT(4) DEFAULT 0      NULL,
    winDec      INT(4) DEFAULT 0      NULL,
    lossKo      INT(4) DEFAULT 0      NULL,
    lossSub     INT(4) DEFAULT 0      NULL,
    lossDec     INT(4) DEFAULT 0      NULL
);


-- auto-generated definition
CREATE TABLE   IF NOT EXISTS  stats_categories (
    id          VARCHAR(255)  NOT NULL
        PRIMARY KEY,
    name        VARCHAR(255)  NULL,
    description TEXT          NULL,
    `order`     INT DEFAULT 0 NOT NULL,
    lastUpdate  DATETIME      NULL
);

-- auto-generated definition
CREATE TABLE   IF NOT EXISTS  stats_entries (
    id           INT AUTO_INCREMENT
        PRIMARY KEY,
    category_id  VARCHAR(255)  NULL,
    fighter_id   VARCHAR(999)  NULL,
    percent      INT(3)        NULL,
    `rank`       INT DEFAULT 0 NOT NULL,
    text_to_show VARCHAR(255)  NULL,
    details      TEXT          NULL,
    lastUpdate   DATETIME      NULL
);


-- auto-generated definition
CREATE TABLE   IF NOT EXISTS  hype_trains (
    user      VARCHAR(255)         NOT NULL,
    fighter   VARCHAR(1000)        NOT NULL,
    nextFight BIGINT               NULL,
    notified  TINYINT(1) DEFAULT 0 NULL,
    PRIMARY KEY (user, fighter)
);

CREATE INDEX hype_trains_fights_id_fk
    ON hype_trains (nextFight);
-- auto-generated definition

CREATE TABLE   IF NOT EXISTS  hype_trains_stats (
    month   VARCHAR(7)       NOT NULL,
    fighter VARCHAR(1000)    NOT NULL,
    count   BIGINT DEFAULT 0 NOT NULL,
    PRIMARY KEY (month, fighter)
);
-- auto-generated definition
CREATE TABLE   IF NOT EXISTS  jdbc_test (
    a CHAR NULL
);


