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
INSERT INTO t_locale (locale, home) VALUES ('en', '/admin.srv?act=openAdministration');
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
  first_name         VARCHAR(100) NOT NULL DEFAULT '',
  middle_name        VARCHAR(100) NOT NULL DEFAULT '',
  last_name          VARCHAR(100) NOT NULL,
  street             VARCHAR(100) NOT NULL DEFAULT '',
  zipCode            VARCHAR(16)  NOT NULL DEFAULT '',
  city               VARCHAR(50)  NOT NULL DEFAULT '',
  country            VARCHAR(50)  NOT NULL DEFAULT '',
  locale             VARCHAR(10)  NOT NULL DEFAULT 'en',
  email              VARCHAR(100) NOT NULL DEFAULT '',
  phone              VARCHAR(50)  NOT NULL DEFAULT '',
  mobile             VARCHAR(50)  NOT NULL DEFAULT '',
  notes              VARCHAR(500) NOT NULL DEFAULT '',
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
  config_key   VARCHAR(30)  NOT NULL,
  config_value VARCHAR(255) NOT NULL,
  CONSTRAINT t_configuration_pk PRIMARY KEY (config_key)
);
--
CREATE TABLE t_cluster (
  ipaddress   VARCHAR(30) NOT NULL,
  port        INTEGER     NOT NULL DEFAULT 0,
  active      BOOLEAN     NOT NULL DEFAULT FALSE,
  change_date TIMESTAMP   NOT NULL DEFAULT now(),
  CONSTRAINT t_cluster_pk PRIMARY KEY (ipaddress)
);
--
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
--
CREATE TABLE t_template (
  name         VARCHAR(60)  NOT NULL,
  type         VARCHAR(20)  NOT NULL,
  change_date  TIMESTAMP    NOT NULL DEFAULT now(),
  display_name VARCHAR(100) NOT NULL DEFAULT '',
  description  VARCHAR(255) NOT NULL DEFAULT '',
  usage        VARCHAR(100) NOT NULL DEFAULT '',
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
  id  INTEGER NOT NULL,
  user_id INTEGER NOT NULL,
  relation VARCHAR(20) NOT NULL,
  CONSTRAINT t_treenode2user_pk PRIMARY KEY (id, user_id),
  CONSTRAINT t_treenode2user_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_treenode2user_fk2 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_treenode2group (
  id  INTEGER NOT NULL,
  group_id INTEGER NOT NULL,
  relation VARCHAR(20) NOT NULL,
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
-- resource ------------------
--
CREATE TABLE IF NOT EXISTS t_resource (
  id                INTEGER      NOT NULL,
  keywords          VARCHAR(500) NOT NULL DEFAULT '',
  published_version INTEGER      NOT NULL DEFAULT 0,
  draft_version     INTEGER      NOT NULL DEFAULT 0,
  CONSTRAINT t_resource_pk PRIMARY KEY (id),
  CONSTRAINT t_resource_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE
);
--
-- file ------------------
--
CREATE TABLE IF NOT EXISTS t_file (
  id         INTEGER     NOT NULL,
  media_type VARCHAR(60) NOT NULL DEFAULT '',
  CONSTRAINT t_file_pk PRIMARY KEY (id),
  CONSTRAINT t_file_fk1 FOREIGN KEY (id) REFERENCES t_resource (id) ON DELETE CASCADE
);
--
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
  search_text          TEXT         NOT NULL DEFAULT '',
  CONSTRAINT t_file_content_pk PRIMARY KEY (id, version),
  CONSTRAINT t_file_content_fk1 FOREIGN KEY (id) REFERENCES t_file (id) ON DELETE CASCADE
);
--
-- page ------------------
--
CREATE TABLE IF NOT EXISTS t_page (
  id            INTEGER      NOT NULL,
  template_type VARCHAR(20)  NOT NULL DEFAULT 'PAGE',
  template      VARCHAR(255) NOT NULL,
  CONSTRAINT t_page_pk PRIMARY KEY (id),
  CONSTRAINT t_page_fk1 FOREIGN KEY (id) REFERENCES t_resource (id) ON DELETE CASCADE,
  CONSTRAINT t_page_fk2 FOREIGN KEY (template, template_type) REFERENCES t_template (name, type)
);
--
CREATE TABLE IF NOT EXISTS t_page_content (
  id          INTEGER      NOT NULL,
  version     INTEGER      NOT NULL DEFAULT 1,
  change_date TIMESTAMP    NOT NULL DEFAULT now(),
  published   BOOLEAN      NOT NULL DEFAULT FALSE,
  author_name VARCHAR(255) NOT NULL,
  search_text TEXT         NOT NULL DEFAULT '',
  CONSTRAINT t_page_content_pk PRIMARY KEY (id, version),
  CONSTRAINT t_page_content_fk1 FOREIGN KEY (id) REFERENCES t_page (id) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_page_part (
  id            INTEGER      NOT NULL,
  version       INTEGER      NOT NULL DEFAULT 1,
  page_id       INTEGER      NULL,
  change_date   TIMESTAMP    NOT NULL DEFAULT now(),
  section       VARCHAR(60)  NOT NULL,
  ranking       INTEGER      NOT NULL DEFAULT 0,
  template_type VARCHAR(20)  NOT NULL DEFAULT 'PART',
  template      VARCHAR(255) NOT NULL,
  content       TEXT         NOT NULL DEFAULT '',
  CONSTRAINT t_page_part_pk PRIMARY KEY (id, version),
  CONSTRAINT t_page_part_fk1 FOREIGN KEY (page_id, version) REFERENCES t_page_content (id, version) ON DELETE CASCADE,
  CONSTRAINT t_page_part_fk2 FOREIGN KEY (template, template_type) REFERENCES t_template (name, type)
);
--
CREATE TABLE IF NOT EXISTS t_shared_page_part (
  id            INTEGER      NOT NULL,
  change_date   TIMESTAMP    NOT NULL DEFAULT now(),
  share_name    VARCHAR(60)  NOT NULL DEFAULT '',
  template_type VARCHAR(20)  NOT NULL DEFAULT 'PART',
  template      VARCHAR(255) NOT NULL,
  content       TEXT         NULL,
  CONSTRAINT t_shared_page_part_pk PRIMARY KEY (id),
  CONSTRAINT t_shared_page_part_fk1 FOREIGN KEY (template, template_type) REFERENCES t_template (name, type)
);
--
CREATE TABLE IF NOT EXISTS t_shared_part_usage (
  part_id     INTEGER     NOT NULL,
  page_id     INTEGER     NULL,
  version     INTEGER     NOT NULL DEFAULT 1,
  section     VARCHAR(60) NOT NULL,
  ranking     INTEGER     NOT NULL DEFAULT 0,
  change_date TIMESTAMP   NOT NULL DEFAULT now(),
  CONSTRAINT t_shared_part_usage_pk PRIMARY KEY (part_id, page_id, version, section, ranking),
  CONSTRAINT t_shared_part_usage_fk1 FOREIGN KEY (part_id) REFERENCES t_shared_page_part (id) ON DELETE CASCADE,
  CONSTRAINT t_shared_part_usage_fk2 FOREIGN KEY (page_id, version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_node_usage (
  linked_node_id INTEGER NOT NULL,
  page_id        INTEGER NOT NULL,
  page_version   INTEGER NOT NULL,
  CONSTRAINT t_page_usage_pk PRIMARY KEY (linked_node_id, page_id, page_version),
  CONSTRAINT t_page_usage_fk1 FOREIGN KEY (linked_node_id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_page_usage_fk2 FOREIGN KEY (page_id, page_version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_shared_node_usage (
  linked_node_id INTEGER NOT NULL,
  part_id        INTEGER NOT NULL,
  CONSTRAINT t_shared_page_usage_pk PRIMARY KEY (linked_node_id, part_id),
  CONSTRAINT t_shared_page_usage_fk1 FOREIGN KEY (linked_node_id) REFERENCES t_treenode (id) ON DELETE CASCADE,
  CONSTRAINT t_shared_page_usage_fk2 FOREIGN KEY (part_id) REFERENCES t_shared_page_part (id) ON DELETE CASCADE
);
--
CREATE TABLE IF NOT EXISTS t_teamblog_entry (
  id             INTEGER      NOT NULL,
  change_date    TIMESTAMP    NOT NULL DEFAULT now(),
  pagepart_id    INTEGER      NOT NULL,
  title          VARCHAR(255) NOT NULL DEFAULT '',
  author_id      INTEGER      NOT NULL DEFAULT 1,
  author_name    VARCHAR(255) NOT NULL,
  email          VARCHAR(255) NOT NULL DEFAULT '',
  entry          TEXT         NOT NULL DEFAULT '',
  search_content TEXT         NOT NULL DEFAULT '',
  CONSTRAINT t_teamblog_entry_pk PRIMARY KEY (id),
  CONSTRAINT t_teamblog_entry_fk1 FOREIGN KEY (author_id) REFERENCES t_user (id) ON DELETE SET DEFAULT
);
-- inserts
-- std locale
INSERT INTO t_locale (locale, home)
VALUES ('de', '/admin.srv?act=openAdministration');
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
-- sysadmin user
INSERT INTO t_user (id, first_name, last_name, email, login, pwd, pkey, approval_code, approved)
VALUES (1, 'Sys', 'Admin', 'sysadmin@localhost', 'sysadmin', '', '', '', TRUE);
-- sysadmin is Global Administrator
INSERT INTO t_user2group (user_id, group_id, relation) VALUES (1, 1, 'RIGHTS');
-- configuration
INSERT INTO t_configuration (config_key, config_value)
VALUES ('defaultLocale', 'en');
--
INSERT INTO t_configuration (config_key, config_value)
VALUES ('mailHost', 'localhost');
--
INSERT INTO t_configuration (config_key, config_value)
VALUES ('mailSender', 'me@myhost.tld');
--
INSERT INTO t_configuration (config_key, config_value)
VALUES ('timerInterval', '30');
--
INSERT INTO t_configuration (config_key, config_value)
VALUES ('clusterPort', '2555');
--
INSERT INTO t_configuration (config_key, config_value)
VALUES ('clusterTimeout', '60');
--
INSERT INTO t_configuration (config_key, config_value)
VALUES ('maxClusterTimeouts', '5');
--
INSERT INTO t_configuration (config_key, config_value)
VALUES ('maxVersions', '5');
--
INSERT INTO t_timer_task (id, name, class_name, interval_type, execution_minute, active, note_execution)
VALUES (500, 'Heartbeat Task', 'de.bandika.timer.HeartbeatTask', 0, 5, TRUE, FALSE);
--
INSERT INTO t_timer_task (id, name, class_name, interval_type, execution_minute, active, note_execution)
VALUES (501, 'Search Index Task', 'de.bandika.search.SearchIndexTask', 0, 15, FALSE, FALSE);
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
CREATE OR REPLACE FUNCTION addPage (parentId INTEGER,pageId INTEGER, rank INTEGER, nodeName VARCHAR(100), displayName VARCHAR(100), pageTemplate VARCHAR(255)) RETURNS INTEGER AS $$
BEGIN
  INSERT INTO t_treenode (id, parent_id, ranking, name, display_name, description, owner_id, author_name, in_navigation, anonymous, inherits_rights)
  VALUES (pageId, parentId, rank, nodeName, displayName, '', 1, 'System', TRUE, TRUE, TRUE);
  INSERT INTO t_resource (id, published_version, draft_version)
  VALUES (pageId, 1, 0);
  INSERT INTO t_page (id, template)
  VALUES (pageId, pageTemplate);
  INSERT INTO t_page_content (id, version, author_name)
  VALUES (pageId, 1, 'Sytem');
  RETURN 1;
END;
$$ LANGUAGE plpgsql;
--
--
CREATE OR REPLACE FUNCTION addSite (parentId INTEGER, siteId INTEGER, rank INTEGER, nodeName VARCHAR(100), displayName VARCHAR(100), pageTemplate VARCHAR(255)) RETURNS INTEGER AS $$
DECLARE
  pageId INTEGER = siteId+1;
BEGIN
  INSERT INTO t_treenode (id, parent_id, ranking, name, display_name, description, owner_id, author_name, in_navigation, anonymous, inherits_rights)
  VALUES (siteId, parentId, rank, nodeName, displayName, '', 1, 'System', TRUE, TRUE, TRUE);
  INSERT INTO t_site (id, inherits_master)
  VALUES (siteId, TRUE);
  INSERT INTO t_treenode (id, parent_id, ranking, name, display_name, description, owner_id, author_name, in_navigation, anonymous, inherits_rights)
  VALUES (pageId, siteId, 0, 'default', displayName, '', 1, 'System', TRUE, TRUE, TRUE);
  INSERT INTO t_resource (id, published_version, draft_version)
  VALUES (pageId, 1, 0);
  INSERT INTO t_page (id, template)
  VALUES (pageId, pageTemplate);
  INSERT INTO t_page_content (id, version, author_name)
  VALUES (pageId, 1, 'System');
  RETURN 1;
END;
$$ LANGUAGE plpgsql;
--

