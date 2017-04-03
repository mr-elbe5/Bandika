-- create

CREATE TABLE IF NOT EXISTS t_id (
  id INTEGER NOT NULL
);
CREATE TABLE IF NOT EXISTS t_locale (
  locale VARCHAR(10)  NOT NULL,
  home   VARCHAR(255) NOT NULL,
  CONSTRAINT t_locale_pk PRIMARY KEY (locale)
);
CREATE TABLE IF NOT EXISTS t_group (
  id          INTEGER      NOT NULL,
  change_date TIMESTAMP    NOT NULL DEFAULT now(),
  name        VARCHAR(100) NOT NULL,
  CONSTRAINT t_group_pk PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS t_user (
  id                 INTEGER      NOT NULL,
  change_date        TIMESTAMP    NOT NULL DEFAULT now(),
  first_name         VARCHAR(100) NOT NULL DEFAULT '',
  last_name          VARCHAR(100) NOT NULL,
  email              VARCHAR(200) NOT NULL DEFAULT '',
  attributes         TEXT         NOT NULL DEFAULT '',
  login              VARCHAR(30)  NOT NULL,
  pwd                VARCHAR(200) NOT NULL,
  pkey               VARCHAR(50)  NOT NULL,
  approval_code      VARCHAR(50)  NOT NULL DEFAULT '',
  approved           BOOLEAN      NOT NULL DEFAULT FALSE,
  failed_login_count INTEGER      NOT NULL DEFAULT 0,
  locked             BOOLEAN      NOT NULL DEFAULT FALSE,
  deleted            BOOLEAN      NOT NULL DEFAULT FALSE,
  CONSTRAINT t_user_pk PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS t_user2locale (
  user_id INTEGER     NOT NULL,
  locale  VARCHAR(10) NOT NULL,
  CONSTRAINT t_user2locale_pk PRIMARY KEY (user_id, locale),
  CONSTRAINT t_user2locale_fk1 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE,
  CONSTRAINT t_user2locale_fk2 FOREIGN KEY (locale) REFERENCES t_locale (locale)
);
CREATE TABLE IF NOT EXISTS t_user2group (
  user_id  INTEGER NOT NULL,
  group_id INTEGER NOT NULL,
  CONSTRAINT t_user2group_pk PRIMARY KEY (user_id, group_id),
  CONSTRAINT t_user2group_fk1 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE,
  CONSTRAINT t_user2group_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_general_rights (
  group_id INTEGER NOT NULL,
  rights   INTEGER NOT NULL,
  CONSTRAINT t_general_right_pk PRIMARY KEY (group_id),
  CONSTRAINT t_general_right_fk1 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_configuration (
  config_key   VARCHAR(30)  NOT NULL,
  config_value VARCHAR(255) NOT NULL,
  CONSTRAINT t_configuration_pk PRIMARY KEY (config_key)
);
CREATE TABLE t_timer_task (
  id               INTEGER      NOT NULL,
  name             VARCHAR(60)  NOT NULL,
  class_name       VARCHAR(255) NOT NULL,
  interval_type    INTEGER      NOT NULL,
  execution_day    INTEGER      NOT NULL DEFAULT 0,
  execution_hour   INTEGER      NOT NULL DEFAULT 0,
  execution_minute INTEGER      NOT NULL DEFAULT 0,
  execution_second INTEGER      NOT NULL DEFAULT 0,
  last_execution   TIMESTAMP    NULL,
  note_execution   BOOLEAN      NOT NULL DEFAULT FALSE,
  active           BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT t_timer_task_pk PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS t_treenode (
  id              INTEGER      NOT NULL,
  creation_date   TIMESTAMP    NOT NULL DEFAULT now(),
  change_date     TIMESTAMP    NOT NULL DEFAULT now(),
  parent_id       INTEGER      NULL,
  ranking         INTEGER      NOT NULL DEFAULT 0,
  name            VARCHAR(100) NOT NULL,
  display_name    VARCHAR(100) NOT NULL DEFAULT '',
  description     VARCHAR(200) NOT NULL DEFAULT '',
  author_name     VARCHAR(255) NOT NULL,
  visible         BOOLEAN      NOT NULL DEFAULT TRUE,
  anonymous       BOOLEAN      NOT NULL DEFAULT TRUE,
  inherits_rights BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT t_treenode_pk PRIMARY KEY (id),
  CONSTRAINT t_treenode_fk1 FOREIGN KEY (parent_id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_treenode_un1 UNIQUE (id, parent_id, name)
);
CREATE TABLE IF NOT EXISTS t_treenode_locale (
  id     INTEGER     NOT NULL,
  locale VARCHAR(10) NOT NULL,
  CONSTRAINT t_treenode_locale_pk PRIMARY KEY (id, locale),
  CONSTRAINT t_treenode_locale_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_treenode_locale_fk2 FOREIGN KEY (locale) REFERENCES t_locale (locale) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_general_treenode_rights (
  group_id INTEGER NOT NULL,
  rights   INTEGER NOT NULL,
  CONSTRAINT t_general_treenode_rights_pk PRIMARY KEY (group_id),
  CONSTRAINT t_general_treenode_rights_fk1 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_treenode_rights (
  treenode_id INTEGER NOT NULL,
  group_id    INTEGER NOT NULL,
  rights      INTEGER NOT NULL,
  CONSTRAINT t_treenode_right_pk PRIMARY KEY (treenode_id, group_id),
  CONSTRAINT t_treenode_right_fk1 FOREIGN KEY (treenode_id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_treenode_right_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_site (
  id              INTEGER NOT NULL,
  inherits_master BOOLEAN NOT NULL DEFAULT TRUE,
  template        VARCHAR(255) NULL,
  CONSTRAINT t_site_pk PRIMARY KEY (id),
  CONSTRAINT t_site_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE
);
-- resource ------------------

CREATE TABLE IF NOT EXISTS t_resource (
  id                INTEGER      NOT NULL,
  keywords          VARCHAR(500) NOT NULL DEFAULT '',
  published_version INTEGER      NOT NULL DEFAULT 0,
  draft_version     INTEGER      NOT NULL DEFAULT 0,
  CONSTRAINT t_resource_pk PRIMARY KEY (id),
  CONSTRAINT t_resource_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE
);
-- file ------------------

CREATE TABLE IF NOT EXISTS t_file (
  id         INTEGER     NOT NULL,
  media_type VARCHAR(60) NOT NULL DEFAULT '',
  CONSTRAINT t_file_pk PRIMARY KEY (id),
  CONSTRAINT t_file_fk1 FOREIGN KEY (id) REFERENCES t_resource (id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_file_content (
  id                   INTEGER      NOT NULL,
  version              INTEGER      NOT NULL DEFAULT 1,
  change_date          TIMESTAMP    NOT NULL DEFAULT now(),
  published            BOOLEAN      NOT NULL DEFAULT FALSE,
  author_name          VARCHAR(255) NOT NULL,
  content_type         VARCHAR(255) NOT NULL DEFAULT '',
  file_size            INTEGER      NOT NULL DEFAULT 0,
  width                INTEGER      NOT NULL DEFAULT 0,
  height               INTEGER      NOT NULL DEFAULT 0,
  bytes                BYTEA        NOT NULL,
  preview_content_type VARCHAR(255) NOT NULL DEFAULT '',
  preview_bytes        BYTEA        NULL,
  CONSTRAINT t_file_content_pk PRIMARY KEY (id, version),
  CONSTRAINT t_file_content_fk1 FOREIGN KEY (id) REFERENCES t_file (id) ON DELETE CASCADE
);
-- page ------------------

CREATE TABLE IF NOT EXISTS t_page (
  id          INTEGER NOT NULL,
  template    VARCHAR(255) NOT NULL,
  CONSTRAINT t_page_pk PRIMARY KEY (id),
  CONSTRAINT t_page_fk1 FOREIGN KEY (id) REFERENCES t_resource (id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_page_content (
  id          INTEGER      NOT NULL,
  version     INTEGER      NOT NULL DEFAULT 1,
  change_date TIMESTAMP    NOT NULL DEFAULT now(),
  published   BOOLEAN      NOT NULL DEFAULT FALSE,
  author_name VARCHAR(255) NOT NULL,
  CONSTRAINT t_page_content_pk PRIMARY KEY (id, version),
  CONSTRAINT t_page_content_fk1 FOREIGN KEY (id) REFERENCES t_page (id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_page_part (
  id          INTEGER     NOT NULL,
  version     INTEGER     NOT NULL DEFAULT 1,
  page_id     INTEGER     NULL,
  change_date TIMESTAMP   NOT NULL DEFAULT now(),
  area        VARCHAR(60) NOT NULL,
  ranking     INTEGER     NOT NULL DEFAULT 0,
  template    VARCHAR(255) NOT NULL,
  content     TEXT        NOT NULL DEFAULT '',
  CONSTRAINT t_page_part_pk PRIMARY KEY (id, version),
  CONSTRAINT t_page_part_fk1 FOREIGN KEY (page_id, version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_shared_page_part (
  id          INTEGER     NOT NULL,
  change_date TIMESTAMP   NOT NULL DEFAULT now(),
  share_name  VARCHAR(60) NOT NULL DEFAULT '',
  template    VARCHAR(255) NOT NULL,
  content     TEXT        NULL,
  CONSTRAINT t_shared_page_part_pk PRIMARY KEY (id)
);
CREATE TABLE IF NOT EXISTS t_shared_part_usage (
  part_id     INTEGER     NOT NULL,
  page_id     INTEGER     NULL,
  version     INTEGER     NOT NULL DEFAULT 1,
  area        VARCHAR(60) NOT NULL,
  ranking     INTEGER     NOT NULL DEFAULT 0,
  change_date TIMESTAMP   NOT NULL DEFAULT now(),
  CONSTRAINT t_shared_part_usage_pk PRIMARY KEY (part_id, page_id, version, area, ranking),
  CONSTRAINT t_shared_part_usage_fk1 FOREIGN KEY (part_id) REFERENCES t_shared_page_part (id) ON DELETE CASCADE,
  CONSTRAINT t_shared_part_usage_fk2 FOREIGN KEY (page_id, version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_page_usage (
  linked_page_id INTEGER NOT NULL,
  page_id        INTEGER NOT NULL,
  page_version   INTEGER NOT NULL,
  CONSTRAINT t_page_usage_pk PRIMARY KEY (linked_page_id, page_id, page_version),
  CONSTRAINT t_page_usage_fk1 FOREIGN KEY (linked_page_id) REFERENCES t_page (id) ON DELETE CASCADE,
  CONSTRAINT t_page_usage_fk2 FOREIGN KEY (page_id, page_version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_shared_page_usage (
  linked_page_id INTEGER NOT NULL,
  part_id        INTEGER NOT NULL,
  CONSTRAINT t_shared_page_usage_pk PRIMARY KEY (linked_page_id, part_id),
  CONSTRAINT t_shared_page_usage_fk1 FOREIGN KEY (linked_page_id) REFERENCES t_page (id) ON DELETE CASCADE,
  CONSTRAINT t_shared_page_usage_fk2 FOREIGN KEY (part_id) REFERENCES t_shared_page_part (id) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_file_page_usage (
  file_id      INTEGER NOT NULL,
  page_id      INTEGER NOT NULL,
  page_version INTEGER NOT NULL,
  CONSTRAINT t_file_usage_pk PRIMARY KEY (file_id, page_id, page_version),
  CONSTRAINT t_file_usage_fk1 FOREIGN KEY (file_id) REFERENCES t_file (id) ON DELETE CASCADE,
  CONSTRAINT t_file_usage_fk2 FOREIGN KEY (page_id, page_version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);
CREATE TABLE IF NOT EXISTS t_shared_file_usage (
  file_id INTEGER NOT NULL,
  part_id INTEGER NOT NULL,
  CONSTRAINT t_shared_file_usage_pk PRIMARY KEY (file_id, part_id),
  CONSTRAINT t_shared_file_usage_fk1 FOREIGN KEY (file_id) REFERENCES t_file (id) ON DELETE CASCADE,
  CONSTRAINT t_shared_file_usage_fk2 FOREIGN KEY (part_id) REFERENCES t_shared_page_part (id) ON DELETE CASCADE
);
-- inserts

INSERT INTO t_id (id)
  SELECT 1000
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_id);
INSERT INTO t_locale (locale, home)
  SELECT
    'en', '/default.srv?act=openAdministration'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_locale
                   WHERE locale = 'en');
INSERT INTO t_group (id, name)
  SELECT
    1, 'Administrators'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_group
                   WHERE id = 1);
INSERT INTO t_general_rights (group_id, rights) (
  SELECT
    1, (1+2+4)
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_general_rights
                   WHERE group_id = 1));
INSERT INTO t_group (id, name) (
  SELECT
    2, 'Approvers'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_group
                   WHERE id = 2));
INSERT INTO t_general_rights (group_id, rights) (
  SELECT
    2, (4)
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_general_rights
                   WHERE group_id = 2));
INSERT INTO t_general_treenode_rights (group_id, rights) (
  SELECT
    2, (8+4+2+1)
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_general_treenode_rights
                   WHERE group_id = 2));
INSERT INTO t_group (id, name)
  SELECT
    3, 'Editors'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_group
                   WHERE id = 3);
INSERT INTO t_general_rights (group_id, rights) (
  SELECT
    3, (4)
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_general_rights
                   WHERE group_id = 3));
INSERT INTO t_general_treenode_rights (group_id, rights) (
  SELECT
    3, (4+2+1)
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_general_treenode_rights
                   WHERE group_id = 3));
INSERT INTO t_group (id, name)
  SELECT
    4, 'Readers'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_group
                   WHERE id = 4);
INSERT INTO t_general_treenode_rights (group_id, rights) (
  SELECT
    4, (1)
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_general_treenode_rights
                   WHERE group_id = 4));
INSERT INTO t_user (id, first_name, last_name, email, login, pwd, pkey, approval_code, approved)
  SELECT
    1, 'Super', 'Administrator', 'xxx@host.tld', 'admin', '', '', '', TRUE
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_user
                   WHERE id = 1);
INSERT INTO t_user2locale (user_id, locale)
  SELECT
    1, 'en'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_user2locale
                   WHERE user_id = 1);
INSERT INTO t_user2group (user_id, group_id) VALUES (1, 1);
INSERT INTO t_configuration (config_key, config_value)
  SELECT
    'appTitle', 'Elbe 5'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_configuration
                   WHERE config_key = 'appTitle');
INSERT INTO t_configuration (config_key, config_value)
  SELECT
    'mailHost', 'localhost'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_configuration
                   WHERE config_key = 'mailHost');
INSERT INTO t_configuration (config_key, config_value)
  SELECT
    'mailSender', 'me@myhost.tld'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_configuration
                   WHERE config_key = 'mailSender');
INSERT INTO t_configuration (config_key, config_value)
  SELECT
    'timerInterval', '30'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_configuration
                   WHERE config_key = 'timerInterval');
INSERT INTO t_timer_task (id, name, class_name, interval_type, execution_minute, active, note_execution)
  SELECT
    500, 'Control Task', 'de.elbe5.webserver.timer.HeartbeatTask', 0, 5, TRUE,
    FALSE
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_timer_task
                   WHERE name = 'Control Task');
INSERT INTO t_configuration (config_key, config_value)
  SELECT
    'maxVersions', '5'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_configuration
                   WHERE config_key = 'maxVersions');
INSERT INTO t_treenode (id, parent_id, ranking, name, display_name, description, author_name, visible, anonymous, inherits_rights)
  SELECT
    1, NULL, 0, '', 'Root', '', 'system', FALSE, TRUE, FALSE
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_treenode
                   WHERE id = 1);
INSERT INTO t_site (id, inherits_master, template)
  SELECT
    1, FALSE, 'defaultMaster.jsp'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_site
                   WHERE id = 1);
INSERT INTO t_treenode (id, parent_id, ranking, name, display_name, description, author_name, visible, anonymous, inherits_rights)
  SELECT
    100, 1, 0, 'en', 'Home', '', 'system', TRUE, TRUE, TRUE
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_treenode
                   WHERE id = 100);
INSERT INTO t_site (id, inherits_master)
  SELECT
    100, TRUE
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_site
                   WHERE id = 100);
INSERT INTO t_treenode_locale (id, locale)

  SELECT
    100, 'en'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_treenode_locale
                   WHERE id = 100);
INSERT INTO t_treenode (id, parent_id, ranking, name, display_name, description, author_name, visible, anonymous, inherits_rights)
  SELECT
    110, 100, 0, 'index', 'Index', '', 'system', TRUE, TRUE, TRUE
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_treenode
                   WHERE id = 110);
INSERT INTO t_resource (id, published_version, draft_version)
  SELECT
    110, 1, 0
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_resource
                   WHERE id = 110);
INSERT INTO t_page (id, template)
  SELECT
    110, 'defaultPage.jsp'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_page
                   WHERE id = 110);
INSERT INTO t_page_content (id, version, author_name)
  SELECT
    110, 1, 'system'
  WHERE NOT EXISTS(SELECT 'x'
                   FROM t_page_content
                   WHERE id = 110);
UPDATE t_locale SET home = '/en/'
WHERE locale = 'en';



