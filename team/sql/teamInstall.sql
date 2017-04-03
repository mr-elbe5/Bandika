insert into t_module (name,properties,head_include_file) values('team','team','/_jsp/_master/include/teamhead.jsp');
drop table if exists t_teamblogentry;
drop table if exists t_teamfile_current;
drop table if exists t_teamfile_version;
drop table if exists t_teamfile;
create table t_teamfile(
id integer not null,
change_date timestamp not null default now(),
teampart_id integer not  null,
owner_id integer null,
owner_name varchar(255) not null,
checkout_id integer null,
checkout_name varchar(255) not null default '',
search_content text not null default '',
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
search_content text not null default '',
constraint t_teamblogentry_pk primary key (id)
);
insert into t_template (name,description,type_name,class_name,module_name) values('teamLayout','Layout with team parts','layout','de.bandika.team.TeamPageData','team');
insert into t_template (name,description,type_name,match_types,class_name,module_name) values('teamFilePart','Part with team files','part','team','de.bandika.team.file.TeamFilePartData','team');
insert into t_template (name,description,type_name,match_types,class_name,module_name) values('teamBlogPart','Part with team blog','part','team','de.bandika.team.blog.TeamBlogPartData','team');
insert into t_template (name,description,type_name,match_types,class_name,module_name) values('teamChatPart','Part with team chat','part','team','de.bandika.page.PagePartData','team');
insert into t_servlet (name,class_name,pattern,module_name) values ('teamFileServlet','de.bandika.team.file.TeamFileServlet','/_teamfile','team');
insert into t_servlet (name,class_name,pattern,module_name) values ('teamBlogServlet','de.bandika.team.blog.TeamBlogServlet','/_teamblog','team');
insert into t_servlet (name,class_name,pattern,module_name) values ('teamChatServlet','de.bandika.team.chat.TeamChatServlet','/_teamchat','team');
