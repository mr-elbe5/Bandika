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

--

----------------------------------------------------------------------------------
DROP TABLE t_user2user;
DROP TABLE t_cluster;
DROP TABLE t_team_blog_entry;


delete from t_node_usage t1 where t1.page_version<>(select published_version from t_resource t2 where t2.id=t1.page_id);
delete from t_shared_part_usage t1 where t1.version<>(select published_version from t_resource t2 where t2.id=t1.page_id);
delete from t_page_part t1 where t1.version<>(select published_version from t_resource t2 where t2.id=t1.page_id);
delete from t_page_content t1 where t1.version<>(select published_version from t_resource t2 where t2.id=t1.id);

delete from t_file_content t1 where t1.version<>(select published_version from t_resource t2 where t2.id=t1.id);

ALTER TABLE t_shared_node_usage DROP CONSTRAINT t_shared_page_usage_pk;
ALTER TABLE t_shared_node_usage DROP CONSTRAINT t_shared_page_usage_fk1;
ALTER TABLE t_shared_node_usage DROP CONSTRAINT t_shared_page_usage_fk2;

ALTER TABLE t_node_usage DROP CONSTRAINT t_page_usage_pk;
ALTER TABLE t_node_usage DROP CONSTRAINT t_page_usage_fk1;
ALTER TABLE t_node_usage DROP CONSTRAINT t_page_usage_fk2;

ALTER TABLE t_node_usage DROP COLUMN page_version;

ALTER TABLE t_shared_part_usage DROP CONSTRAINT t_shared_part_usage_pk;
ALTER TABLE t_shared_part_usage DROP CONSTRAINT t_shared_part_usage_fk1;
ALTER TABLE t_shared_part_usage DROP CONSTRAINT t_shared_part_usage_fk2;

ALTER TABLE t_shared_part_usage DROP COLUMN version;

ALTER TABLE t_page_part DROP CONSTRAINT t_page_part_pk;
ALTER TABLE t_page_part DROP CONSTRAINT t_page_part_fk1;

ALTER TABLE t_page_part DROP COLUMN version;

ALTER TABLE t_page DROP CONSTRAINT t_page_fk1;
ALTER TABLE t_page_content DROP CONSTRAINT t_page_content_fk1;
ALTER TABLE t_page_content DROP CONSTRAINT t_page_content_pk;

ALTER TABLE t_page_content DROP COLUMN version;

ALTER TABLE t_file DROP CONSTRAINT t_file_fk1;
ALTER TABLE t_file_content DROP CONSTRAINT t_file_content_fk1;
ALTER TABLE t_file_content DROP CONSTRAINT t_file_content_pk;

ALTER TABLE t_file_content DROP COLUMN version;

DROP TABLE t_file;
ALTER TABLE t_file_content RENAME TO t_file;
ALTER TABLE t_file ADD keywords VARCHAR(500) NOT NULL DEFAULT '';
UPDATE t_file set keywords=(select keywords from t_resource where t_resource.id=t_file.id);
ALTER TABLE t_file DROP COLUMN published;

ALTER TABLE t_page ADD keywords VARCHAR(500) NOT NULL DEFAULT '';
UPDATE t_page set keywords=(select keywords from t_resource where t_resource.id=t_page.id);
ALTER TABLE t_page ADD change_date TIMESTAMP    NOT NULL DEFAULT now();
UPDATE t_page set change_date=(select change_date from t_page_content where t_page.id=t_page_content.id);
ALTER TABLE t_page ADD author_name VARCHAR(255) NULL;
UPDATE t_page set author_name=(select author_name from t_page_content where t_page.id=t_page_content.id);
ALTER TABLE t_page ALTER COLUMN author_name SET NOT NULL;

DROP TABLE t_resource;
DROP TABLE t_page_content;

ALTER TABLE t_file ADD CONSTRAINT t_file_pk PRIMARY KEY (id);
ALTER TABLE t_file ADD CONSTRAINT t_file_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE;

