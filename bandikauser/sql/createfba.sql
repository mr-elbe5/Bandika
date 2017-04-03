create table t_id(
id int not null
);

insert into t_id (id) values(1000);

create table t_group(
id int not null,
version int not null default 1,
name varchar(100) not null,
constraint t_group_pk primary key (id)
);

create table t_user(
id int not null,
version int not null default 1,
first_name varchar(100) not null default '',
last_name varchar(100) not null,
email varchar(200) null,
login varchar(30) not null,
pwd varchar(50) not null,
admin int not null default 0,
approval_code varchar(50) null,
approved int not null default 0,
failed_login_count int not null default 0,
locked int not null default 0,
deleted int not null default 0,
constraint t_user_pk primary key (id)
);

create table t_user_profile(
user_id int not null,
profile_key varchar(100) not null,
profile_value varchar(255) not null,
constraint t_user_profile_pk primary key (user_id),
constraint t_user_profile_fk1 foreign key (user_id) references t_user(id) on delete cascade
);

create table t_user2group(
user_id int not null,
group_id int not null,
constraint t_user2group_pk primary key (user_id,group_id),
constraint t_user2group_fk1 foreign key (user_id) references t_user(id) on delete cascade,
constraint t_user2group_fk2 foreign key (group_id) references t_group(id) on delete cascade
);

insert  t_group (id,version,name) values(1,1,'Editors');
insert  t_user (id,version,first_name,last_name,email,login,pwd,admin,approval_code,approved) values(1,1,'A.','Administrator','xxx@host.tld','admin','947KIBCXluXCWY2seZp7Zm4n424=',1,'',1);
insert  t_user2group (user_id,group_id) values(1,1);

commit;