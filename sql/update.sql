CREATE EXTENSION "uuid-ossp";
CREATE CAST (uuid AS varchar)
    WITH INOUT
    AS IMPLICIT;
ALTER TABLE t_company ADD uuid VARCHAR(40) NOT NULL DEFAULT uuid_generate_v4()::varchar;
ALTER TABLE t_content ADD uuid VARCHAR(40) NOT NULL DEFAULT uuid_generate_v4()::varchar;
ALTER TABLE t_file ADD uuid VARCHAR(40) NOT NULL DEFAULT uuid_generate_v4()::varchar;
ALTER TABLE t_group ADD uuid VARCHAR(40) NOT NULL DEFAULT uuid_generate_v4()::varchar;
ALTER TABLE t_user ADD uuid VARCHAR(40) NOT NULL DEFAULT uuid_generate_v4()::varchar;
ALTER TABLE t_page_part ADD uuid VARCHAR(40) NOT NULL DEFAULT uuid_generate_v4()::varchar;

UPDATE  t_user set uuid = '00000000-0000-0000-0000-000000000001' where id = 1;

UPDATE  t_content set uuid ='00000000-0000-0000-0001-000000000001' where parent_id is null;

ALTER TABLE t_content ADD parent_uuid VARCHAR(40);
UPDATE t_content t1 set parent_uuid = (select t2.uuid from t_content t2 where t1.parent_id = t2.id);

ALTER TABLE t_content ADD creator_uuid VARCHAR(40);
UPDATE t_content t1 set creator_uuid = (select t2.uuid from t_user t2 where t1.creator_id = t2.id);
ALTER TABLE t_content ADD changer_uuid VARCHAR(40);
UPDATE t_content t1 set changer_uuid = (select t2.uuid from t_user t2 where t1.changer_id = t2.id);

ALTER TABLE t_user ADD company_uuid VARCHAR(40);
UPDATE t_user set company_uuid = (select uuid from t_company where company_id = id);

ALTER TABLE t_content_right ADD content_uuid VARCHAR(40);
UPDATE t_content_right set content_uuid = (select uuid from t_content where content_id = id);
ALTER TABLE t_content_right ADD group_uuid VARCHAR(40);
UPDATE t_content_right set group_uuid = (select uuid from t_group where group_id = id);

ALTER TABLE t_file ADD parent_uuid VARCHAR(40);
UPDATE t_file t1 set parent_uuid = (select t2.uuid from t_content t2 where t1.parent_id = t2.id);
ALTER TABLE t_file ADD creator_uuid VARCHAR(40);
UPDATE t_file t1 set creator_uuid = (select t2.uuid from t_user t2 where t1.creator_id = t2.id);
ALTER TABLE t_file ADD changer_uuid VARCHAR(40);
UPDATE t_file t1 set changer_uuid = (select t2.uuid from t_user t2 where t1.changer_id = t2.id);

ALTER TABLE t_image ADD uuid VARCHAR(40);
UPDATE t_image t1 set uuid = (select t2.uuid from t_file t2 where t1.id = t2.id);

ALTER TABLE t_layout_part ADD uuid VARCHAR(40);
UPDATE t_layout_part set uuid = (select uuid from t_page_part where t_layout_part.id = id);

ALTER TABLE t_page ADD uuid VARCHAR(40);
UPDATE t_page set uuid = (select uuid from t_content where t_page.id = id);

ALTER TABLE t_page_part ADD page_uuid VARCHAR(40);
UPDATE t_page_part t1 set page_uuid = (select t2.uuid from t_page t2 where t1.page_id = t2.id);

ALTER TABLE t_part_field ADD part_uuid VARCHAR(40);
UPDATE t_part_field set part_uuid = (select uuid from t_page_part where t_part_field.part_id = id);

ALTER TABLE t_system_right ADD group_uuid VARCHAR(40);
UPDATE t_system_right t1 set group_uuid = (select t2.uuid from t_group t2 where t1.group_id = t2.id);

ALTER TABLE t_user2group ADD user_uuid VARCHAR(40);
UPDATE t_user2group set user_uuid = (select uuid from t_user where t_user2group.user_id = id);
ALTER TABLE t_user2group ADD group_uuid VARCHAR(40);
UPDATE t_user2group set group_uuid = (select uuid from t_group where t_user2group.group_id = id);

---

ALTER TABLE t_part_field DROP CONSTRAINT t_part_field_fk1;
ALTER TABLE t_part_field DROP CONSTRAINT t_part_field_pk;

ALTER TABLE t_layout_part DROP CONSTRAINT t_layout_part_fk1;
ALTER TABLE t_layout_part DROP CONSTRAINT t_layout_part_pk;

