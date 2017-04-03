#from version 0.2 to version 0.3

alter table t_user add deleted int not null default 0;

alter table t_content add author_id int not null default 1;

alter table t_content add constraint t_content_fk2 foreign key (author_id) references t_user(id);