create table t_id(
id int not null
);

insert into t_id (id) values(1000);

create table t_user(
id int not null,
version int not null default 1,
name varchar(100) not null,
email varchar(200) null,
login varchar(30) not null,
pwd varchar(50) not null,
admin int not null default 0,
editor int not null default 0,
deleted int not null default 0,
constraint t_user_pk primary key (id)
);

create table t_group(
id int not null,
version int not null default 1,
name varchar(100) not null,
constraint t_group_pk primary key (id)
);

create table t_user2group(
user_id int not null,
group_id int not null,
constraint t_user2group_pk primary key (user_id,group_id),
constraint t_user2group_fk1 foreign key (user_id) references t_user(id) on delete cascade,
constraint t_user2group_fk2 foreign key (group_id) references t_group(id) on delete cascade
);

create table t_template(
id int not null,
version int not null default 1,
description varchar(200) not null,
html text,
constraint t_template_pk primary key (id)
);

create table t_content(
id int not null,
version int not null default 1,
parent int null,
ranking int not null default 0,
name varchar(100) not null,
description varchar(200),
meta_keywords varchar(500),
restricted int not null,
state int not null default 0,
show_menu int not null default 1,
author_id int not null default 1,
constraint t_content_pk primary key (id),
constraint t_content_fk1 foreign key (parent) references t_content(id),
constraint t_content_fk2 foreign key (author_id) references t_user(id)
);

create table t_paragraph(
id int not null,
content_id int not null,
ranking int not null,
template_id int not null,
constraint t_paragraph_pk primary key (id),
constraint t_paragraph_fk1 foreign key (content_id) references t_content(id) on delete cascade,
constraint t_paragraph_fk2 foreign key (template_id) references t_template(id) on delete cascade
);

create table t_paragraph_field(
paragraph_id int not null,
field_name varchar(30) not null,
field_type varchar(20) not null,
xml text,
constraint t_paragraph_field_pk primary key (paragraph_id,field_name),
constraint t_paragraph_field_fk1 foreign key (paragraph_id) references t_paragraph(id) on delete cascade
);

create table t_image(
id int not null,
version int not null,
image_name varchar(100),
content_type varchar(60),
height int default 0,
width int default 0,
thumb_height int default 0,
thumb_width int default 0,
image longblob not null,
thumbnail blob,
content_id int null,
constraint t_image_pk primary key (id),
constraint t_image_fk1 foreign key (content_id) references t_content(id) on delete cascade
);

create table t_image_usage(
image_id int not null,
content_id int not null,
constraint t_image_usage_pk primary key (image_id,content_id),
constraint t_image_usage_fk1 foreign key (image_id) references t_image(id) on delete cascade,
constraint t_image_usage_fk2 foreign key (content_id) references t_content(id) on delete cascade
);

create table t_document(
id int not null,
version int not null default 1,
document_name varchar(100),
content_type varchar(60),
document longblob not null,
content_id int null,
constraint t_document_pk primary key (id),
constraint t_document_fk1 foreign key (content_id) references t_content(id) on delete cascade
);

create table t_document_usage(
document_id int not null,
content_id int not null,
constraint t_document_usage_pk primary key (document_id,content_id),
constraint t_document_usage_fk1 foreign key (document_id) references t_document(id) on delete cascade,
constraint t_document_usage_fk2 foreign key (content_id) references t_content(id) on delete cascade
);

create table t_group_right(
content_id int not null,
group_id int not null,
rights int not null default 0,
constraint t_group_right_pk primary key (content_id,group_id),
constraint t_group_right_fk1 foreign key (content_id) references t_content(id) on delete cascade,
constraint t_group_right_fk2 foreign key (group_id) references t_group(id) on delete cascade
);

create table t_user_right(
content_id int not null,
user_id int not null,
rights int not null default 0,
constraint t_user_right_pk primary key (content_id,user_id),
constraint t_user_right_fk1 foreign key (content_id) references t_content(id) on delete cascade,
constraint t_user_right_fk2 foreign key (user_id) references t_user(id) on delete cascade
);

insert  t_user (id,version,name,email,login,pwd,admin,editor) values(1,1,'Administrator','xxx@host.tld','admin','947KIBCXluXCWY2seZp7Zm4n424=',1,1);

insert into t_content (id,version,parent,ranking,name,meta_keywords,restricted,state,show_menu) values(100,1,null,0,'Home','',0,0,0);
insert into t_content (id,version,parent,ranking,name,meta_keywords,restricted,state,show_menu) values(110,1,100,0,'Imprint','',0,0,0);
insert into t_content (id,version,parent,ranking,name,meta_keywords,restricted,state,show_menu) values(111,1,100,0,'Contact','',0,0,0);

insert into t_content (id,version,parent,ranking,name,meta_keywords,restricted,state,show_menu) values(200,1,null,0,'Startseite','',0,0,0);
insert into t_content (id,version,parent,ranking,name,meta_keywords,restricted,state,show_menu) values(210,1,200,0,'Impressum','',0,0,0);
insert into t_content (id,version,parent,ranking,name,meta_keywords,restricted,state,show_menu) values(211,1,200,0,'Kontakt','',0,0,0);

commit;