ALTER TABLE t_page_part DROP CONSTRAINT t_page_part_fk1;
ALTER TABLE t_page_part DROP CONSTRAINT t_page_part_pk;

ALTER TABLE t_page DROP CONSTRAINT t_page_fk1;
ALTER TABLE t_page DROP CONSTRAINT t_page_pk;

ALTER TABLE t_content_log DROP CONSTRAINT t_content_log_fk1;
ALTER TABLE t_content_log DROP CONSTRAINT t_content_log_pk;

ALTER TABLE t_content_right DROP CONSTRAINT t_content_right_fk2;
ALTER TABLE t_content_right DROP CONSTRAINT t_content_right_fk1;
ALTER TABLE t_content_right DROP CONSTRAINT t_content_right_pk;

ALTER TABLE t_image DROP CONSTRAINT t_image_fk1;
ALTER TABLE t_image DROP CONSTRAINT t_image_pk;

ALTER TABLE t_file DROP CONSTRAINT t_file_pk;

ALTER TABLE t_link DROP CONSTRAINT t_link_fk1;
ALTER TABLE t_link DROP CONSTRAINT t_link_pk;

ALTER TABLE t_content DROP CONSTRAINT t_content_un1;
ALTER TABLE t_content DROP CONSTRAINT t_content_fk3;
ALTER TABLE t_content DROP CONSTRAINT t_content_fk2;
ALTER TABLE t_content DROP CONSTRAINT t_content_fk1;
ALTER TABLE t_content DROP CONSTRAINT t_content_pk;

ALTER TABLE t_system_right DROP CONSTRAINT t_system_fk1;
ALTER TABLE t_system_right DROP CONSTRAINT t_system_right_pk;

ALTER TABLE t_user2group DROP CONSTRAINT t_user2group_pk;
ALTER TABLE t_user2group DROP CONSTRAINT t_user2group_fk1;
ALTER TABLE t_user2group DROP CONSTRAINT t_user2group_fk2;

ALTER TABLE t_user DROP CONSTRAINT t_user_fk1;
ALTER TABLE t_user DROP CONSTRAINT t_user_pk;

ALTER TABLE t_group DROP CONSTRAINT t_group_pk;

ALTER TABLE t_company DROP CONSTRAINT t_company_pk;

---

ALTER TABLE t_part_field drop column part_id;
ALTER TABLE t_part_field rename column part_uuid to part_id;
ALTER TABLE t_part_field add CONSTRAINT t_part_field_pk PRIMARY KEY (part_id, name);

ALTER TABLE t_layout_part drop column id;
ALTER TABLE t_layout_part rename column uuid to id;
ALTER TABLE t_layout_part add CONSTRAINT t_layout_part_pk PRIMARY KEY (id);

ALTER TABLE t_page_part drop column id;
ALTER TABLE t_page_part rename column uuid to id;
ALTER TABLE t_page_part drop column page_id;
ALTER TABLE t_page_part rename column page_uuid to page_id;
ALTER TABLE t_page_part add CONSTRAINT t_page_part_pk PRIMARY KEY (id);

ALTER TABLE t_page drop column id;
ALTER TABLE t_page rename column uuid to id;
ALTER TABLE t_page add CONSTRAINT t_page_pk PRIMARY KEY (id);

ALTER TABLE t_content_log drop column content_id;
ALTER TABLE t_content_log rename column content_uuid to content_id;
ALTER TABLE t_content_log add CONSTRAINT t_content_log_pk PRIMARY KEY (content_id, day);

ALTER TABLE t_content_right drop column content_id;
ALTER TABLE t_content_right rename column content_uuid to content_id;
ALTER TABLE t_content_right drop column group_id;
ALTER TABLE t_content_right rename column group_uuid to group_id;
ALTER TABLE t_content_right add CONSTRAINT t_content_right_pk PRIMARY KEY (content_id, group_id);

DROP  VIEW v_preview_file;

ALTER TABLE t_image drop column id;
ALTER TABLE t_image rename column uuid to id;
ALTER TABLE t_image add CONSTRAINT t_image_pk PRIMARY KEY (id);

ALTER TABLE t_file drop column id;
ALTER TABLE t_file rename column uuid to id;
ALTER TABLE t_file add CONSTRAINT t_file_pk PRIMARY KEY (id);

CREATE OR REPLACE VIEW v_preview_file as (
     select t_file.id,file_name,content_type,preview_bytes
     from t_file, t_image
     where t_file.id=t_image.id
);

ALTER TABLE t_link drop column id;
ALTER TABLE t_link rename column uuid to id;
ALTER TABLE t_link add CONSTRAINT t_link_pk PRIMARY KEY (id);

