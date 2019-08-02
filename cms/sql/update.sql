
--life here

alter table t_page add display_name      VARCHAR(100) NULL;
update t_page set display_name=name;
update t_page set name = '/' where id=1;
update t_page set name = (select locale from t_locale where t_page.id=t_locale.home_id) where parent_id=1;
alter table t_page alter column display_name set not null;

alter table t_locale drop column home_id;

alter table t_user drop column locale;