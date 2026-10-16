CREATE TABLE "challenge"
(
    id          integer not null
        constraint pk
            primary key,
    title       TEXT    not null,
    slug        TEXT    not null,
    dojo        TEXT    not null,
    dojoId      TEXT    not null,
    difficulty  TEXT    not null,
    description TEXT    not null,
    tags        TEXT,
    remark      TEXT
);

CREATE TABLE "challenge_language"
(
    challengeId     integer
        constraint challenge_language_challenge_id_fk
            references challenge
            on update set null on delete set null,
    language        TEXT            not null,
    codeTemplate    TEXT            not null,
    id              integer         not null
        constraint challenge_language_pk
            primary key,
    languageVersion TEXT default '' not null
);

CREATE TABLE "leetcode_challenge"
(
    frontendQuestionId TEXT    not null,
    testCase           TEXT,
    id                 INTEGER not null
        constraint leetcode_challenge_pk
            primary key
        constraint leetcode_challenge_challenge_id_fk
            references challenge
);

CREATE TABLE "leetcode_submission"
(
    id                INTEGER not null
        constraint leetcode_submission_pk
            primary key
        constraint leetcode_submission_solution_submission_id_fk
            references solution_submission,
    memory            integer,
    totalCorrect      integer,
    totalTestcases    integer,
    statusMemory      TEXT,
    runtimePercentile REAL,
    memoryPercentile  REAL,
    lastTestcase      TEXT,
    inputFormatted    TEXT,
    expectedOutput    TEXT
, codeOutput TEXT, stdOutput TEXT, statusRuntime TEXT);

CREATE TABLE "solution"
(
    id             integer           not null
        constraint solution_pk
            primary key,
    challengeId    integer           not null
        constraint solution_challenge_id_fk
            references challenge,
    createDateTime INTEGER           not null,
    tags           TEXT,
    remark         TEXT,
    title          TEXT              not null,
    isDefault      INTEGER default 0 not null
);

CREATE TABLE "solution_submission"
(
    id                  INTEGER not null
        constraint solution_submission_pk
            primary key,
    dojoSubmissionId    TEXT,
    submitDateTime      integer not null,
    localCode           TEXT    not null,
    submitCode          TEXT    not null,
    result              TEXT    not null,
    solutionId          integer not null
        constraint solution_submission_solution_id_fk
            references solution,
    challengeLanguageId integer not null
        constraint solution_submission_challenge_language_id_fk
            references challenge_language,
    score               TEXT,
    message             TEXT,
    resultDateTime      integer
);

CREATE UNIQUE INDEX challenge_dojoId_dojo_uindex
    on challenge (dojoId, dojo);
