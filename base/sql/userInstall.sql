CREATE TABLE t_id (
  id INTEGER NOT NULL
);

CREATE TABLE t_group (
  id          INTEGER      NOT NULL,
  change_date TIMESTAMP    NOT NULL DEFAULT now(),
  name        VARCHAR(100) NOT NULL,
  CONSTRAINT t_group_pk PRIMARY KEY (id)
);

CREATE TABLE t_user (
  id                 INTEGER      NOT NULL,
  change_date        TIMESTAMP    NOT NULL DEFAULT now(),
  first_name         VARCHAR(100) NOT NULL DEFAULT '',
  last_name          VARCHAR(100) NOT NULL,
  email              VARCHAR(200) NOT NULL DEFAULT '',
  attributes         TEXT         NOT NULL DEFAULT '',
  login              VARCHAR(30)  NOT NULL,
  pwd                VARCHAR(50)  NOT NULL,
  approval_code      VARCHAR(50)  NOT NULL DEFAULT '',
  approved           BOOLEAN      NOT NULL DEFAULT FALSE,
  failed_login_count INTEGER      NOT NULL DEFAULT 0,
  locked             BOOLEAN      NOT NULL DEFAULT FALSE,
  deleted            BOOLEAN      NOT NULL DEFAULT FALSE,
  CONSTRAINT t_user_pk PRIMARY KEY (id)
);

CREATE TABLE t_user2group (
  user_id  INTEGER NOT NULL,
  group_id INTEGER NOT NULL,
  CONSTRAINT t_user2group_pk PRIMARY KEY (user_id, group_id),
  CONSTRAINT t_user2group_fk1 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE,
  CONSTRAINT t_user2group_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);

-- insert

INSERT INTO t_id (id) VALUES (1000);

INSERT INTO t_group (id, name) VALUES (1, 'Administrators');
INSERT INTO t_group (id, name) VALUES (2, 'Approvers');
INSERT INTO t_group (id, name) VALUES (3, 'Editors');

INSERT INTO t_user (id, first_name, last_name, email, login, pwd, approval_code, approved) VALUES (1, 'Super', 'Administrator', 'xxx@host.tld', 'admin', 'nU4eI71bcnBGqeO0t9tXvY1u5oQ=', '', TRUE);


