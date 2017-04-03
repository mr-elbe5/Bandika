create table t_id(
id int not null
);

insert into t_id (id) values(1000);

create table t_config(
config_key varchar(30) not null,
config_value varchar(255) not null
);

insert into t_config (config_key, config_value) values ("properties","bandika");
insert into t_config (config_key, config_value) values ("mailHost","localhost");
insert into t_config (config_key, config_value) values ("mailSender","me@myhost.tld");
insert into t_config (config_key, config_value) values ("locale","en");
insert into t_config (config_key, config_value) values ("dateFormat","dd-MM-yyyy");
insert into t_config (config_key, config_value) values ("timeFormat","HH:mm:ss.SSS");
insert into t_config (config_key, config_value) values ("defaultController","page");
insert into t_config (config_key, config_value) values ("initSequence","admin;menu");

create table t_field(
shortkey varchar(10) not null,
field_class varchar(255) null,
constraint t_field_pk primary key (shortkey)
);

insert into t_field(shortkey,field_class) values("textline","de.bandika.page.fields.TextLineField");
insert into t_field(shortkey,field_class) values("textarea","de.bandika.page.fields.TextAreaField");
insert into t_field(shortkey,field_class) values("html","de.bandika.page.fields.HtmlField");
insert into t_field(shortkey,field_class) values("image","de.bandika.page.fields.ImageField");
insert into t_field(shortkey,field_class) values("document","de.bandika.page.fields.DocumentField");
insert into t_field(shortkey,field_class) values("blog","de.bandika.page.fields.BlogField");
insert into t_field(shortkey,field_class) values("mail","de.bandika.page.fields.MailField");

create table t_bean(
shortkey varchar(10) not null,
bean_class varchar(255) null,
constraint t_bean_pk primary key (shortkey)
);

insert into t_bean(shortkey,bean_class) values("admin","de.bandika.admin.AdminBean");
insert into t_bean(shortkey,bean_class) values("user","de.bandika.user.UserBean");
insert into t_bean(shortkey,bean_class) values("right","de.bandika.user.RightBean");
insert into t_bean(shortkey,bean_class) values("comm","de.bandika.communication.CommunicationBean");
insert into t_bean(shortkey,bean_class) values("menu","de.bandika.menu.MenuBean");
insert into t_bean(shortkey,bean_class) values("page","de.bandika.page.PageBean");
insert into t_bean(shortkey,bean_class) values("doc","de.bandika.document.DocumentBean");
insert into t_bean(shortkey,bean_class) values("img","de.bandika.image.ImageBean");

create table t_controller(
shortkey varchar(10) not null,
controller_class varchar(255) not null,
constraint t_controller_pk primary key (shortkey)
);

insert into t_controller(shortkey,controller_class) values("admin","de.bandika.admin.AdminController");
insert into t_controller(shortkey,controller_class) values("user","de.bandika.user.UserController");
insert into t_controller(shortkey,controller_class) values("comm","de.bandika.communication.CommunicationController");
insert into t_controller(shortkey,controller_class) values("menu","de.bandika.menu.MenuController");
insert into t_controller(shortkey,controller_class) values("page","de.bandika.page.PageController");
insert into t_controller(shortkey,controller_class) values("doc","de.bandika.document.DocumentController");
insert into t_controller(shortkey,controller_class) values("img","de.bandika.image.ImageController");

create table t_adminlink(
name varchar(100) not null,
url varchar(255) not null,
ranking int not null default 0,
editor int not null default 0,
admin int not null default 0
);

insert into t_adminlink (name,url,ranking,admin) values("Configuration","/_jsp/adminConfigEdit.jsp?ctrl=admin&method=openEditConfig",0,1);
insert into t_adminlink (name,url,ranking,admin) values("Edit Images","/_jsp/imageEditAll.jsp?ctrl=img&method=openEditImages",1,1);
insert into t_adminlink (name,url,ranking,admin) values("Edit Documents","/_jsp/documentEditAll.jsp?ctrl=doc&method=openEditDocuments",2,1);
insert into t_adminlink (name,url,ranking,admin) values("Edit Users","/_jsp/userEditAll.jsp?ctrl=user&method=openEditUsers",3,1);
insert into t_adminlink (name,url,ranking,admin) values("Edit Groups","/_jsp/groupEditAll.jsp?ctrl=user&method=openEditGroups",4,1);
insert into t_adminlink (name,url,ranking,admin) values("Edit Caches","/_jsp/adminCacheEdit.jsp?ctrl=admin&method=openEditCaches",5,1);

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
in_menu int not null default 1,
author_id int not null default 1,
xml text,
constraint t_page_pk primary key (id),
constraint t_page_fk1 foreign key (parent_id) references t_page(id) on delete cascade,
constraint t_page_fk2 foreign key (author_id) references t_user(id),
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

create table t_group_right(
page_id int not null,
group_id int not null,
rights int not null default 0,
constraint t_group_right_pk primary key (page_id,group_id),
constraint t_group_right_fk1 foreign key (page_id) references t_page(id) on delete cascade,
constraint t_group_right_fk2 foreign key (group_id) references t_group(id) on delete cascade
);

insert  t_user (id,version,name,email,login,pwd,admin,editor) values(1,1,'Administrator','xxx@host.tld','admin','947KIBCXluXCWY2seZp7Zm4n424=',1,1);

insert into t_page (id,version,parent_id,ranking,name,description,keywords,state,restricted,in_menu,author_id) values(100,1,null,1,'Home','Home page','bandika cms',1,0,1,1);

commit;