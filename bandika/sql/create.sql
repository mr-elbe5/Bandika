--mod_sql
CREATE TABLE t_id (
  id INTEGER NOT NULL
);

--mod_portal
CREATE TABLE t_locale (
  locale      VARCHAR(10) NOT NULL,
  CONSTRAINT t_locale_pk PRIMARY KEY (locale)
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
  pwd                VARCHAR(200) NOT NULL,
  pkey               VARCHAR(50)  NOT NULL,
  approval_code      VARCHAR(50)  NOT NULL DEFAULT '',
  approved           BOOLEAN      NOT NULL DEFAULT FALSE,
  failed_login_count INTEGER      NOT NULL DEFAULT 0,
  locked             BOOLEAN      NOT NULL DEFAULT FALSE,
  deleted            BOOLEAN      NOT NULL DEFAULT FALSE,
  CONSTRAINT t_user_pk PRIMARY KEY (id)
);

CREATE TABLE t_user2locale (
  user_id  INTEGER NOT NULL,
  locale VARCHAR(10) NOT NULL,
  CONSTRAINT t_user2locale_pk PRIMARY KEY (user_id, locale),
  CONSTRAINT t_user2locale_fk1 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE,
  CONSTRAINT t_user2locale_fk2 FOREIGN KEY (locale) REFERENCES t_locale (locale)
);

CREATE TABLE t_user2group (
  user_id  INTEGER NOT NULL,
  group_id INTEGER NOT NULL,
  CONSTRAINT t_user2group_pk PRIMARY KEY (user_id, group_id),
  CONSTRAINT t_user2group_fk1 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE,
  CONSTRAINT t_user2group_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);

CREATE TABLE t_general_right (
  group_id INTEGER NOT NULL,
  rights   INTEGER NOT NULL,
  CONSTRAINT t_general_right_pk PRIMARY KEY (group_id),
  CONSTRAINT t_general_right_fk1 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);

CREATE TABLE t_configuration (
  config_key   VARCHAR(30)  NOT NULL,
  config_value VARCHAR(255) NOT NULL,
  CONSTRAINT t_configuration_pk PRIMARY KEY (config_key)
);


CREATE TABLE t_timer_task (
  name             VARCHAR(60)  NOT NULL,
  class_name       VARCHAR(255) NOT NULL,
  interval_type    INTEGER      NOT NULL,
  execution_day    INTEGER      NOT NULL DEFAULT 0,
  execution_hour   INTEGER      NOT NULL DEFAULT 0,
  execution_minute INTEGER      NOT NULL DEFAULT 0,
  execution_second INTEGER      NOT NULL DEFAULT 0,
  note_execution   BOOLEAN      NOT NULL DEFAULT FALSE,
  last_execution   TIMESTAMP    NULL,
  active           BOOLEAN      NOT NULL DEFAULT TRUE,
  CONSTRAINT t_timer_task_pk PRIMARY KEY (name)
);

CREATE TABLE t_master_template (
  name        VARCHAR(60)  NOT NULL,
  change_date TIMESTAMP    NOT NULL DEFAULT now(),
  description VARCHAR(255) NOT NULL DEFAULT '',
  code        TEXT         NULL,
  CONSTRAINT t_master_template_pk PRIMARY KEY (name)
);

CREATE TABLE t_layout_template (
  name        VARCHAR(60)  NOT NULL,
  change_date TIMESTAMP    NOT NULL DEFAULT now(),
  description VARCHAR(255) NOT NULL DEFAULT '',
  class_name  VARCHAR(255) NOT NULL DEFAULT '',
  code        TEXT         NULL,
  CONSTRAINT t_layout_template_pk PRIMARY KEY (name)
);

CREATE TABLE t_part_template (
  name        VARCHAR(60)  NOT NULL,
  change_date TIMESTAMP    NOT NULL DEFAULT now(),
  description VARCHAR(255) NOT NULL DEFAULT '',
  class_name  VARCHAR(255) NOT NULL DEFAULT '',
  area_types  VARCHAR(255) NOT NULL DEFAULT '',
  code        TEXT         NULL,
  CONSTRAINT t_part_template_pk PRIMARY KEY (name)
);

