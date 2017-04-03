create table t_id(
id int not null
);

insert into t_id (id) values(1000);

create table t_config(
config_key varchar(30) not null,
config_value varchar(255) not null
);

insert into t_config (config_key, config_value) values ("mailHost","localhost");
insert into t_config (config_key, config_value) values ("mailSender","me@myhost.tld");
insert into t_config (config_key, config_value) values ("locale","en");
insert into t_config (config_key, config_value) values ("dateFormat","dd-MM-yyyy");
insert into t_config (config_key, config_value) values ("timeFormat","HH:mm:ss.SSS");

create table t_template(
name varchar(100) not null,
description varchar(255) null,
html text null,
constraint t_template_pk primary key (name)
);

create table t_page(
id int not null,
version int not null default 1,
parent_id int null,
ranking int not null default 0,
name varchar(100) not null,
description varchar(200),
keywords varchar(500),
state int not null default 0,
restricted int not null default 0,
visible int not null default 1,
author_id int not null default 0,
author_name varchar(255) not null default "anonymous",
xml text,
constraint t_page_pk primary key (id),
constraint t_page_fk1 foreign key (parent_id) references t_page(id) on delete cascade,
constraint t_page_un2 unique (parent_id,name)
);

create table t_image(
id int not null,
version int not null,
image_name varchar(100),
content_type varchar(60),
img_size int default 0,
height int default 0,
width int default 0,
thumb_height int default 0,
thumb_width int default 0,
image longblob not null,
thumbnail blob,
constraint t_image_pk primary key (id)
);

create table t_image_usage(
image_id int not null,
page_id int not null,
constraint t_image_usage_pk primary key (image_id,page_id),
constraint t_image_usage_fk1 foreign key (image_id) references t_image(id) on delete cascade,
constraint t_image_usage_fk2 foreign key (page_id) references t_page(id) on delete cascade
);

create table t_document(
id int not null,
version int not null default 1,
document_name varchar(100),
content_type varchar(60),
doc_size int default 0,
document longblob not null,
constraint t_document_pk primary key (id)
);

create table t_document_usage(
document_id int not null,
page_id int not null,
constraint t_document_usage_pk primary key (document_id,page_id),
constraint t_document_usage_fk1 foreign key (document_id) references t_document(id) on delete cascade,
constraint t_document_usage_fk2 foreign key (page_id) references t_page(id) on delete cascade
);

create table t_app_group(
id int not null,
constraint t_app_group_pk primary key (id)
);

create table t_right(
page_id int not null,
group_id int not null,
rights int not null default 0,
constraint t_right_pk primary key (page_id,group_id),
constraint t_right_fk1 foreign key (page_id) references t_page(id) on delete cascade,
constraint t_right_fk2 foreign key (group_id) references t_app_group(id) on delete cascade
);

insert into t_page (id,version,parent_id,ranking,name,description,keywords,state,restricted,visible,author_id,author_name) values(100,1,null,1,'Home','Home page','bandika cms',1,0,1,1,'system');
insert into t_app_group(id) values(1);
insert into t_right(page_id,group_id,rights) values(100,1,2);

commit;