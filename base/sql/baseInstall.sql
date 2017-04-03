CREATE TABLE t_id (
  id INTEGER NOT NULL
);

CREATE TABLE t_module (
  name              VARCHAR(30)  NOT NULL,
  change_date       TIMESTAMP    NOT NULL DEFAULT now(),
  author_name       VARCHAR(255) NOT NULL DEFAULT 'system',
  dependencies      VARCHAR(255) NULL,
  properties        VARCHAR(255) NOT NULL DEFAULT '',
  head_include_file VARCHAR(255) NOT NULL DEFAULT '',
  install_files     TEXT         NOT NULL DEFAULT '',
  install_log       TEXT         NOT NULL DEFAULT '',
  CONSTRAINT t_module_pk PRIMARY KEY (name),
  CONSTRAINT t_module_fk1 FOREIGN KEY (dependencies) REFERENCES t_module (name)
);

CREATE TABLE t_configuration (
  config_key   VARCHAR(30)  NOT NULL,
  config_value VARCHAR(255) NOT NULL,
  module_name  VARCHAR(30)  NOT NULL,
  CONSTRAINT t_configuration_pk PRIMARY KEY (config_key),
  CONSTRAINT t_configuration_fk1 FOREIGN KEY (module_name) REFERENCES t_module (name) ON DELETE CASCADE
);

CREATE TABLE t_timer_task (
  name             VARCHAR(60)  NOT NULL,
  class_name       VARCHAR(255) NOT NULL,
  interval_type    INTEGER      NOT NULL,
  master_only      BOOLEAN      NOT NULL DEFAULT FALSE,
  execution_day    INTEGER      NOT NULL DEFAULT 0,
  execution_hour   INTEGER      NOT NULL DEFAULT 0,
  execution_minute INTEGER      NOT NULL DEFAULT 0,
  execution_second INTEGER      NOT NULL DEFAULT 0,
  last_execution   TIMESTAMP    NULL,
  active           BOOLEAN      NOT NULL DEFAULT TRUE,
  module_name      VARCHAR(30)  NOT NULL,
  CONSTRAINT t_timer_task_pk PRIMARY KEY (name),
  CONSTRAINT t_timer_task_fk1 FOREIGN KEY (module_name) REFERENCES t_module (name) ON DELETE CASCADE
);

CREATE TABLE t_rights_provider (
  name        VARCHAR(60)  NOT NULL,
  class_name  VARCHAR(255) NOT NULL,
  module_name VARCHAR(30)  NOT NULL,
  CONSTRAINT t_rights_provider_pk PRIMARY KEY (name),
  CONSTRAINT t_rights_provider_fk1 FOREIGN KEY (module_name) REFERENCES t_module (name) ON DELETE CASCADE
);

CREATE TABLE t_servlet (
  name        VARCHAR(60)  NOT NULL,
  change_date TIMESTAMP    NOT NULL DEFAULT now(),
  class_name  VARCHAR(255) NOT NULL,
  pattern     VARCHAR(60)  NOT NULL,
  startup     INTEGER      NOT NULL DEFAULT 0,
  module_name VARCHAR(30)  NOT NULL,
  CONSTRAINT t_servlet_pk PRIMARY KEY (name),
  CONSTRAINT t_servlet_fk1 FOREIGN KEY (module_name) REFERENCES t_module (name) ON DELETE CASCADE
);

CREATE TABLE t_initializable (
  class_name  VARCHAR(100) NOT NULL,
  method_name VARCHAR(50)  NOT NULL,
  ranking     INT          NOT NULL DEFAULT 0,
  module_name VARCHAR(30)  NOT NULL,
  CONSTRAINT t_initializable_pk PRIMARY KEY (class_name),
  CONSTRAINT t_initializable_fk1 FOREIGN KEY (module_name) REFERENCES t_module (name) ON DELETE CASCADE
);

