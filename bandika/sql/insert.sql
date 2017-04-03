--mod_sql

INSERT INTO t_id (id) VALUES (1000);


--mod_portal

INSERT INTO t_locale (locale) VALUES ('en');
INSERT INTO t_locale (locale) VALUES ('de');

INSERT INTO t_group (id, name) VALUES (1, 'Administrators');
INSERT INTO t_group (id, name) VALUES (2, 'Approvers');
INSERT INTO t_group (id, name) VALUES (3, 'Editors');
INSERT INTO t_group (id, name) VALUES (4, 'Readers');

-- admins: admin rights: application + user
INSERT INTO t_general_right (group_id, rights) VALUES (1, 3);

-- approvers: all rights
INSERT INTO t_general_page_right (group_id, rights) VALUES (2, 31);
-- editors: all rights except approve
INSERT INTO t_general_page_right (group_id, rights) VALUES (3, 23);
-- readers: read only
INSERT INTO t_general_page_right (group_id, rights) VALUES (4, 1);

INSERT INTO t_user (id, first_name, last_name, email, login, pwd, pkey, approval_code, approved) VALUES (1, 'Super', 'Administrator', 'xxx@host.tld', 'admin', 'pou1XdlVVfTX1z1yVRE25rQR3js=', 'EJRmwkgjB7c=', '', TRUE);

INSERT INTO t_user2locale (user_id, locale) VALUES (1,'en');

INSERT INTO t_configuration (config_key, config_value) VALUES ('mailHost', 'localhost');
INSERT INTO t_configuration (config_key, config_value) VALUES ('mailSender', 'me@myhost.tld');
INSERT INTO t_configuration (config_key, config_value) VALUES ('timerInterval', '30');
INSERT INTO t_configuration (config_key, config_value) VALUES ('maxVersions', '5');

INSERT INTO t_timer_task (name, class_name, interval_type, execution_minute, note_execution, active) VALUES ('controlTask', 'de.bandika.timer.ControlTask', 0, 2, FALSE, FALSE);
INSERT INTO t_timer_task (name, class_name, interval_type, execution_minute, note_execution, active) VALUES ('clusterTask', 'de.bandika.cluster.ClusterControlTask', 0, 1, FALSE, FALSE);

INSERT INTO t_master_template (name, description) VALUES ('layoutMaster', 'Master template for frontend layouts');
INSERT INTO t_master_template (name, description) VALUES ('popupMaster', 'Master template for user popup windows');

insert into t_layout_template (name,description,class_name) values('homeLayout','Home Page Layout','de.bandika.page.PageData');
insert into t_layout_template (name,description,class_name) values('indexLayout','Index Page Layout','de.bandika.page.PageData');
insert into t_layout_template (name,description,class_name) values('articleLayout','Article Page Layout','de.bandika.page.PageData');

insert into t_part_template (name,description,class_name,area_types) values('htmlPart','HTML Paragraph','de.bandika.cms.HtmlPartData','');
insert into t_part_template (name,description,class_name,area_types) values('h1','Main Headline','de.bandika.cms.CmsPartData','');
insert into t_part_template (name,description,class_name,area_types) values('h2','Sub Headline','de.bandika.cms.CmsPartData','');
insert into t_part_template (name,description,class_name,area_types) values('h3','Intermediate Headline','de.bandika.cms.CmsPartData','');
insert into t_part_template (name,description,class_name,area_types) values('htmlFull','Full HTML Editor','de.bandika.cms.CmsPartData','');
insert into t_part_template (name,description,class_name,area_types) values('htmlSmall','Resiricted HTML Editor','de.bandika.cms.CmsPartData','');
insert into t_part_template (name,description,class_name,area_types) values('image','Image','de.bandika.cms.CmsPartData','');
insert into t_part_template (name,description,class_name,area_types) values('legend','Legend Headline','de.bandika.cms.CmsPartData','');
insert into t_part_template (name,description,class_name,area_types) values('moreLink','Link on more...','de.bandika.cms.CmsPartData','');
insert into t_part_template (name,description,class_name,area_types) values('textLink','Text Link','de.bandika.cms.CmsPartData','');
insert into t_part_template (name,description,class_name,area_types) values('imageLink','Image Link','de.bandika.cms.CmsPartData','');
insert into t_part_template (name,description,class_name,area_types) values('textArea','Multiline Text','de.bandika.cms.CmsPartData','');
insert into t_part_template (name,description,class_name,area_types) values('textLine','Singleline Text','de.bandika.cms.CmsPartData','');

INSERT INTO t_page (id, name, path, master_template, layout_template, author_name) VALUES (100, 'Home', 'en', 'layoutMaster', 'homeLayout', 'system');
INSERT INTO t_page_content (id, version, author_name) VALUES (100, 1, 'system');
INSERT INTO t_page_current (id, published_version, draft_version) VALUES (100, 1, null);
INSERT INTO t_page_locale (id,locale) VALUES(100,'en');

INSERT INTO t_page (id, name, path, master_template, layout_template, author_name) VALUES (101, 'Home', 'de', 'layoutMaster', 'homeLayout', 'system');
INSERT INTO t_page_content (id, version, author_name) VALUES (101, 1, 'system');
INSERT INTO t_page_current (id, published_version, draft_version) VALUES (101, 1, null);
INSERT INTO t_page_locale (id,locale) VALUES(101,'de');

--mod_cluster
INSERT INTO t_configuration (config_key, config_value) VALUES ('clusterPort', '2731');
INSERT INTO t_configuration (config_key, config_value) VALUES ('clusterTimeout', '1000');
INSERT INTO t_configuration (config_key, config_value) VALUES ('maxClusterTimeouts', '5');

--mod_lucenesearch
insert into t_timer_task (name,class_name,interval_type,execution_minute,active) values ('searchIndexTask','de.bandika.search.SearchIndexTask',0,60,false);

--mod_team
insert into t_layout_template (name,description,class_name) values('teamLayout','Layout for team parts','de.bandika.team.TeamPageData');
insert into t_part_template (name,description,class_name) values('teamFilePart','Part with team files','de.bandika.team.file.TeamFilePartData');
insert into t_part_template (name,description,class_name) values('teamBlogPart','Part with team blog','de.bandika.team.blog.TeamBlogPartData');
insert into t_part_template (name,description,class_name) values('teamChatPart','Part with team chat','de.bandika.page.PagePartData');





