alter table t_user drop refresh_token;
alter table t_user drop token_host;
alter table t_user add token VARCHAR(100) NOT NULL DEFAULT '';
