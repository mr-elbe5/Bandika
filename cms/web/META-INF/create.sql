-- create
--
CREATE TABLE IF NOT EXISTS t_id (
  id INTEGER NOT NULL
);
--
INSERT INTO t_id (id) VALUES (1000);
--
CREATE TABLE IF NOT EXISTS t_locale (
  locale    VARCHAR(10)  NOT NULL,
  home      VARCHAR(255) NOT NULL,
  tree_id   INTEGER      NOT NULL DEFAULT 0,
  CONSTRAINT t_locale_pk PRIMARY KEY (locale)
);
--
CREATE TABLE IF NOT EXISTS t_group (
  id          INTEGER      NOT NULL,
  change_date TIMESTAMP    NOT NULL DEFAULT now(),
  name        VARCHAR(100) NOT NULL,
  notes       VARCHAR(500) NOT NULL DEFAULT '',
  CONSTRAINT t_group_pk PRIMARY KEY (id)
);
--
CREATE TABLE IF NOT EXISTS t_user (
  id                 INTEGER      NOT NULL,
  change_date        TIMESTAMP    NOT NULL DEFAULT now(),
  title              VARCHAR(30)  NOT NULL DEFAULT '',
  first_name         VARCHAR(100) NOT NULL DEFAULT '',
  last_name          VARCHAR(100) NOT NULL,
  street             VARCHAR(100) NOT NULL DEFAULT '',
  zipCode            VARCHAR(16)  NOT NULL DEFAULT '',
  city               VARCHAR(50)  NOT NULL DEFAULT '',
  country            VARCHAR(50)  NOT NULL DEFAULT '',
  locale             VARCHAR(10)  NOT NULL DEFAULT 'en',
  email              VARCHAR(100) NOT NULL DEFAULT '',
  phone              VARCHAR(50)  NOT NULL DEFAULT '',
  fax                VARCHAR(50)  NOT NULL DEFAULT '',
  mobile             VARCHAR(50)  NOT NULL DEFAULT '',
  notes              VARCHAR(500) NOT NULL DEFAULT '',
  portrait_name      VARCHAR(255) NOT NULL DEFAULT '',
  portrait           BYTEA        NULL,
  login              VARCHAR(30)  NOT NULL,
  pwd                VARCHAR(100) NOT NULL,
  pkey               VARCHAR(50)  NOT NULL,
  approval_code      VARCHAR(50)  NOT NULL DEFAULT '',
  approved           BOOLEAN      NOT NULL DEFAULT FALSE,
  failed_login_count INTEGER      NOT NULL DEFAULT 0,
  locked             BOOLEAN      NOT NULL DEFAULT FALSE,
  deleted            BOOLEAN      NOT NULL DEFAULT FALSE,
  CONSTRAINT t_user_pk PRIMARY KEY (id),
  CONSTRAINT t_user_fk1 FOREIGN KEY (locale) REFERENCES t_locale (locale) ON DELETE SET DEFAULT
);
--
CREATE TABLE IF NOT EXISTS t_user2group (
  user_id  INTEGER NOT NULL,
  group_id INTEGER NOT NULL,
  relation VARCHAR(20) NOT NULL,
  CONSTRAINT t_user2group_pk PRIMARY KEY (user_id, group_id, relation),
  CONSTRAINT t_user2group_fk1 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE,
  CONSTRAINT t_user2group_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_user2user (
  user_id  INTEGER NOT NULL,
  user2_id INTEGER NOT NULL,
  relation VARCHAR(20) NOT NULL,
  CONSTRAINT t_user2user_pk PRIMARY KEY (user_id, user2_id),
  CONSTRAINT t_user2user_fk1 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE,
  CONSTRAINT t_user2user_fk2 FOREIGN KEY (user2_id) REFERENCES t_user (id) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_system_right (
  name     VARCHAR(30) NOT NULL,
  group_id INTEGER     NOT NULL,
  value    VARCHAR(20) NOT NULL,
  CONSTRAINT t_system_right_pk PRIMARY KEY (name, group_id),
  CONSTRAINT t_system_fk1 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_configuration (
  defaultLocale   VARCHAR(30)  NOT NULL,
  mailHost        VARCHAR(255)  NOT NULL,
  mailPort        INTEGER  NOT NULL,
  mailConnectionType   VARCHAR(30)  NOT NULL,
  mailUser        VARCHAR(255)  NOT NULL,
  mailPassword    VARCHAR(255)  NOT NULL,
  mailSender      VARCHAR(255)  NOT NULL,
  timerInterval   INTEGER  NOT NULL
);
--
CREATE TABLE IF NOT EXISTS t_dynamics (
  change_date    TIMESTAMP  NOT NULL DEFAULT now(),
  css_code       TEXT       NOT NULL DEFAULT '',
  js_code        TEXT       NOT NULL DEFAULT ''
);
--
INSERT INTO t_dynamics (css_code, js_code) VALUES ('','');
--
CREATE TABLE t_timer_task (
  name            VARCHAR(60)  NOT NULL,
  display_name    VARCHAR(255) NOT NULL,
  execution_interval VARCHAR(30)  NOT NULL,
  day             INTEGER      NOT NULL DEFAULT 0,
  hour            INTEGER      NOT NULL DEFAULT 0,
  minute          INTEGER      NOT NULL DEFAULT 0,
  last_execution  TIMESTAMP    NULL,
  note_execution  BOOLEAN      NOT NULL DEFAULT FALSE,
  active          BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT t_timer_task_pk PRIMARY KEY (name)
);
--
CREATE TABLE t_template (
  name         VARCHAR(60)  NOT NULL,
  type         VARCHAR(20)  NOT NULL,
  change_date  TIMESTAMP    NOT NULL DEFAULT now(),
  display_name VARCHAR(100) NOT NULL DEFAULT '',
  description  VARCHAR(255) NOT NULL DEFAULT '',
  section_types VARCHAR(100) NOT NULL DEFAULT '',
  code         TEXT         NOT NULL DEFAULT '',
  CONSTRAINT t_template_pk PRIMARY KEY (name, type)
);
--
CREATE TABLE IF NOT EXISTS t_treenode (
  id              INTEGER      NOT NULL,
  creation_date   TIMESTAMP    NOT NULL DEFAULT now(),
  change_date     TIMESTAMP    NOT NULL DEFAULT now(),
  parent_id       INTEGER      NULL,
  ranking         INTEGER      NOT NULL DEFAULT 0,
  name            VARCHAR(100) NOT NULL,
  display_name    VARCHAR(100) NOT NULL DEFAULT '',
  description     VARCHAR(200) NOT NULL DEFAULT '',
  keywords        VARCHAR(500) NOT NULL DEFAULT '',
  owner_id        INTEGER      NOT NULL DEFAULT 1,
  author_name     VARCHAR(255) NOT NULL,
  in_navigation   BOOLEAN      NOT NULL DEFAULT TRUE,
  anonymous       BOOLEAN      NOT NULL DEFAULT TRUE,
  inherits_rights BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT t_treenode_pk PRIMARY KEY (id),
  CONSTRAINT t_treenode_fk1 FOREIGN KEY (parent_id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_treenode_fk2 FOREIGN KEY (owner_id) REFERENCES t_user (id) ON DELETE SET DEFAULT,
  CONSTRAINT t_treenode_un1 UNIQUE (id, parent_id, name)
);
--
CREATE TABLE IF NOT EXISTS t_treenode_right (
  id       INTEGER     NOT NULL,
  group_id INTEGER     NOT NULL,
  value    VARCHAR(20) NOT NULL,
  CONSTRAINT t_treenode_right_pk PRIMARY KEY (id, group_id),
  CONSTRAINT t_treenode_right_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_treenode_right_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_treenode2user (
  id  INTEGER           NOT NULL,
  user_id INTEGER       NOT NULL,
  relation VARCHAR(20)  NOT NULL,
  CONSTRAINT t_treenode2user_pk PRIMARY KEY (id, user_id),
  CONSTRAINT t_treenode2user_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_treenode2user_fk2 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_treenode2group (
  id  INTEGER           NOT NULL,
  group_id INTEGER      NOT NULL,
  relation VARCHAR(20)  NOT NULL,
  CONSTRAINT t_treenode2group_pk PRIMARY KEY (id, group_id),
  CONSTRAINT t_treenode2group_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_treenode2group_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_site (
  id              INTEGER      NOT NULL,
  template_type   VARCHAR(20)  NOT NULL DEFAULT 'MASTER',
  template        VARCHAR(255) NULL,
  inherits_master BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT t_site_pk PRIMARY KEY (id),
  CONSTRAINT t_site_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_site_fk2 FOREIGN KEY (template, template_type) REFERENCES t_template (name, type)
);
--
-- file ------------------
--
CREATE TABLE IF NOT EXISTS t_file (
  id                   INTEGER      NOT NULL,
  content_type         VARCHAR(255) NOT NULL DEFAULT '',
  file_size            INTEGER      NOT NULL DEFAULT 0,
  width                INTEGER      NOT NULL DEFAULT 0,
  height               INTEGER      NOT NULL DEFAULT 0,
  bytes                BYTEA        NOT NULL,
  preview_content_type VARCHAR(255) NOT NULL DEFAULT '',
  preview_bytes        BYTEA        NULL,
  CONSTRAINT t_file_pk PRIMARY KEY (id),
  CONSTRAINT t_file_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE
);
--
-- page ------------------
--
CREATE TABLE IF NOT EXISTS t_page (
  id            INTEGER      NOT NULL,
  template_type VARCHAR(20)  NOT NULL DEFAULT 'PAGE',
  template      VARCHAR(255) NOT NULL,
  publish_date  TIMESTAMP    NULL,
  published_content TEXT     NOT NULL DEFAULT '',
  CONSTRAINT t_page_pk PRIMARY KEY (id),
  CONSTRAINT t_page_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_page_fk2 FOREIGN KEY (template, template_type) REFERENCES t_template (name, type)
);
--
CREATE TABLE IF NOT EXISTS t_page_part (
  id            INTEGER      NOT NULL,
  name          VARCHAR(60)  NOT NULL DEFAULT '',
  creation_date   TIMESTAMP  NOT NULL DEFAULT now(),
  change_date   TIMESTAMP    NOT NULL DEFAULT now(),
  template_type VARCHAR(20)  NOT NULL DEFAULT 'PART',
  template      VARCHAR(255) NOT NULL,
  content       TEXT         NOT NULL DEFAULT '',
  CONSTRAINT t_page_part_pk  PRIMARY KEY (id),
  CONSTRAINT t_page_part_fk1 FOREIGN KEY (template, template_type) REFERENCES t_template (name, type)
);
--
CREATE TABLE IF NOT EXISTS t_page_part2page (
  part_id     INTEGER     NOT NULL,
  page_id     INTEGER     NULL,
  section     VARCHAR(60) NOT NULL,
  ranking     INTEGER     NOT NULL DEFAULT 0,
  CONSTRAINT t_page_part2page_pk PRIMARY KEY (part_id, page_id, section, ranking),
  CONSTRAINT t_page_part2page_fk1 FOREIGN KEY (part_id) REFERENCES t_page_part (id) ON DELETE CASCADE,
  CONSTRAINT t_page_part2page_fk2 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_node_usage (
  linked_node_id INTEGER NOT NULL,
  page_id        INTEGER NOT NULL,
  CONSTRAINT t_page_usage_pk PRIMARY KEY (linked_node_id, page_id),
  CONSTRAINT t_page_usage_fk1 FOREIGN KEY (linked_node_id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_page_usage_fk2 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE
);
--
--collaboration
CREATE TABLE IF NOT EXISTS t_document(
  id INTEGER NOT NULL,
  change_date TIMESTAMP NOT NULL DEFAULT now(),
  part_id INTEGER NOT NULL,
  owner_id INTEGER NULL,
  owner_name VARCHAR(255) NOT NULL,
  author_id INTEGER NULL,
  author_name VARCHAR(255) NOT NULL,
  checkout_id INTEGER NULL,
  checkout_name VARCHAR(255) NOT NULL DEFAULT '',
  file_name VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL DEFAULT '',
  notes TEXT NOT NULL DEFAULT '',
  content_type varchar(255),
  file_size INTEGER NOT NULL DEFAULT 0,
  bytes BYTEA NOT NULL,
  CONSTRAINT t_document_pk PRIMARY KEY (id),
  CONSTRAINT t_document_fk1 FOREIGN KEY (part_id) REFERENCES t_page_part (id) ON DELETE CASCADE,
  CONSTRAINT t_document_fk2 FOREIGN KEY (owner_id) REFERENCES t_user (id) ON DELETE SET NULL,
  CONSTRAINT t_document_fk3 FOREIGN KEY (author_id) REFERENCES t_user (id) ON DELETE SET NULL,
  CONSTRAINT t_document_fk4 FOREIGN KEY (checkout_id) REFERENCES t_user (id) ON DELETE SET NULL
);
--
CREATE TABLE IF NOT EXISTS t_blogentry(
  id INTEGER NOT NULL,
  change_date TIMESTAMP NOT NULL DEFAULT now(),
  part_id INTEGER NOT NULL,
  author_id INTEGER NULL,
  author_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL DEFAULT '',
  entry TEXT NOT NULL DEFAULT '',
  constraint t_logentry_pk PRIMARY KEY (id),
  CONSTRAINT t_blogentry_fk1 FOREIGN KEY (part_id) REFERENCES t_page_part (id) ON DELETE CASCADE,
  CONSTRAINT t_blogentry_fk2 FOREIGN KEY (author_id) REFERENCES t_user (id) ON DELETE SET NULL
);
--
CREATE TABLE IF NOT EXISTS t_calendarentry(
  id INTEGER NOT NULL,
  change_date TIMESTAMP NOT NULL DEFAULT now(),
  part_id INTEGER NOT NULL,
  author_id INTEGER NULL,
  author_name VARCHAR(255) NOT NULL,
  start_time  TIMESTAMP NOT NULL,
  end_time  TIMESTAMP NULL,
  title VARCHAR(255) NOT NULL DEFAULT '',
  entry TEXT NOT NULL DEFAULT '',
  constraint t_calendarentry_pk PRIMARY KEY (id),
  CONSTRAINT t_calendarentry_fk1 FOREIGN KEY (part_id) REFERENCES t_page_part (id) ON DELETE CASCADE,
  CONSTRAINT t_calendarentry_fk2 FOREIGN KEY (author_id) REFERENCES t_user (id) ON DELETE SET NULL
);
-- inserts
-- std locale
INSERT INTO t_locale (locale, home)
VALUES ('en', '/admin.srv?act=openAdministration');
-- virtual all users group
INSERT INTO t_group (id, name)
VALUES (0, 'All Users');
-- global administrators
INSERT INTO t_group (id, name)
VALUES (1, 'Global Administrators');
--
INSERT INTO t_system_right (name, group_id, value)
VALUES ('APPLICATION', 1, 'EDIT');
--
INSERT INTO t_system_right (name, group_id, value)
VALUES ('USER', 1, 'EDIT');
--
INSERT INTO t_system_right (name, group_id, value)
VALUES ('CONTENT', 1, 'EDIT');
-- global approvers
INSERT INTO t_group (id, name)
VALUES (2, 'Global Approvers');
--
INSERT INTO t_system_right (name, group_id, value)
VALUES ('CONTENT', 2, 'APPROVE');
--global editors
INSERT INTO t_group (id, name)
VALUES (3, 'Global Editors');
--
INSERT INTO t_system_right (name, group_id, value)
VALUES ('CONTENT', 3, 'EDIT');
--global readers
INSERT INTO t_group (id, name)
VALUES (4, 'Global Readers');
--
INSERT INTO t_system_right (name, group_id, value)
VALUES ('CONTENT', 4, 'READ');
-- root user
INSERT INTO t_user (id, first_name, last_name, email, login, pwd, pkey, approval_code, approved)
VALUES (1, 'Sys', 'Admin', 'root@localhost', 'root', '', '', '', TRUE);
-- root is Global Administrator
INSERT INTO t_user2group (user_id, group_id, relation) VALUES (1, 1, 'RIGHTS');
-- configuration
INSERT INTO t_configuration (defaultLocale, mailHost, mailPort, mailConnectionType, mailUser, mailPassword, mailSender, timerInterval)
VALUES ('en', 'localhost', 25, 'plain', '', '', 'me@myhost.tld', 30);
--
INSERT INTO t_timer_task (name, display_name, execution_interval, day, hour, minute, active, note_execution)
VALUES ('heartbeat', 'Heartbeat Task', 'CONTINOUS', 0, 0, 5, FALSE, FALSE);
--
INSERT INTO t_timer_task (name, display_name, execution_interval, day, hour, minute, active, note_execution)
VALUES ('searchindex', 'Search Index Task', 'CONTINOUS', 0, 1, 0, FALSE, FALSE);
-- virtual all ids node
INSERT INTO t_treenode (id, parent_id, ranking, name, display_name, description, owner_id, author_name, in_navigation, anonymous, inherits_rights)
VALUES (0, NULL, 0, '', 'ALL-NODE', 'Virtual Node for all IDs', 1, 'System', FALSE, FALSE, FALSE);
--
INSERT INTO t_template (name, type, display_name, code)
VALUES ('pageMaster', 'MASTER', 'Default Page Master',
'<!DOCTYPE html>
<html>
  <head>
      <meta charset="utf-8"/>
  </head>
  <body>
  </body>
</html>');
-- root node
INSERT INTO t_treenode (id, parent_id, ranking, name, display_name, description, owner_id, author_name, in_navigation, anonymous, inherits_rights)
VALUES (1, NULL, 0, '', 'Root', 'Root node as parent for all languages branches', 1, 'System', FALSE, TRUE, FALSE);
--
INSERT INTO t_site (id, inherits_master, template)
VALUES (1, FALSE, 'pageMaster');
--
--
-- conveniance methods
--
-- get next Id
--
CREATE OR REPLACE FUNCTION getNextId () RETURNS INTEGER AS $$
DECLARE
  nextId INTEGER;
    cur CURSOR FOR (select id from T_ID);
BEGIN
  open cur;
  fetch cur into nextId;
  close cur;
  nextId:=nextId+1;
  UPDATE T_ID SET id=nextId;
  RETURN nextId;
END;
$$ LANGUAGE plpgsql;
--
CREATE OR REPLACE FUNCTION setNextId (_id INTEGER) RETURNS INTEGER AS $$
BEGIN
  UPDATE T_ID SET id=_id;
  RETURN _id;
END;
$$ LANGUAGE plpgsql;
--
--
-- conveniance methods
CREATE OR REPLACE FUNCTION addPage (parentId INTEGER, pageId INTEGER, rank INTEGER, nodeName VARCHAR(100), displayName VARCHAR(100), pageTemplate VARCHAR(255)) RETURNS INTEGER AS $$
BEGIN
  INSERT INTO t_treenode (id, parent_id, ranking, name, display_name, description, owner_id, author_name, in_navigation, anonymous, inherits_rights)
  VALUES (pageId, parentId, rank, nodeName, displayName, '', 1, 'System', TRUE, TRUE, TRUE);
  INSERT INTO t_page (id, template)
  VALUES (pageId, pageTemplate);
  RETURN pageId;
END;
$$ LANGUAGE plpgsql;
--
--
CREATE OR REPLACE FUNCTION addSite (parentId INTEGER, siteId INTEGER, rank INTEGER, nodeName VARCHAR(100), displayName VARCHAR(100), pageTemplate VARCHAR(255)) RETURNS INTEGER AS $$
DECLARE
  pageId INTEGER;
BEGIN
  pageId := GETNEXTID();
  INSERT INTO t_treenode (id, parent_id, ranking, name, display_name, description, owner_id, author_name, in_navigation, anonymous, inherits_rights)
  VALUES (siteId, parentId, rank, nodeName, displayName, '', 1, 'System', TRUE, TRUE, TRUE);
  INSERT INTO t_site (id, inherits_master)
  VALUES (siteId, TRUE);
  INSERT INTO t_treenode (id, parent_id, ranking, name, display_name, description, owner_id, author_name, in_navigation, anonymous, inherits_rights)
  VALUES (pageId, siteId, 0, 'default', displayName, '', 1, 'System', TRUE, TRUE, TRUE);
  INSERT INTO t_page (id, template)
  VALUES (pageId, pageTemplate);
  RETURN siteId;
END;
$$ LANGUAGE plpgsql;
--