CREATE TABLE t_template_type (
  name           VARCHAR(30) NOT NULL,
  change_date    TIMESTAMP   NOT NULL DEFAULT now(),
  template_path  VARCHAR(60) NOT NULL,
  template_level INTEGER     NOT NULL DEFAULT 0,
  module_name    VARCHAR(30) NOT NULL,
  CONSTRAINT t_template_type_pk PRIMARY KEY (name),
  CONSTRAINT t_template_type_fk1 FOREIGN KEY (module_name) REFERENCES t_module (name) ON DELETE CASCADE
);

CREATE TABLE t_template (
  name        VARCHAR(60)  NOT NULL,
  type_name   VARCHAR(30)  NOT NULL,
  match_types VARCHAR(30)  NOT NULL DEFAULT '',
  change_date TIMESTAMP    NOT NULL DEFAULT now(),
  description VARCHAR(255) NOT NULL DEFAULT '',
  class_name  VARCHAR(255) NOT NULL DEFAULT '',
  module_name VARCHAR(30)  NOT NULL DEFAULT '',
  CONSTRAINT t_template_pk PRIMARY KEY (name, type_name),
  CONSTRAINT t_template_fk1 FOREIGN KEY (type_name) REFERENCES t_template_type (name) ON DELETE CASCADE,
  CONSTRAINT t_template_fk2 FOREIGN KEY (module_name) REFERENCES t_module (name) ON DELETE CASCADE
);

CREATE TABLE t_jsp (
  name        VARCHAR(60)  NOT NULL,
  path        VARCHAR(255) NOT NULL,
  module_name VARCHAR(30)  NOT NULL,
  CONSTRAINT t_jsp_pk PRIMARY KEY (name),
  CONSTRAINT t_jsp_fk1 FOREIGN KEY (module_name) REFERENCES t_module (name) ON DELETE CASCADE
);

CREATE TABLE t_backend_link (
  link_key    VARCHAR(60)  NOT NULL,
  link        VARCHAR(255) NOT NULL,
  ranking     INTEGER      NOT NULL DEFAULT 0,
  module_name VARCHAR(30)  NOT NULL,
  CONSTRAINT t_backend_link_pk PRIMARY KEY (link_key),
  CONSTRAINT t_backend_link_fk1 FOREIGN KEY (module_name) REFERENCES t_module (name) ON DELETE CASCADE
);

CREATE TABLE t_backend_link_right (
  link_key VARCHAR(60) NOT NULL,
  group_id INTEGER     NOT NULL,
  CONSTRAINT t_backend_link_right_pk PRIMARY KEY (link_key, group_id),
  CONSTRAINT t_backend_link_right_fk1 FOREIGN KEY (link_key) REFERENCES t_backend_link (link_key) ON DELETE CASCADE
);

CREATE TABLE t_cluster (
  ipaddress   VARCHAR(30) NOT NULL,
  port        INTEGER     NOT NULL DEFAULT 0,
  active      BOOLEAN     NOT NULL DEFAULT FALSE,
  change_date TIMESTAMP   NOT NULL DEFAULT now(),
  CONSTRAINT t_cluster_pk PRIMARY KEY (ipaddress)
);

CREATE TABLE t_page (
  id                   INTEGER      NOT NULL,
  change_date          TIMESTAMP    NOT NULL DEFAULT now(),
  parent_id            INTEGER      NULL,
  ranking              INTEGER      NOT NULL DEFAULT 0,
  name                 VARCHAR(100) NOT NULL,
  path                 VARCHAR(100) NOT NULL,
  redirect_id          INTEGER      NOT NULL DEFAULT 0,
  description          VARCHAR(200) NOT NULL DEFAULT '',
  keywords             VARCHAR(500) NOT NULL DEFAULT '',
  master_template      VARCHAR(60)  NOT NULL,
  master_template_type VARCHAR(60)  NOT NULL DEFAULT 'master',
  layout_template      VARCHAR(60)  NOT NULL,
  layout_template_type VARCHAR(60)  NOT NULL DEFAULT 'layout',
  locale               VARCHAR(10)  NOT NULL DEFAULT '',
  inherits_locale      BOOLEAN      NOT NULL DEFAULT TRUE,
  locked               BOOLEAN      NOT NULL DEFAULT FALSE,
  restricted           BOOLEAN      NOT NULL DEFAULT FALSE,
  inherits_rights      BOOLEAN      NOT NULL DEFAULT TRUE,
  visible              BOOLEAN      NOT NULL DEFAULT TRUE,
  author_id            INTEGER      NOT NULL,
  author_name          VARCHAR(255) NOT NULL,
  CONSTRAINT t_page_pk PRIMARY KEY (id),
  CONSTRAINT t_page_fk1 FOREIGN KEY (parent_id) REFERENCES t_page (id) ON DELETE CASCADE,
  CONSTRAINT t_page_fk2 FOREIGN KEY (master_template, master_template_type) REFERENCES t_template (name, type_name) ON DELETE CASCADE,
  CONSTRAINT t_page_fk3 FOREIGN KEY (layout_template, layout_template_type) REFERENCES t_template (name, type_name) ON DELETE CASCADE,
  CONSTRAINT t_page_un4 UNIQUE (id, parent_id, name)
);