CREATE TABLE t_page (
  id                    INTEGER      NOT NULL,
  change_date           TIMESTAMP    NOT NULL DEFAULT now(),
  parent_id             INTEGER      NULL,
  ranking               INTEGER      NOT NULL DEFAULT 0,
  name                  VARCHAR(100) NOT NULL,
  path                  VARCHAR(100) NOT NULL DEFAULT '',
  description           VARCHAR(200) NOT NULL DEFAULT '',
  keywords              VARCHAR(500) NOT NULL DEFAULT '',
  master_template       VARCHAR(60)  NOT NULL DEFAULT null,
  layout_template       VARCHAR(60)  NOT NULL DEFAULT null,
  restricted            BOOLEAN      NOT NULL DEFAULT FALSE,
  inherits_rights       BOOLEAN      NOT NULL DEFAULT TRUE,
  visible               BOOLEAN      NOT NULL DEFAULT TRUE,
  author_name           VARCHAR(255) NOT NULL,
  CONSTRAINT t_page_pk PRIMARY KEY (id),
  CONSTRAINT t_page_fk1 FOREIGN KEY (parent_id) REFERENCES t_page (id) ON DELETE CASCADE,
  CONSTRAINT t_page_fk2 FOREIGN KEY (master_template) REFERENCES t_master_template (name) ON DELETE CASCADE,
  CONSTRAINT t_page_fk3 FOREIGN KEY (layout_template) REFERENCES t_layout_template (name) ON DELETE CASCADE,
  CONSTRAINT t_page_un4 UNIQUE (id, parent_id, name)
);

CREATE TABLE t_page_locale (
  id                    INTEGER     NOT NULL,
  locale                VARCHAR(10) NOT NULL,
  CONSTRAINT t_page_locale_pk PRIMARY KEY (id,locale),
  CONSTRAINT t_page_locale_fk1 FOREIGN KEY (id) REFERENCES t_page (id) ON DELETE CASCADE,
  CONSTRAINT t_page_locale_fk2 FOREIGN KEY (locale) REFERENCES t_locale (locale) ON DELETE CASCADE
);

CREATE TABLE t_page_content (
  id                    INTEGER      NOT NULL,
  version               INTEGER      NOT NULL DEFAULT 1,
  change_date           TIMESTAMP    NOT NULL DEFAULT now(),
  published             BOOLEAN      NOT NULL DEFAULT FALSE,
  author_name           VARCHAR(255) NOT NULL,
  CONSTRAINT t_page_content_pk PRIMARY KEY (id, version),
  CONSTRAINT t_page_content_fk1 FOREIGN KEY (id) REFERENCES t_page (id) ON DELETE CASCADE
);

