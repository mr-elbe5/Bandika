alter table t_user drop column token;
alter table t_user drop column token_expiration;
alter table t_user add refresh_token VARCHAR(2000) NOT NULL DEFAULT '';
alter table t_user add token_host VARCHAR(500) NOT NULL DEFAULT '';