CREATE TABLE t_page_content (
  id             INTEGER      NOT NULL,
  version        INTEGER      NOT NULL DEFAULT 1,
  change_date    TIMESTAMP    NOT NULL DEFAULT now(),
  published      BOOLEAN      NOT NULL DEFAULT FALSE,
  author_id      INTEGER      NOT NULL,
  author_name    VARCHAR(255) NOT NULL,
  search_content TEXT         NOT NULL DEFAULT '',
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
  part_template_type VARCHAR(60) NOT NULL DEFAULT 'part',
  content            TEXT        NOT NULL DEFAULT '',
  CONSTRAINT t_page_part_pk PRIMARY KEY (id, version),
  CONSTRAINT t_page_part_fk1 FOREIGN KEY (page_id, version) REFERENCES t_page_content (id, version) ON DELETE CASCADE,
  CONSTRAINT t_page_part_fk2 FOREIGN KEY (part_template, part_template_type) REFERENCES t_template (name, type_name) ON DELETE CASCADE
);

CREATE TABLE t_page_usage (
  linked_page_id INTEGER NOT NULL,
  page_id        INTEGER NOT NULL,
  page_version   INTEGER NOT NULL,
  CONSTRAINT t_page_usage_pk PRIMARY KEY (linked_page_id, page_id, page_version),
  CONSTRAINT t_page_usage_fk1 FOREIGN KEY (linked_page_id) REFERENCES t_page (id) ON DELETE CASCADE,
  CONSTRAINT t_page_usage_fk2 FOREIGN KEY (page_id, page_version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);

CREATE TABLE t_page_right (
  page_id  INTEGER NOT NULL,
  group_id INTEGER NOT NULL,
  rights   INTEGER NOT NULL,
  CONSTRAINT t_page_right_pk PRIMARY KEY (page_id, group_id),
  CONSTRAINT t_page_right_fk1 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE
);

CREATE TABLE t_file_type (
  name                 VARCHAR(60)  NOT NULL,
  change_date          TIMESTAMP    NOT NULL DEFAULT now(),
  module_name          VARCHAR(30)  NOT NULL,
  class_name           VARCHAR(255) NOT NULL DEFAULT '',
  dimensioned          BOOLEAN      NOT NULL DEFAULT FALSE,
  content_type_pattern VARCHAR(255) NOT NULL DEFAULT '',
  CONSTRAINT t_file_type_pk PRIMARY KEY (name),
  CONSTRAINT t_file_type_fk1 FOREIGN KEY (module_name) REFERENCES t_module (name) ON DELETE CASCADE
);

CREATE TABLE t_file (
  id             INTEGER      NOT NULL,
  change_date    TIMESTAMP    NOT NULL DEFAULT now(),
  file_type      VARCHAR(60)  NOT NULL,
  file_name      VARCHAR(100) NOT NULL,
  name           VARCHAR(255) NOT NULL DEFAULT '',
  content_type   VARCHAR(255) NOT NULL DEFAULT '',
  file_size      INTEGER      NOT NULL DEFAULT 0,
  width          INTEGER      NOT NULL DEFAULT 0,
  height         INTEGER      NOT NULL DEFAULT 0,
  author_id      INTEGER      NOT NULL,
  author_name    VARCHAR(255) NOT NULL,
  locked         BOOLEAN      NOT NULL DEFAULT FALSE,
  page_id        INTEGER      NULL,
--postgres
  bytes          BYTEA        NOT NULL,
-- mysql
--  bytes LONGBLOB NOT NULL,
  search_content TEXT         NULL,
  CONSTRAINT t_file_pk PRIMARY KEY (id),
  CONSTRAINT t_file_fk1 FOREIGN KEY (file_type) REFERENCES t_file_type (name) ON DELETE CASCADE,
  CONSTRAINT t_file_fk2 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE
);

CREATE TABLE t_file_thumbnail (
  file_id      INTEGER      NOT NULL,
  change_date  TIMESTAMP    NOT NULL DEFAULT now(),
  file_name    VARCHAR(100) NOT NULL,
  content_type VARCHAR(255) NOT NULL DEFAULT '',
  file_size    INTEGER      NOT NULL DEFAULT 0,
  width        INTEGER      NOT NULL DEFAULT 0,
  height       INTEGER      NOT NULL DEFAULT 0,
-- postgres
  bytes        BYTEA        NOT NULL,
-- mysql
--  bytes BLOB NOT NULL,
  CONSTRAINT t_file_thumbnail_pk PRIMARY KEY (file_id),
  CONSTRAINT t_file_thumbnail_fk1 FOREIGN KEY (file_id) REFERENCES t_file (id) ON DELETE CASCADE
);

CREATE TABLE t_file_usage (
  file_id      INTEGER NOT NULL,
  page_id      INTEGER NOT NULL,
  page_version INTEGER NOT NULL,
  CONSTRAINT t_file_usage_pk PRIMARY KEY (file_id, page_id, page_version),
  CONSTRAINT t_file_usage_fk1 FOREIGN KEY (file_id) REFERENCES t_file (id) ON DELETE CASCADE,
  CONSTRAINT t_file_usage_fk2 FOREIGN KEY (page_id, page_version) REFERENCES t_page_content (id, version) ON DELETE CASCADE
);

-- inserts

INSERT INTO t_id (id) VALUES (1000);

INSERT INTO t_module (name) VALUES ('base');

INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('mailHost', 'localhost', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('mailSender', 'me@myhost.tld', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('stdLocale', 'en', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('allLocales', 'en,de', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('dateFormat', 'dd-MM-yyyy', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('timeFormat', 'hh:mm:ss', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('dateTimeFormat', 'dd-MM-yyyy hh:mm:ss', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('timerInterval', '30', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('adminMaster', 'adminMaster', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('layoutMaster', 'layoutMaster', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('userPopupMaster', 'userPopupMaster', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('adminPopupMaster', 'adminPopupMaster', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('sessionTimeout', '30', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('maxVersions', '5', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('clusterPort', '2731', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('clusterTimeout', '1000', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('maxClusterTimeouts', '5', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('applicationTitle', 'Bandika', 'base');
INSERT INTO t_configuration (config_key, config_value, module_name) VALUES ('showFirstMenuLevel', 'true', 'base');

INSERT INTO t_timer_task (name, class_name, interval_type, master_only, execution_minute, active, module_name) VALUES ('controlTask', 'de.bandika.timer.ControlTask', 0, FALSE, 2, FALSE, 'base');
INSERT INTO t_timer_task (name, class_name, interval_type, master_only, execution_minute, active, module_name) VALUES ('clusterTask', 'de.bandika.cluster.ClusterControlTask', 0, FALSE, 1, FALSE, 'base');

INSERT INTO t_rights_provider (name, class_name, module_name) VALUES ('page', 'de.bandika.page.PageRightsProvider', 'base');

INSERT INTO t_servlet (name, class_name, pattern, startup, module_name) VALUES ('applicationservlet', 'de.bandika.application.ApplicationServlet', '/_application', 10, 'base');
INSERT INTO t_servlet (name, class_name, pattern, module_name) VALUES ('fileservlet', 'de.bandika.file.FileServlet', '/_file', 'base');
INSERT INTO t_servlet (name, class_name, pattern, module_name) VALUES ('clusterservlet', 'de.bandika.cluster.ClusterServlet', '/_cluster', 'base');
INSERT INTO t_servlet (name, class_name, pattern, module_name) VALUES ('moduleservlet', 'de.bandika.module.ModuleServlet', '/_module', 'base');
INSERT INTO t_servlet (name, class_name, pattern, module_name) VALUES ('menuservlet', 'de.bandika.menu.MenuServlet', '/_menu', 'base');
INSERT INTO t_servlet (name, class_name, pattern, module_name) VALUES ('pageservlet', 'de.bandika.page.PageServlet', '/_page', 'base');
INSERT INTO t_servlet (name, class_name, pattern, module_name) VALUES ('htmlpageservlet', 'de.bandika.page.HtmlPageServlet', '*.html', 'base');
INSERT INTO t_servlet (name, class_name, pattern, module_name) VALUES ('templateservlet', 'de.bandika.template.TemplateServlet', '/_template', 'base');
INSERT INTO t_servlet (name, class_name, pattern, module_name) VALUES ('timerservlet', 'de.bandika.timer.TimerServlet', '/_timer', 'base');
INSERT INTO t_servlet (name, class_name, pattern, module_name) VALUES ('userservlet', 'de.bandika.user.UserServlet', '/_user', 'base');

INSERT INTO t_initializable (class_name, method_name, ranking, module_name) VALUES ('de.bandika.module.ModuleCache', 'getInstance', 10, 'base');
INSERT INTO t_initializable (class_name, method_name, ranking, module_name) VALUES ('de.bandika.application.JspCache', 'getInstance', 40, 'base');
INSERT INTO t_initializable (class_name, method_name, ranking, module_name) VALUES ('de.bandika.file.FileTypeCache', 'getInstance', 50, 'base');

INSERT INTO t_initializable (class_name, method_name, ranking, module_name) VALUES ('de.bandika.cluster.ClusterController', 'getInstance', 100, 'base');

INSERT INTO t_initializable (class_name, method_name, ranking, module_name) VALUES ('de.bandika.template.TemplateCache', 'getInstance', 200, 'base');
INSERT INTO t_initializable (class_name, method_name, ranking, module_name) VALUES ('de.bandika.file.FileCache', 'getInstance', 210, 'base');
INSERT INTO t_initializable (class_name, method_name, ranking, module_name) VALUES ('de.bandika.file.ThumbnailCache', 'getInstance', 220, 'base');
INSERT INTO t_initializable (class_name, method_name, ranking, module_name) VALUES ('de.bandika.rights.RightsCache', 'getInstance', 300, 'base');
INSERT INTO t_initializable (class_name, method_name, ranking, module_name) VALUES ('de.bandika.menu.MenuCache', 'getInstance', 310, 'base');
INSERT INTO t_initializable (class_name, method_name, ranking, module_name) VALUES ('de.bandika.timer.TimerCache', 'getInstance', 320, 'base');

INSERT INTO t_initializable (class_name, method_name, ranking, module_name) VALUES ('de.bandika.timer.TimerController', 'getInstance', 5000, 'base');

INSERT INTO t_template_type (name, template_path, template_level, module_name) VALUES ('master', '/_jsp/_master/', 1, 'base');
INSERT INTO t_template_type (name, template_path, template_level, module_name) VALUES ('layout', '/_jsp/_layout/', 2, 'base');
INSERT INTO t_template_type (name, template_path, template_level, module_name) VALUES ('part', '/_jsp/_part/', 3, 'base');

INSERT INTO t_template (name, description, type_name, module_name) VALUES ('adminMaster', 'Master template for administration', 'master', 'base');
INSERT INTO t_template (name, description, type_name, module_name) VALUES ('layoutMaster', 'Master template for frontend layouts', 'master', 'base');
INSERT INTO t_template (name, description, type_name, module_name) VALUES ('popupMaster', 'Master template for popup windows', 'master', 'base');
INSERT INTO t_template (name, description, type_name, class_name, module_name) VALUES ('areaLayout', 'Layout with one full size area', 'layout', 'de.bandika.page.PageData', 'base');
INSERT INTO t_template (name, description, type_name, match_types, class_name, module_name) VALUES ('htmlPart', 'Part with one HTML field', 'part', '', 'de.bandika.page.HtmlPartData', 'base');

INSERT INTO t_jsp (name, path, module_name) VALUES ('login', '/_jsp/user/login.jsp', 'base');
INSERT INTO t_jsp (name, path, module_name) VALUES ('changePassword', '/_jsp/user/changePassword.jsp', 'base');
INSERT INTO t_jsp (name, path, module_name) VALUES ('changeProfile', '/_jsp/user/changeProfile.jsp', 'base');
INSERT INTO t_jsp (name, path, module_name) VALUES ('systemMenu', '/_jsp/menu/systemmenu.jsp', 'base');
INSERT INTO t_jsp (name, path, module_name) VALUES ('serviceMenu', '/_jsp/menu/servicemenu.jsp', 'base');

INSERT INTO t_backend_link (link_key, link, ranking, module_name) VALUES ('link|images', '/_file?method=openEditFiles&type=image', 10, 'base');
INSERT INTO t_backend_link (link_key, link, ranking, module_name) VALUES ('link|documents', '/_file?method=openEditFiles&type=document', 20, 'base');
INSERT INTO t_backend_link (link_key, link, ranking, module_name) VALUES ('link|templates', '/_template?method=openEditTemplates', 30, 'base');
INSERT INTO t_backend_link (link_key, link, ranking, module_name) VALUES ('link|jsps', '/_application?method=openEditJsps', 35, 'base');
INSERT INTO t_backend_link (link_key, link, ranking, module_name) VALUES ('link|configuration', '/_application?method=openEditConfiguration', 40, 'base');
INSERT INTO t_backend_link (link_key, link, ranking, module_name) VALUES ('link|modules', '/_module?method=openEditModules', 50, 'base');
INSERT INTO t_backend_link (link_key, link, ranking, module_name) VALUES ('link|cluster', '/_cluster?method=openViewCluster', 60, 'base');
INSERT INTO t_backend_link (link_key, link, ranking, module_name) VALUES ('link|caches', '/_application?method=openCaches', 70, 'base');
INSERT INTO t_backend_link (link_key, link, ranking, module_name) VALUES ('link|timers', '/_timer?method=openEditTimerTasks', 80, 'base');
INSERT INTO t_backend_link (link_key, link, ranking, module_name) VALUES ('link|fileTree', '/_application?method=openFileTree', 90, 'base');
INSERT INTO t_backend_link (link_key, link, ranking, module_name) VALUES ('link|users', '/_user?method=openEditUsers', 200, 'base');
INSERT INTO t_backend_link (link_key, link, ranking, module_name) VALUES ('link|groups', '/_user?method=openEditGroups', 210, 'base');

INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|images', 2);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|images', 3);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|documents', 2);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|documents', 3);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|templates', 1);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|templates', 2);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|templates', 3);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|jsps', 1);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|configuration', 1);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|modules', 1);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|cluster', 1);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|caches', 1);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|timers', 1);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|fileTree', 1);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|users', 1);
INSERT INTO t_backend_link_right (link_key, group_id) VALUES ('link|groups', 1);

INSERT INTO t_file_type (name, class_name, dimensioned, content_type_pattern, module_name) VALUES ('document', 'de.bandika.file.DocumentData', FALSE, '', 'base');
INSERT INTO t_file_type (name, class_name, dimensioned, content_type_pattern, module_name) VALUES ('image', 'de.bandika.file.ImageData', TRUE, 'image/', 'base');

INSERT INTO t_page (id, parent_id, ranking, name, path, description, keywords, master_template, layout_template, locale, inherits_locale, restricted, inherits_rights, visible, author_id, author_name) VALUES (100, null, 1, 'Home', '', 'Home page', 'bandika java framework cms modular', 'layoutMaster', 'areaLayout', 'en', FALSE, FALSE, FALSE, TRUE, 1, 'system');
INSERT INTO t_page_content (id, version, author_id, author_name) VALUES (100, 1, 1, 'system');
INSERT INTO t_page_current (id, published_version, draft_version) VALUES (100, 1, null);

