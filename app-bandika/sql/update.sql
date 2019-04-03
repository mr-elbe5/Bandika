alter table t_page add search_content TEXT NOT NULL DEFAULT '';

alter table t_user add email_verified     BOOLEAN      NOT NULL DEFAULT FALSE;

update t_page set name=display_name;
alter table t_page drop column display_name;

alter table t_user2group drop column relation;

drop table t_user2user;

alter table t_locale drop column home;

alter table t_page alter column master drop not null;
alter table t_page alter column template drop not null;

update t_page set master=null, template=null where id=1;

alter table t_user2group drop CONSTRAINT t_user2group_pk;
alter table t_user2group add CONSTRAINT t_user2group_pk PRIMARY KEY (user_id, group_id);
alter table t_user2group drop column if exists relation;

drop table if exists t_user2user;