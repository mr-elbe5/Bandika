drop table t_timer_task;
CREATE TABLE t_timer_task (
  name            VARCHAR(60)  NOT NULL,
  display_name    VARCHAR(255) NOT NULL,
  interval        VARCHAR(30)  NOT NULL DEFAULT 'CONTINOUS',
  day             INTEGER      NOT NULL DEFAULT 0,
  hour            INTEGER      NOT NULL DEFAULT 0,
  minute          INTEGER      NOT NULL DEFAULT 0,
  last_execution  TIMESTAMP    NULL,
  note_execution  BOOLEAN      NOT NULL DEFAULT FALSE,
  active          BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT t_timer_task_pk PRIMARY KEY (name)
);
--
INSERT INTO t_timer_task (name, display_name, interval, minute, active, note_execution)
VALUES ('heartbeat', 'Heartbeat Task', 'CONTINOUS', 5, TRUE, FALSE);
--
INSERT INTO t_timer_task (name, display_name, interval, hour, active, note_execution)
VALUES ('searchindex', 'Search Index Task', 'CONTINOUS', 1, FALSE, FALSE);