ALTER TABLE t_content drop column id;
ALTER TABLE t_content rename column uuid to id;
ALTER TABLE t_content drop column creator_id;
ALTER TABLE t_content rename column creator_uuid to creator_id;
ALTER TABLE t_content drop column changer_id;
ALTER TABLE t_content rename column changer_uuid to changer_id;
ALTER TABLE t_content drop column parent_id;
ALTER TABLE t_content rename column parent_uuid to parent_id;
ALTER TABLE t_content add CONSTRAINT t_content_pk PRIMARY KEY (id);

ALTER TABLE t_system_right drop column group_id;
ALTER TABLE t_system_right rename column group_uuid to group_id;
ALTER TABLE t_system_right add CONSTRAINT t_system_right_pk PRIMARY KEY (name, group_id);

ALTER TABLE t_user2group drop column user_id;
ALTER TABLE t_user2group rename column user_uuid to user_id;
ALTER TABLE t_user2group drop column group_id;
ALTER TABLE t_user2group rename column group_uuid to group_id;
ALTER TABLE t_user2group add CONSTRAINT t_user2group_pk PRIMARY KEY (user_id, group_id);

ALTER TABLE t_user drop column id;
ALTER TABLE t_user rename column uuid to id;
ALTER TABLE t_user drop column company_id;
ALTER TABLE t_user rename column company_uuid to company_id;
ALTER TABLE t_user add CONSTRAINT t_user_pk PRIMARY KEY (id);

ALTER TABLE t_group drop column id;
ALTER TABLE t_group rename column uuid to id;
ALTER TABLE t_group add CONSTRAINT t_group_pk PRIMARY KEY (id);

ALTER TABLE t_company drop column id;
ALTER TABLE t_company rename column uuid to id;
ALTER TABLE t_company add CONSTRAINT t_company_pk PRIMARY KEY (id);

ALTER TABLE t_user ADD CONSTRAINT t_user_fk1 FOREIGN KEY (company_id) REFERENCES t_company (id) ON DELETE SET NULL;

ALTER TABLE t_system_right ADD CONSTRAINT t_system_right_fk1 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE;

ALTER TABLE t_user2group ADD CONSTRAINT t_user2group_fk1 FOREIGN KEY (user_id) REFERENCES t_user (id) ON DELETE CASCADE;
ALTER TABLE t_user2group ADD CONSTRAINT t_user2group_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE;

ALTER TABLE t_user ADD CONSTRAINT t_user_fk1 FOREIGN KEY (company_id) REFERENCES t_company (id) ON DELETE SET NULL;

ALTER TABLE t_content_log ADD CONSTRAINT t_content_log_fk1 FOREIGN KEY (content_id) REFERENCES t_content (id) ON DELETE CASCADE;

ALTER TABLE t_content_right ADD CONSTRAINT t_content_right_fk1 FOREIGN KEY (content_id) REFERENCES t_content (id) ON DELETE CASCADE;
ALTER TABLE t_content_right ADD CONSTRAINT t_content_right_fk2 FOREIGN KEY (group_id) REFERENCES t_group (id) ON DELETE CASCADE;

ALTER TABLE t_image ADD CONSTRAINT t_image_fk1 FOREIGN KEY (id) REFERENCES t_file (id) ON DELETE CASCADE;

ALTER TABLE t_link ADD CONSTRAINT t_link_fk1 FOREIGN KEY (id) REFERENCES t_content (id) ON DELETE CASCADE;

ALTER TABLE t_content ADD CONSTRAINT t_content_fk1 FOREIGN KEY (parent_id) REFERENCES t_content (id) ON DELETE CASCADE;
ALTER TABLE t_content ADD CONSTRAINT t_content_fk2 FOREIGN KEY (creator_id) REFERENCES t_user (id) ON DELETE SET DEFAULT;
ALTER TABLE t_content ADD CONSTRAINT t_content_fk3 FOREIGN KEY (changer_id) REFERENCES t_user (id) ON DELETE SET DEFAULT;
ALTER TABLE t_content ADD CONSTRAINT t_content_un1 UNIQUE (id, parent_id, name);

ALTER TABLE t_part_field ADD CONSTRAINT t_part_field_fk1 FOREIGN KEY (part_id) REFERENCES t_page_part (id) ON DELETE CASCADE;

ALTER TABLE t_layout_part ADD CONSTRAINT t_layout_part_fk1 FOREIGN KEY (id) REFERENCES t_page_part (id) ON DELETE CASCADE;

ALTER TABLE t_page_part ADD CONSTRAINT t_page_part_fk1 FOREIGN KEY (page_id) REFERENCES t_page (id) ON DELETE CASCADE;

ALTER TABLE t_page ADD CONSTRAINT t_page_fk1 FOREIGN KEY (id) REFERENCES t_content (id) ON DELETE CASCADE;

