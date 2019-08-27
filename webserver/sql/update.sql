
--life here

alter table t_page add display_name      VARCHAR(100) NULL;
update t_page set display_name=name;
update t_page set name = '/' where id=1;
update t_page set name = (select locale from t_locale where t_page.id=t_locale.home_id) where parent_id=1;
alter table t_page alter column display_name set not null;

alter table t_locale drop column home_id;

alter table t_user drop column locale;

CREATE TABLE IF NOT EXISTS t_editor_page
(
    id            INTEGER      NOT NULL,
    content       TEXT         NULL,
    CONSTRAINT t_editor_page_pk PRIMARY KEY (id),
    CONSTRAINT t_editor_page_fk1 FOREIGN KEY (id) REFERENCES t_page (id) ON DELETE CASCADE
);