ALTER TABLE t_page ADD CONSTRAINT t_page_fk1 FOREIGN KEY (id) REFERENCES t_treenode (id) ON DELETE CASCADE;

ALTER TABLE t_page_part ADD CONSTRAINT t_page_part_pk PRIMARY KEY (id);

CREATE TABLE IF NOT EXISTS t_page_part2page (
  part_id     INTEGER     NOT NULL,
  page_id     INTEGER     NULL,
  section     VARCHAR(60) NOT NULL,
  ranking     INTEGER     NOT NULL DEFAULT 0,
  change_date TIMESTAMP   NOT NULL DEFAULT now(),
  CONSTRAINT t_page_part2page_pk PRIMARY KEY (part_id, page_id, section, ranking),
  CONSTRAINT t_page_part2page_fk1 FOREIGN KEY (part_id) REFERENCES t_page_part (id) ON DELETE CASCADE,
  CONSTRAINT t_page_part2page_fk2 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE
);

INSERT INTO t_page_part2page (part_id, page_id, section, ranking, change_date)
  SELECT id,page_id,section,ranking,change_date from t_page_part;

ALTER TABLE t_page_part ADD name VARCHAR(60) NOT NULL DEFAULT '';

INSERT INTO t_page_part (id,name,change_date,template_type,template,content)
  SELECT id,share_name,change_date,template_type,template,content from t_shared_page_part;

INSERT INTO t_page_part2page (part_id,page_id,section,ranking,change_date)
  SELECT part_id,page_id,section,ranking,change_date from t_shared_part_usage;

ALTER TABLE t_page_part DROP COLUMN page_id;
ALTER TABLE t_page_part DROP COLUMN section;
ALTER TABLE t_page_part DROP COLUMN ranking;

ALTER TABLE t_page_part DROP CONSTRAINT t_page_part_fk2;
ALTER TABLE t_page_part ADD CONSTRAINT t_page_part_fk1 FOREIGN KEY (template, template_type) REFERENCES t_template (name, type);

DROP TABLE t_shared_node_usage;
DROP TABLE t_shared_part_usage;
DROP TABLE t_shared_page_part;

ALTER TABLE t_node_usage DROP COLUMN page_version;

ALTER TABLE t_node_usage ADD CONSTRAINT t_page_usage_pk PRIMARY KEY (linked_node_id, page_id);
ALTER TABLE t_node_usage ADD CONSTRAINT t_page_usage_fk1 FOREIGN KEY (linked_node_id) REFERENCES t_treenode (id) ON DELETE CASCADE;
ALTER TABLE t_node_usage ADD CONSTRAINT t_page_usage_fk2 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE;

ALTER TABLE t_template DROP COLUMN data_type;
ALTER TABLE t_template DROP COLUMN dynamic;
ALTER TABLE t_template DROP COLUMN editable;

ALTER TABLE t_configuration DROP COLUMN clusterport;
ALTER TABLE t_configuration DROP COLUMN clustertimeout;
ALTER TABLE t_configuration DROP COLUMN maxclustertimeouts;
ALTER TABLE t_configuration DROP COLUMN maxversions;

CREATE OR REPLACE FUNCTION addPage (parentId INTEGER, pageId INTEGER, rank INTEGER, nodeName VARCHAR(100), displayName VARCHAR(100), pageTemplate VARCHAR(255)) RETURNS INTEGER AS $$
BEGIN
  INSERT INTO t_treenode (id, parent_id, ranking, name, display_name, description, owner_id, author_name, in_navigation, anonymous, inherits_rights)
  VALUES (pageId, parentId, rank, nodeName, displayName, '', 1, 'System', TRUE, TRUE, TRUE);
  INSERT INTO t_page (id, template, author_name)
  VALUES (pageId, pageTemplate, 'System');
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
  INSERT INTO t_page (id, template, author_name)
  VALUES (pageId, pageTemplate, 'System');
  RETURN siteId;
END;
$$ LANGUAGE plpgsql;