CREATE TABLE t_page_current (
  id                INTEGER   NOT NULL,
  change_date       TIMESTAMP NOT NULL DEFAULT now(),
  published_version INTEGER   NULL,
  draft_version     INTEGER   NULL,
  CONSTRAINT t_page_current_pk PRIMARY KEY (id),
  CONSTRAINT t_page_current_fk1 FOREIGN KEY (id, published_version) REFERENCES t_page_content (id, version) ON DELETE CASCADE,
  CONSTRAINT t_page_current_fk2 FOREIGN KEY (id, draft_version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);

CREATE TABLE t_page_part (
  id                 INTEGER     NOT NULL,
  version            INTEGER     NOT NULL DEFAULT 1,
  page_id            INTEGER     NULL,
  change_date        TIMESTAMP   NOT NULL DEFAULT now(),
  name               VARCHAR(60) NOT NULL DEFAULT '',
  area               VARCHAR(60) NOT NULL,
  ranking            INTEGER     NOT NULL DEFAULT 0,
  part_template      VARCHAR(60) NOT NULL,
  content            TEXT        NOT NULL DEFAULT '',
  CONSTRAINT t_page_part_pk PRIMARY KEY (id, version),
  CONSTRAINT t_page_part_fk1 FOREIGN KEY (page_id, version) REFERENCES t_page_content (id, version) ON DELETE CASCADE,
  CONSTRAINT t_page_part_fk2 FOREIGN KEY (part_template) REFERENCES t_part_template (name) ON DELETE CASCADE
);

CREATE TABLE t_shared_page_part (
  id            INTEGER     NOT NULL,
  change_date   TIMESTAMP   NOT NULL DEFAULT now(),
  name          VARCHAR(60) NOT NULL DEFAULT '',
  part_template VARCHAR(60) NOT NULL,
  content       TEXT        NULL,
  CONSTRAINT t_shared_page_part_pk PRIMARY KEY (id),
  CONSTRAINT t_shared_page_part_fk1 FOREIGN KEY (part_template) REFERENCES t_part_template (name) ON DELETE CASCADE
);

CREATE TABLE t_shared_part_usage (
  part_id       INTEGER     NOT NULL,
  page_id       INTEGER     NULL,
  version       INTEGER     NOT NULL DEFAULT 1,
  area          VARCHAR(60) NOT NULL,
  ranking       INTEGER     NOT NULL DEFAULT 0,
  change_date   TIMESTAMP   NOT NULL DEFAULT now(),
  CONSTRAINT t_shared_part_usage_pk PRIMARY KEY (part_id, page_id, version, area, ranking),
  CONSTRAINT t_shared_part_usage_fk1 FOREIGN KEY (part_id) REFERENCES t_shared_page_part (id) ON DELETE CASCADE,
  CONSTRAINT t_shared_part_usage_fk2 FOREIGN KEY (page_id, version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);

CREATE TABLE t_page_usage (
  linked_page_id INTEGER NOT NULL,
  page_id        INTEGER NOT NULL,
  page_version   INTEGER NOT NULL,
  CONSTRAINT t_page_usage_pk PRIMARY KEY (linked_page_id, page_id, page_version),
  CONSTRAINT t_page_usage_fk1 FOREIGN KEY (linked_page_id) REFERENCES t_page (id) ON DELETE CASCADE,
  CONSTRAINT t_page_usage_fk2 FOREIGN KEY (page_id, page_version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);

CREATE TABLE t_shared_page_usage (
  linked_page_id  INTEGER NOT NULL,
  part_id         INTEGER NOT NULL,
  CONSTRAINT t_shared_page_usage_pk PRIMARY KEY (linked_page_id, part_id),
  CONSTRAINT t_shared_page_usage_fk1 FOREIGN KEY (linked_page_id) REFERENCES t_page (id) ON DELETE CASCADE,
  CONSTRAINT t_shared_page_usage_fk2 FOREIGN KEY (part_id) REFERENCES t_shared_page_part (id) ON DELETE CASCADE
);

CREATE TABLE t_page_right (
  page_id  INTEGER NOT NULL,
  group_id INTEGER NOT NULL,
  rights   INTEGER NOT NULL,
  CONSTRAINT t_page_right_pk PRIMARY KEY (page_id, group_id),
  CONSTRAINT t_page_right_fk1 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE,
  CONSTRAINT t_page_right_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);

CREATE TABLE t_general_page_right (
  group_id INTEGER NOT NULL,
  rights   INTEGER NOT NULL,
  CONSTRAINT t_general_page_right_pk PRIMARY KEY (group_id),
  CONSTRAINT t_general_page_right_fk1 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE
);

CREATE TABLE t_document (
  id             INTEGER      NOT NULL,
  change_date    TIMESTAMP    NOT NULL DEFAULT now(),
  file_name      VARCHAR(100) NOT NULL,
  content_type   VARCHAR(255) NOT NULL DEFAULT '',
  file_size      INTEGER      NOT NULL DEFAULT 0,
  author_name    VARCHAR(255) NOT NULL,
  page_id        INTEGER      NULL,
--postgres
  bytes         BYTEA        NOT NULL,
-- mysql
-- bytes LONGBLOB NOT NULL,
  CONSTRAINT t_document_pk PRIMARY KEY (id),
  CONSTRAINT t_document_fk1 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE
);

CREATE TABLE t_document_usage (
  document_id  INTEGER NOT NULL,
  page_id      INTEGER NOT NULL,
  page_version INTEGER NOT NULL,
  CONSTRAINT t_document_usage_pk PRIMARY KEY (document_id, page_id, page_version),
  CONSTRAINT t_document_usage_fk1 FOREIGN KEY (document_id) REFERENCES t_document (id) ON DELETE CASCADE,
  CONSTRAINT t_document_usage_fk2 FOREIGN KEY (page_id, page_version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);

CREATE TABLE t_shared_document_usage (
  document_id     INTEGER NOT NULL,
  part_id     INTEGER NOT NULL,
  CONSTRAINT t_shared_document_usage_pk PRIMARY KEY (document_id, part_id),
  CONSTRAINT t_shared_document_usage_fk1 FOREIGN KEY (document_id) REFERENCES t_document (id) ON DELETE CASCADE,
  CONSTRAINT t_shared_document_usage_fk2 FOREIGN KEY (part_id) REFERENCES t_shared_page_part (id) ON DELETE CASCADE
);


CREATE TABLE t_image (
  id              INTEGER      NOT NULL,
  change_date     TIMESTAMP    NOT NULL DEFAULT now(),
  file_name       VARCHAR(100) NOT NULL,
  content_type    VARCHAR(255) NOT NULL DEFAULT '',
  file_size       INTEGER      NOT NULL DEFAULT 0,
  width           INTEGER      NOT NULL DEFAULT 0,
  height          INTEGER      NOT NULL DEFAULT 0,
  author_name     VARCHAR(255) NOT NULL,
  page_id         INTEGER      NULL,
--postgres
  bytes           BYTEA        NOT NULL,
  thumbnail_bytes BYTEA        NOT NULL,
-- mysql
--  bytes LONGBLOB NOT NULL,
--  thumbnail_bytes LONGBLOB NOT NULL,
  CONSTRAINT t_image_pk PRIMARY KEY (id),
  CONSTRAINT t_image_fk1 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE
);

CREATE TABLE t_image_usage (
  image_id      INTEGER NOT NULL,
  page_id       INTEGER NOT NULL,
  page_version  INTEGER NOT NULL,
  CONSTRAINT t_image_usage_pk PRIMARY KEY (image_id, page_id, page_version),
  CONSTRAINT t_image_usage_fk1 FOREIGN KEY (image_id) REFERENCES t_image (id) ON DELETE CASCADE,
  CONSTRAINT t_image_usage_fk2 FOREIGN KEY (page_id, page_version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);

CREATE TABLE t_shared_image_usage (
  image_id     INTEGER NOT NULL,
  part_id     INTEGER NOT NULL,
  CONSTRAINT t_shared_image_usage_pk PRIMARY KEY (image_id, part_id),
  CONSTRAINT t_shared_image_usage_fk1 FOREIGN KEY (image_id) REFERENCES t_image (id) ON DELETE CASCADE,
  CONSTRAINT t_shared_image_usage_fk2 FOREIGN KEY (part_id) REFERENCES t_shared_page_part (id) ON DELETE CASCADE
);

--mod_cluster
CREATE TABLE t_cluster (
  ipaddress   VARCHAR(30) NOT NULL,
  port        INTEGER     NOT NULL DEFAULT 0,
  active      BOOLEAN     NOT NULL DEFAULT FALSE,
  change_date TIMESTAMP   NOT NULL DEFAULT now(),
  CONSTRAINT t_cluster_pk PRIMARY KEY (ipaddress)
);

--mod_team
create table t_teamfile(
  id integer not null,
  change_date timestamp not null default now(),
  teampart_id integer not  null,
  owner_id integer null,
  owner_name varchar(255) not null,
  checkout_id integer null,
  checkout_name varchar(255) not null default '',
  constraint t_teamfile_pk primary key (id)
);
create table t_teamfile_version(
  id integer not null,
  version integer not null default 1,
  change_date timestamp not null default now(),
  file_name varchar(100) not null,
  name varchar(255) not null default '',
  description text not null default '',
  content_type varchar(255),
  size integer not null default 0,
  author_id integer null,
  author_name varchar(255) not null,
  bytes bytea not null,
  constraint t_teamfile_version_pk primary key (id,version)
);
create table t_teamfile_current(
  id integer not null,
  change_date timestamp not null default now(),
  current_version integer null,
  checkout_version integer null,
  constraint t_teamfile_current_pk primary key (id),
  constraint t_teamfile_current_fk1 foreign key (id,current_version) references t_teamfile_version(id, version) on delete cascade,
  constraint t_teamfile_current_fk2 foreign key (id,checkout_version) references t_teamfile_version(id, version) on delete cascade
);
create table t_teamblogentry(
  id integer not null,
  change_date timestamp not null default now(),
  teampart_id integer not  null,
  title varchar(255) not null default '',
  author_id integer null,
  author_name varchar(255) not null,
  email varchar(255) not null default '',
  entry text not null default '',
  constraint t_teamblogentry_pk primary key (id)
);