
--
CREATE TABLE IF NOT EXISTS t_workflow_page_part
(
    id           INTEGER       NOT NULL,
    project_name VARCHAR(255)  NOT NULL,
    start_date   TIMESTAMP     NOT NULL DEFAULT now(),
    due_date     TIMESTAMP     NULL,
    notes        VARCHAR(2000) NOT NULL DEFAULT '',
    author_name  VARCHAR(255)  NOT NULL,
    owner_id     INTEGER       NOT NULL,
    CONSTRAINT t_workflow_page_part_pk PRIMARY KEY (id),
    CONSTRAINT t_workflow_page_part_fk1 FOREIGN KEY (id) REFERENCES t_page_part (id) ON DELETE CASCADE,
    CONSTRAINT t_workflow_page_part_fk2 FOREIGN KEY (owner_id) REFERENCES t_user (id) ON DELETE SET DEFAULT
);
--
CREATE SEQUENCE IF NOT EXISTS s_workflow_task_id START 1000;
--
CREATE TABLE IF NOT EXISTS t_workflow_task
(
    id                  INTEGER       NOT NULL,
    workflow_id         INTEGER       NOT NULL,
    predecessor_task_id INTEGER       NULL,
    creation_date       TIMESTAMP     NOT NULL DEFAULT now(),
    change_date         TIMESTAMP     NOT NULL DEFAULT now(),
    start_date          TIMESTAMP     NOT NULL,
    due_date            TIMESTAMP     NULL,
    done_date           TIMESTAMP     NULL,
    approve_date        TIMESTAMP     NULL,
    reject_date         TIMESTAMP     NULL,
    name                VARCHAR(60)   NOT NULL,
    notes               VARCHAR(2000) NOT NULL,
    creator_id          INTEGER       NOT NULL DEFAULT 0,
    owner_id            INTEGER       NOT NULL DEFAULT 0,
    approver_id         INTEGER       NOT NULL DEFAULT 0,
    CONSTRAINT t_workflow_task_pk PRIMARY KEY (id),
    CONSTRAINT t_workflow_task_fk1 FOREIGN KEY (workflow_id) REFERENCES t_workflow_page_part (id) ON DELETE CASCADE,
    CONSTRAINT t_workflow_task_fk2 FOREIGN KEY (predecessor_task_id) REFERENCES t_workflow_task (id) ON DELETE CASCADE,
    CONSTRAINT t_workflow_task_fk3 FOREIGN KEY (creator_id) REFERENCES t_user (id),
    CONSTRAINT t_workflow_task_fk4 FOREIGN KEY (owner_id) REFERENCES t_user (id),
    CONSTRAINT t_workflow_task_fk5 FOREIGN KEY (approver_id) REFERENCES t_user (id)
);
--
CREATE SEQUENCE IF NOT EXISTS s_workflow_file_id START 1000;
--
CREATE TABLE IF NOT EXISTS t_workflow_file
(
    id               INTEGER       NOT NULL,
    creation_date    TIMESTAMP     NOT NULL DEFAULT now(),
    change_date      TIMESTAMP     NOT NULL DEFAULT now(),
    workflow_id      INTEGER       NOT NULL,
    workflow_task_id INTEGER       NULL,
    name             VARCHAR(100)  NOT NULL,
    notes            VARCHAR(2000) NOT NULL DEFAULT '',
    author_name      VARCHAR(255)  NOT NULL,
    content_type     VARCHAR(255)  NOT NULL DEFAULT '',
    file_size        INTEGER       NOT NULL DEFAULT 0,
    width            INTEGER       NOT NULL DEFAULT 0,
    height           INTEGER       NOT NULL DEFAULT 0,
    bytes            BYTEA         NOT NULL,
    preview_bytes    BYTEA         NULL,
    CONSTRAINT t_workflow_file_pk PRIMARY KEY (id),
    CONSTRAINT t_workflow_file_fk1 FOREIGN KEY (workflow_id) REFERENCES t_workflow_page_part (id) ON DELETE CASCADE,
    CONSTRAINT t_workflow_file_fk2 FOREIGN KEY (workflow_task_id) REFERENCES t_workflow_task (id) ON DELETE CASCADE
);

